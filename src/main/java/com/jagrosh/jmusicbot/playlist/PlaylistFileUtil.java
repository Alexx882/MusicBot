package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.BotConfig;
import com.jagrosh.jmusicbot.utils.FileSystemManager;

public class PlaylistFileUtil implements FileSystemManager {
    private final BotConfig config;

    public PlaylistFileUtil(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getFolderName() {
        return config.getPlaylistsFolder();
    }
}
