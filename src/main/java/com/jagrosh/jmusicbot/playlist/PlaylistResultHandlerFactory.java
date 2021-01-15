package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.BotConfig;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.function.Consumer;

public class PlaylistResultHandlerFactory implements TrackResultHandlerFactory {
    @Override
    public AudioLoadResultHandler createHandler(IPlaylist playlist, Runnable doneCallback, Consumer<AudioTrack> successCallback, int index, BotConfig config) {
        return new PlaylistResultHandler(
                playlist,
                doneCallback,
                successCallback,
                index,
                config
        );
    }
}
