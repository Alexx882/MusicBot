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
package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.BotConfig;
import com.jagrosh.jmusicbot.playlist.IPlaylist;
import com.jagrosh.jmusicbot.playlist.PlaylistManager;
import com.jagrosh.jmusicbot.settings.SettingsProvider;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.jagrosh.jmusicbot.queue.FairQueue;
import com.jagrosh.jmusicbot.settings.Settings;

import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledExecutorService;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;

/**
 * handles all audio-related functionality FOR ONE GUILD
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class AudioHandler implements AudioSendHandler, AudioManager {
    private final FairQueue<QueuedTrack> queue = new FairQueue<>();
    private final List<AudioTrack> defaultQueue = new LinkedList<>();
    private final Set<String> votes = new HashSet<>();

    private final ScheduledExecutorService threadpool;
    private final BotConfig config;
    private final SettingsProvider settingsProvider;
    private final PlaylistManager playlistManager;

    private final AudioPlayerManager manager;
    private final AudioPlayer audioPlayer;
    private final Guild guild;

    public AudioHandler(ScheduledExecutorService threadpool, BotConfig config, SettingsProvider settingsProvider,
                        PlaylistManager playlistManager, AudioPlayerManager manager,
                        Guild guild, AudioPlayer player) {
        this.threadpool = threadpool;
        this.config = config;
        this.settingsProvider = settingsProvider;
        this.playlistManager = playlistManager;
        this.manager = manager;
        this.audioPlayer = player;
        this.guild = guild;
    }

    private AudioFrame lastFrame;

    @Override
    public void stop() {
        audioPlayer.stopTrack();
    }

    @Override
    public void clear() {
        queue.clear();
        defaultQueue.clear();
    }

    @Override
    public int addTrack(QueuedTrack qtrack) {
        if (audioPlayer.getPlayingTrack() == null) {
            audioPlayer.playTrack(qtrack.getTrack());
            return -1;
        } else
            return queue.add(qtrack);
    }

    @Override
    public int addTrackToFront(QueuedTrack qtrack) {
        if (audioPlayer.getPlayingTrack() == null) {
            audioPlayer.playTrack(qtrack.getTrack());
            return -1;
        } else {
            queue.addAt(0, qtrack);
            return 0;
        }
    }

    @Override
    public boolean playFromDefault() {
        if (!defaultQueue.isEmpty()) {
            audioPlayer.playTrack(defaultQueue.remove(0));
            return true;
        }

        Settings settings = settingsProvider.getSettings(guild.getIdLong());
        if (settings == null || settings.getDefaultPlaylist() == null)
            return false;

        IPlaylist playlist = playlistManager.getPlaylist(settings.getDefaultPlaylist());
        if (playlist == null || playlist.getItems().isEmpty())
            return false;

        playlist.loadTracks(
                manager,
                this::playTrackFromDefault,
                () -> loadingIsDone(playlist)
        );

        return true;
    }

    private void loadingIsDone(IPlaylist playlist) {
        if (playlist.getTracks().isEmpty() && !config.getStay())
            closeAudioConnection();
    }

    private void closeAudioConnection() {
        if (guild != null)
            threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
    }

    private void playTrackFromDefault(AudioTrack track) {
        if (audioPlayer.getPlayingTrack() == null)
            audioPlayer.playTrack(track);
        else
            defaultQueue.add(track);
    }

    @Override
    public FairQueue<QueuedTrack> getQueue() {
        return queue;
    }

    @Override
    public boolean isMusicPlaying(JDA jda) {
        return guild.getSelfMember().getVoiceState().inVoiceChannel() && audioPlayer.getPlayingTrack() != null;
    }

    public Set<String> getVotes() {
        return votes;
    }

    public AudioPlayer getPlayer() {
        return audioPlayer;
    }

    @Override
    public long getRequester() {
        if (audioPlayer.getPlayingTrack() == null || audioPlayer.getPlayingTrack().getUserData(Long.class) == null)
            return 0;
        return audioPlayer.getPlayingTrack().getUserData(Long.class);
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
