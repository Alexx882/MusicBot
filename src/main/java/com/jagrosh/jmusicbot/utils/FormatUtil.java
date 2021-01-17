/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.utils;

import java.util.List;

import com.jagrosh.jmusicbot.BotConfig;
import com.jagrosh.jmusicbot.JMusicBot;
import com.jagrosh.jmusicbot.audio.AudioManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class FormatUtil implements TextUtil {

    public static String formatTime(long duration) {
        if (duration == Long.MAX_VALUE)
            return "LIVE";
        long seconds = Math.round(duration / 1000.0);
        long hours = seconds / (60 * 60);
        seconds %= 60 * 60;
        long minutes = seconds / 60;
        seconds %= 60;
        return (hours > 0 ? hours + ":" : "") + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    public static String progressBar(double percent) {
        String str = "";
        for (int i = 0; i < 12; i++)
            if (i == (int) (percent * 12))
                // TODO extract into more readible constants class
                str += "\uD83D\uDD18"; // 🔘
            else
                str += "▬";
        return str;
    }

    // TODO extract into text util
    public static String volumeIcon(int volume) {
        if (volume == 0)
            return "\uD83D\uDD07"; // 🔇
        if (volume < 30)
            return "\uD83D\uDD08"; // 🔈
        if (volume < 70)
            return "\uD83D\uDD09"; // 🔉
        return "\uD83D\uDD0A";     // 🔊
    }

    public static String listOfTChannels(List<TextChannel> list, String query) {
        String out = " Multiple text channels found matching \"" + query + "\":";
        for (int i = 0; i < 6 && i < list.size(); i++)
            out += "\n - " + list.get(i).getName() + " (<#" + list.get(i).getId() + ">)";
        if (list.size() > 6)
            out += "\n**And " + (list.size() - 6) + " more...**";
        return out;
    }

    public static String listOfVChannels(List<VoiceChannel> list, String query) {
        String out = " Multiple voice channels found matching \"" + query + "\":";
        for (int i = 0; i < 6 && i < list.size(); i++)
            out += "\n - " + list.get(i).getName() + " (ID:" + list.get(i).getId() + ")";
        if (list.size() > 6)
            out += "\n**And " + (list.size() - 6) + " more...**";
        return out;
    }

    public static String listOfRoles(List<Role> list, String query) {
        String out = " Multiple text channels found matching \"" + query + "\":";
        for (int i = 0; i < 6 && i < list.size(); i++)
            out += "\n - " + list.get(i).getName() + " (ID:" + list.get(i).getId() + ")";
        if (list.size() > 6)
            out += "\n**And " + (list.size() - 6) + " more...**";
        return out;
    }

    // TODO write useful doc
    public static String filter(String input) {
        return input.replace("\u202E", "")
                .replace("@everyone", "@\u0435veryone") // cyrillic letter e
                .replace("@here", "@h\u0435re") // cyrillic letter e
                .trim();
    }

    @Override
    public String getTopicPattern(JDA jda, AudioManager audioManager) {
        AudioPlayer player = audioManager.getPlayer();

        if (audioManager.isMusicPlaying(jda)) {
            long userid = audioManager.getRequester();
            AudioTrack track = player.getPlayingTrack();
            String title = track.getInfo().title;
            if (title == null || title.equals("Unknown Title"))
                title = track.getInfo().uri;
            return "**" + title + "** [" + (userid == 0 ? "autoplay" : "<@" + userid + ">") + "]"
                    + "\n" + (player.isPaused() ? JMusicBot.PAUSE_EMOJI : JMusicBot.PLAY_EMOJI) + " "
                    + "[" + FormatUtil.formatTime(track.getDuration()) + "] "
                    + FormatUtil.volumeIcon(player.getVolume());
        } else return "No music playing " + JMusicBot.STOP_EMOJI + " " + FormatUtil.volumeIcon(player.getVolume());
    }

    @Override
    public Message getNowPlaying(JDA jda, AudioManager audioManager, BotConfig config, Guild guild) {
        AudioPlayer player = audioManager.getPlayer();

        if (audioManager.isMusicPlaying(jda)) {
            AudioTrack track = player.getPlayingTrack();
            MessageBuilder mb = new MessageBuilder();
            mb.append(FormatUtil.filter(config + " **Now Playing in " + guild.getSelfMember().getVoiceState().getChannel().getName() + "...**"));
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(guild.getSelfMember().getColor());

            long requester = audioManager.getRequester();

            if (requester != 0) {
                User u = guild.getJDA().getUserById(requester);
                if (u == null)
                    eb.setAuthor("Unknown (ID:" + requester + ")", null, null);
                else
                    eb.setAuthor(u.getName() + "#" + u.getDiscriminator(), null, u.getEffectiveAvatarUrl());
            }

            try {
                eb.setTitle(track.getInfo().title, track.getInfo().uri);
            } catch (Exception e) {
                eb.setTitle(track.getInfo().title);
            }

            if (track instanceof YoutubeAudioTrack && config.useNPImages()) {
                eb.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/mqdefault.jpg");
            }

            if (track.getInfo().author != null && !track.getInfo().author.isEmpty())
                eb.setFooter("Source: " + track.getInfo().author, null);

            double progress = (double) player.getPlayingTrack().getPosition() / track.getDuration();
            eb.setDescription((player.isPaused() ? JMusicBot.PAUSE_EMOJI : JMusicBot.PLAY_EMOJI)
                    + " " + FormatUtil.progressBar(progress)
                    + " `[" + FormatUtil.formatTime(track.getPosition()) + "/" + FormatUtil.formatTime(track.getDuration()) + "]` "
                    + FormatUtil.volumeIcon(player.getVolume()));

            return mb.setEmbed(eb.build()).build();
        } else return null;
    }

    @Override
    public Message getNoMusicPlaying(BotConfig config, Guild guild, AudioPlayer audioPlayer) {
        return new MessageBuilder()
                .setContent(FormatUtil.filter(config.getSuccess() + " **Now Playing...**"))
                .setEmbed(new EmbedBuilder()
                        .setTitle("No music playing")
                        .setDescription(JMusicBot.STOP_EMOJI + " " + FormatUtil.progressBar(-1) + " " + FormatUtil.volumeIcon(audioPlayer.getVolume()))
                        .setColor(guild.getSelfMember().getColor())
                        .build()).build();
    }
}
