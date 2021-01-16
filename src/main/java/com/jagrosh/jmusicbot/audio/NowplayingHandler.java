/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.BotConfig;
import com.jagrosh.jmusicbot.entities.Pair;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.settings.SettingsProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

/**
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class NowplayingHandler implements StatusMessageManager {

    private final SettingsProvider settingsProvider;
    private final BotConfig config;
    private final ScheduledExecutorService threadpool;

    private final JDA jda;
    private final Runnable finishedPlayingCallback;

    final HashMap<Long, Pair<Long, Long>> lastNP; // guild -> channel,message

    public NowplayingHandler(SettingsProvider settingsProvider, ScheduledExecutorService threadpool, BotConfig config,
                             JDA jda, Runnable finishedPlayingCallback) {
        this.settingsProvider = settingsProvider;
        this.threadpool = threadpool;
        this.config = config;
        this.jda = jda;
        this.finishedPlayingCallback = finishedPlayingCallback;
        this.lastNP = new HashMap<>();
    }

    public void init() {
        if (!config.useNPImages())
            threadpool.scheduleWithFixedDelay(() -> updateAll(), 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void setLastNPMessage(Message m) {
        lastNP.put(m.getGuild().getIdLong(), new Pair<>(m.getTextChannel().getIdLong(), m.getIdLong()));
    }

    @Override
    public void clearLastNPMessage(Guild guild) {
        lastNP.remove(guild.getIdLong());
    }

    private void updateAll() {
        Set<Long> toRemove = new HashSet<>();
        for (long guildId : lastNP.keySet()) {
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) {
                toRemove.add(guildId);
                continue;
            }
            Pair<Long, Long> pair = lastNP.get(guildId);
            TextChannel tc = guild.getTextChannelById(pair.getKey());
            if (tc == null) {
                toRemove.add(guildId);
                continue;
            }
            AudioHandler handler = (AudioHandler) guild.getAudioManager().getSendingHandler();
            Message msg = handler.getNowPlaying(jda);
            if (msg == null) {
                msg = handler.getNoMusicPlaying(jda);
                toRemove.add(guildId);
            }
            try {
                tc.editMessageById(pair.getValue(), msg).queue(m -> {
                }, t -> lastNP.remove(guildId));
            } catch (Exception e) {
                toRemove.add(guildId);
            }
        }
        toRemove.forEach(id -> lastNP.remove(id));
    }

    // "event"-based methods
    @Override
    public void onTrackUpdate(long guildId, AudioTrack track, AudioManager handler) {
        // TODO "mediator" pattern
        // update bot status if applicable
        if (config.getSongInStatus()) {
            if (track != null && jda.getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inVoiceChannel()).count() <= 1)
                jda.getPresence().setActivity(Activity.listening(track.getInfo().title));
            else
                finishedPlayingCallback.run();
        }

        // update channel topic if applicable
        updateTopic(guildId, handler, false);
    }

    @Override
    public void updateTopic(long guildId, AudioManager handler, boolean wait) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null)
            return;

        Settings settings = settingsProvider.getSettings(guildId);
        TextChannel tchan = settings.getTextChannel(guild);
        if (tchan != null && guild.getSelfMember().hasPermission(tchan, Permission.MANAGE_CHANNEL)) {
            String otherText;
            String topic = tchan.getTopic();
            if (topic == null || topic.isEmpty())
                otherText = "\u200B";
            else if (topic.contains("\u200B"))
                otherText = topic.substring(topic.lastIndexOf("\u200B"));
            else
                otherText = "\u200B\n " + topic;

            String text = config.createTextUtil().getTopicPattern(jda, handler) + otherText;

            if (!text.equals(tchan.getTopic())) {
                try {
                    // normally here if 'wait' was false, we'd want to queue, however,
                    // new discord ratelimits specifically limiting changing channel topics
                    // mean we don't want a backlog of changes piling up, so if we hit a
                    // ratelimit, we just won't change the topic this time
                    tchan.getManager().setTopic(text).complete(wait);
                } catch (PermissionException | RateLimitedException ignore) {
                }
            }
        }
    }

    @Override
    public void onMessageDelete(Guild guild, long messageId) {
        Pair<Long, Long> pair = lastNP.get(guild.getIdLong());
        if (pair == null)
            return;
        if (pair.getValue() == messageId)
            lastNP.remove(guild.getIdLong());
    }
}
