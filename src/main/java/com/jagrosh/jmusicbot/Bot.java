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
package com.jagrosh.jmusicbot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jmusicbot.audio.*;
import com.jagrosh.jmusicbot.gui.GUI;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader;

import java.util.Objects;

import com.jagrosh.jmusicbot.playlist.PlaylistFileUtil;
import com.jagrosh.jmusicbot.settings.SettingsProvider;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
// TODO abstract as everyone needs it everywhere
public class Bot extends DefaultAudioPlayerManager implements JdaProvider, AudioPlayerManager {
    private final EventWaiter waiter;
    private final ScheduledExecutorService threadpool;
    private final BotConfig config;
    private final SettingsProvider settings;
    private final PlaylistLoader playlists;
    private final NowplayingHandler nowplaying;

    private boolean shuttingDown = false;
    private JDA jda;
    private GUI gui;

    public Bot(EventWaiter waiter, BotConfig config, SettingsProvider settings) {
        this.waiter = waiter;
        this.config = config;
        this.settings = settings;
        this.playlists = new PlaylistLoader(config, new PlaylistFileUtil(config));

        this.threadpool = Executors.newSingleThreadScheduledExecutor();

        AudioSourceManagers.registerRemoteSources(this);
        AudioSourceManagers.registerLocalSource(this);
        this.source(YoutubeAudioSourceManager.class).setPlaylistPageCount(10);

        this.nowplaying = new NowplayingHandler(
                settings,
                threadpool,
                config,
                this::resetGame);

        this.nowplaying.init(this);
    }

    public void resetGame() {
        Activity game = config.getGame() == null || config.getGame().getName().equalsIgnoreCase("none") ? null : config.getGame();
        if (!Objects.equals(jda.getPresence().getActivity(), game))
            jda.getPresence().setActivity(game);
    }

    public void shutdown() {
        if (shuttingDown)
            return;
        shuttingDown = true;
        threadpool.shutdownNow();
        if (jda.getStatus() != JDA.Status.SHUTTING_DOWN) {
            jda.getGuilds().stream().forEach(g ->
            {
                g.getAudioManager().closeAudioConnection();
                AudioHandler ah = (AudioHandler) g.getAudioManager().getSendingHandler();
                if (ah != null) {
                    ah.stopAndClear();
                    ah.getPlayer().destroy();
                    nowplaying.updateTopic(g.getIdLong(), ah, true);
                }
            });
            jda.shutdown();
        }
        if (gui != null)
            gui.dispose();
        System.exit(0);
    }

    public EventWaiter getWaiter() {
        return waiter;
    }

    public ScheduledExecutorService getThreadpool() {
        return threadpool;
    }

    public BotConfig getConfig() {
        return config;
    }

    public SettingsProvider getSettingsManager() {
        return settings;
    }

    public PlaylistLoader getPlaylistLoader() {
        return playlists;
    }

    public NowplayingHandler getNowplayingHandler() {
        return nowplaying;
    }

    public JDA getJDA() {
        return jda;
    }

    @Override
    public JDA getJda() {
        return jda;
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
        nowplaying.setJda(jda);
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public AudioHandler setUpHandler(Guild guild) {
        AudioHandler handler;
        if (guild.getAudioManager().getSendingHandler() == null) {
            AudioPlayer player = this.createPlayer();

            player.setVolume(getSettingsManager().getSettings(guild.getIdLong()).getVolume());

            handler = new AudioHandler(
                    threadpool,
                    config,
                    settings,
                    playlists,
                    this,
                    guild,
                    player
            );

            AudioEventHandler audioEventHandler = new AudioEventHandler(
                    guild.getIdLong(),
                    settings,
                    handler,
                    nowplaying,
                    () -> closeAudioConnection(guild),
                    config.getStay()
            );

            audioEventHandler.registerOnTrackStartCallback(() -> handler.getVotes().clear());

            player.addListener(audioEventHandler);
            guild.getAudioManager().setSendingHandler(handler);
        } else
            handler = (AudioHandler) guild.getAudioManager().getSendingHandler();
        return handler;
    }

    private void closeAudioConnection(Guild guild) {
        if (guild != null)
            threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
    }
}
