package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.BotConfig;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlaylistManagerTest {

    private PlaylistManager playlistMgr;
    private static String testFolderPath = "./playlists_folder";

    @Before
    public void setup() throws IOException {
        playlistMgr = new PlaylistLoader(getSomeBotConfig());
        cleanup();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        if(Files.exists(Paths.get(testFolderPath)))
            FileUtils.deleteDirectory(new File(testFolderPath));
    }

    @Test
    public void test_createPlaylist_newPlaylistCreatedButNotFolder() throws IOException {
        String name = "playlist_name";

        // todo directory has to be created manually!!!
        Files.createDirectory(Paths.get(testFolderPath));
        playlistMgr.createPlaylist(name);

        Assert.assertTrue(Files.isDirectory(Paths.get(testFolderPath)));
        Assert.assertTrue(Files.isRegularFile(Paths.get(String.format("%s/%s.txt", testFolderPath, name))));
    }

    @Test
    public void test_getPlaylistNames() throws IOException {
        String[] names = {"playlist1", "playlist2"};

        Files.createDirectory(Paths.get(testFolderPath));
        for (String name : names) {
            playlistMgr.createPlaylist(name);
        }

        List<String> res_names = playlistMgr.getPlaylistNames();
        Assert.assertArrayEquals(names, res_names.toArray());
    }

    @Test
    public void test_writePlaylist_someTextContent() throws IOException {
        String playlistName = "playlist1";
        Files.createDirectory(Paths.get(testFolderPath));
        playlistMgr.createPlaylist(playlistName);

        String playlistContent = "Test123 File content";
        playlistMgr.writePlaylist(playlistName, playlistContent);

        Path playlistFile = Paths.get(String.format("%s/%s.txt", testFolderPath, playlistName));
        Assert.assertTrue(Files.isRegularFile(playlistFile));
        List<String> fileContent = Files.readAllLines(playlistFile);
        Assert.assertEquals(1, fileContent.size());
        Assert.assertEquals(playlistContent, fileContent.get(0));
    }

    private BotConfig getSomeBotConfig(){
        BotConfig mock = mock(BotConfig.class);
        when(mock.getPlaylistsFolder()).thenReturn(testFolderPath);
        return mock;
    }
}
