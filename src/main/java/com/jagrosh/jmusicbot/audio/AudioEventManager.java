package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.settings.SettingsProvider;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public interface AudioEventManager {
    SettingsProvider getSettingProvider();

    void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason);

    void onTrackStart(AudioPlayer player, AudioTrack track);

    long getGuildId();

    void registerOnTrackStartCallback(Runnable callback);
}
