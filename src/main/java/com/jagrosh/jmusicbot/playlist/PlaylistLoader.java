/*
 * Copyright 2018 John Grosh (jagrosh).
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
package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.BotConfig;
import com.jagrosh.jmusicbot.utils.FileSystemManager;
import com.jagrosh.jmusicbot.utils.OtherUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class PlaylistLoader implements PlaylistManager {
    private final BotConfig config;
    private final FileSystemManager fileManager;

    public FileSystemManager getFileManager() {
        return fileManager;
    }

    public PlaylistLoader(BotConfig config, FileSystemManager fileManager) {
        this.config = config;
        this.fileManager = fileManager;
    }

    @Override
    public List<String> getPlaylistNames() {
        return fileManager.getFilesOfType(".txt");
    }

    @Override
    public void createPlaylist(String name) throws IOException {
        fileManager.createFile(name + ".txt");
    }

    @Override
    public void deletePlaylist(String name) throws IOException {
        fileManager.deleteFile(name + ".txt");
    }

    @Override
    public void writePlaylist(String name, String text) throws IOException {
        fileManager.createFile(name + ".txt", text.trim().getBytes());
    }

    /**
     * @param input string to check
     * @return true iff the string is either #shuffle or //shuffle independent of the case and whitespace
     */
    private boolean isShuffleString(String input) {
        if (input.startsWith("#") || input.startsWith("//")) {
            input = input.replaceAll("\\s+", "");
            return input.equalsIgnoreCase("#shuffle") || input.equalsIgnoreCase("//shuffle");
        }

        return false;
    }

    @Override
    public Playlist getPlaylist(String name) {
        if (!getPlaylistNames().contains(name))
            return null;
        try {
            if (fileManager.folderExists()) {
                final boolean[] shuffle = {false};
                List<String> list = new ArrayList<>();
                fileManager.readAllLinesOfFile(name + ".txt")
                        .stream()
                        .filter((string) -> !string.trim().isEmpty())
                        .forEach(str ->
                        {
                            String s = str.trim();

                            if (isShuffleString(s))
                                shuffle[0] = true;
                            else
                                list.add(s);
                        });
                if (shuffle[0])
                    Collections.shuffle(list);

                // TODO move to global DI solution
                return new Playlist(name, list, shuffle[0], config, new PlaylistResultHandlerFactory());
            } else {
                fileManager.createFolder();
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
