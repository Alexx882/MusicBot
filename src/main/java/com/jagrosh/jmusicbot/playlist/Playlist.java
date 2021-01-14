package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.BotConfig;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Playlist implements IPlaylist {

    private final String name;
    private final List<String> items;
    private final boolean shuffle;
    private final List<AudioTrack> tracks = new LinkedList<>();
    private final List<PlaylistLoadError> errors = new LinkedList<>();
    private boolean loaded = false;
    private final BotConfig config;

    public BotConfig getConfig() {
        return config;
    }

    Playlist(String name, List<String> items, boolean shuffle, BotConfig config) {
        this.name = name;
        this.items = items;
        this.shuffle = shuffle;
        this.config = config;
    }

    @Override
    public void loadTracks(AudioPlayerManager manager, Consumer<AudioTrack> consumer, Runnable callback) {
        if (loaded)
            return;
        loaded = true;
        for (int i = 0; i < items.size(); i++)
            manager.loadItemOrdered(name, items.get(i),
                    new PlaylistResultHandler(
                            this,
                            callback,
                            consumer,
                            i,
                            config
                    )
            );
    }

    @Override
    public void shuffleTracks() {
        shuffle(tracks);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getItems() {
        return items;
    }

    @Override
    public List<AudioTrack> getTracks() {
        return tracks;
    }

    @Override
    public List<PlaylistLoadError> getErrors() {
        return errors;
    }

    @Override
    public boolean isShuffled() {
        return shuffle;
    }

    private <T> void shuffle(List<T> list) {
        for (int first = 0; first < list.size(); first++) {
            int second = (int) (Math.random() * list.size());
            T tmp = list.get(first);
            list.set(first, list.get(second));
            list.set(second, tmp);
        }
    }
}
