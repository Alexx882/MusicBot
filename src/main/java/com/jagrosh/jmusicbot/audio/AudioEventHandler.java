package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.settings.SettingsProvider;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.LinkedList;
import java.util.List;

public class AudioEventHandler extends AudioEventAdapter implements AudioEventManager {

    private final Long guildId;
    private final SettingsProvider settingsProvider;
    private final StatusMessageManager statusMessageManager;
    private final Runnable onLeaveCallBack;
    private final AudioManager audioManager;

    private final boolean stay;

    private final List<Runnable> onTrackStartCallbacks = new LinkedList<>();

    public AudioEventHandler(Long guildId, SettingsProvider settingsProvider, AudioManager audioManager,
                             StatusMessageManager statusMessageManager, Runnable onLeaveCallBack, boolean stay) {
        this.guildId = guildId;
        this.settingsProvider = settingsProvider;
        this.audioManager = audioManager;
        this.statusMessageManager = statusMessageManager;
        this.onLeaveCallBack = onLeaveCallBack;
        this.stay = stay;
    }

    @Override
    public void registerOnTrackStartCallback(Runnable callback) {
        onTrackStartCallbacks.add(callback);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // if the track ended normally, and we're in repeat mode, re-add it to the queue
        if (endReason == AudioTrackEndReason.FINISHED && settingsProvider.getSettings(guildId).getRepeatMode()) {
            audioManager.addTrack(new QueuedTrack(track.makeClone(), track.getUserData(Long.class) == null ? 0L : track.getUserData(Long.class)));
        }

        if (audioManager.getQueue().isEmpty()) {
            if (!audioManager.playFromDefault()) {
                statusMessageManager.onTrackUpdate(guildId, null, audioManager);
                if (!stay)
                    onLeaveCallBack.run();

                // unpause, in the case when the player was paused and the track has been skipped.
                // this is to prevent the player being paused next time it's being used.
                player.setPaused(false);
            }
        } else {
            QueuedTrack qt = audioManager.getQueue().pull();
            player.playTrack(qt.getTrack());
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        for (Runnable runnable : onTrackStartCallbacks)
            runnable.run();

        statusMessageManager.onTrackUpdate(guildId, track, audioManager);
    }

    @Override
    public long getGuildId() {
        return guildId;
    }

    @Override
    public SettingsProvider getSettingProvider() {
        return settingsProvider;
    }
}
