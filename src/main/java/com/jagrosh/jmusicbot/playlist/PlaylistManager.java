package com.jagrosh.jmusicbot.playlist;

import java.io.IOException;
import java.util.List;

public interface PlaylistManager {

    /**
     * @return names of all playlists stored
     */
    List<String> getPlaylistNames();

    /**
     * add a new playlist to the bot
     *
     * @param name name the playlist will be referenced by later
     * @throws IOException if something went wrong while persisting the playlist
     */
    void createPlaylist(String name) throws IOException;

    /**
     * @param name name of the playlist
     * @throws IOException if something went wrong while deleting the playlist
     */
    void deletePlaylist(String name) throws IOException;

    /**
     * persist the playlist in some way (filesystem, ...)
     *
     * @param name name of the playlist
     * @param text content of the playlist
     * @throws IOException something went wrong during persisting
     */
    void writePlaylist(String name, String text) throws IOException;

    /**
     * @param name name of the playlist
     * @return playlist identified by its name
     */
    PlaylistLoader.Playlist getPlaylist(String name);


}
