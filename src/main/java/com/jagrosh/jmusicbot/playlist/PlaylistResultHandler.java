package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.BotConfig;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlaylistResultHandler implements AudioLoadResultHandler {

    private final Runnable callback;
    private final boolean shuffle;
    private final boolean isLastResult;
    private final BotConfig config;
    private final IPlaylist caller;
    private final Consumer<AudioTrack> consumer;

    private final int index;
    private final String item;

    public PlaylistResultHandler(IPlaylist caller, Runnable callback, Consumer<AudioTrack> consumer, int index, BotConfig config) {
        this.callback = callback;
        this.shuffle = caller.isShuffled();
        this.config = config;
        this.isLastResult = index == caller.getItems().size() - 1;
        this.caller = caller;
        this.consumer = consumer;
        this.index = index;
        this.item = caller.getItems().get(index);
    }

    private void done() {
        if (isLastResult) {
            if (shuffle)
                caller.shuffleTracks();
            if (callback != null)
                callback.run();
        }
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        if (config.isTooLong(audioTrack))
            caller.getErrors().add(
                    new PlaylistLoadError(
                            index,
                            item,
                            "This track is longer than the allowed maximum"
                    )
            );
        else {
            audioTrack.setUserData(0L);
            caller.getTracks().add(audioTrack);
            consumer.accept(audioTrack);
        }
        done();
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        if (audioPlaylist.isSearchResult()) {
            trackLoaded(audioPlaylist.getTracks().get(0));
        } else if (audioPlaylist.getSelectedTrack() != null) {
            trackLoaded(audioPlaylist.getSelectedTrack());
        } else {
            List<AudioTrack> loaded = new ArrayList<>(audioPlaylist.getTracks());
            if (shuffle)
                for (int first = 0; first < loaded.size(); first++) {
                    int second = (int) (Math.random() * loaded.size());
                    AudioTrack tmp = loaded.get(first);
                    loaded.set(first, loaded.get(second));
                    loaded.set(second, tmp);
                }
            loaded.removeIf(track -> config.isTooLong(track));
            loaded.forEach(at -> at.setUserData(0L));
            caller.getTracks().addAll(loaded);
            loaded.forEach(at -> consumer.accept(at));
        }
        done();
    }

    @Override
    public void noMatches() {
        caller.getErrors().add(new PlaylistLoadError(index, item, "No matches found."));
        done();
    }

    @Override
    public void loadFailed(FriendlyException e) {
        caller.getErrors().add(
                new PlaylistLoadError(
                        index,
                        item,
                        "Failed to load track: " + e.getLocalizedMessage()
                )
        );
        done();
    }
}
