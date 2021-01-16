package com.jagrosh.jmusicbot.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;
import java.util.function.Consumer;

public interface IPlaylist {

    /**
     * fetch all tracks and create audiotracks out of them
     *
     * @param manager  performs the fetching
     * @param consumer callback for what to do with each loaded track
     * @param callback callbock for when the laoding is done
     */
    void loadTracks(AudioPlayerManager manager, Consumer<AudioTrack> consumer, Runnable callback);

    void shuffleTracks();

    String getName();

    /**
     * @return all tracks in the playlist (real audio tracks, not just string identifiers)
     */
    List<AudioTrack> getTracks();

    List<PlaylistLoader.PlaylistLoadError> getErrors();
}
