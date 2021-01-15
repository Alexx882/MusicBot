package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.BotConfig;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.function.Consumer;

public interface TrackResultHandlerFactory {

    /**
     * @param playlist playlist, the handler was create for. holds information about tracks.
     * @param doneCallback callback which should be called when the track-fetching is finished
     * @param successCallback callback with the result AudioTrack. called when the fetching was completed successfully
     * @param index track index of the playlist
     * @param config global configuration
     *
     * @return ResultHandler for the fetching of an AudioTrack
     */
    AudioLoadResultHandler createHandler(
            IPlaylist playlist,
            Runnable doneCallback,
            Consumer<AudioTrack> successCallback,
            int index,
            BotConfig config
    );


}
