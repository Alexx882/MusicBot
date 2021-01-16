package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.BotConfig;
import com.jagrosh.jmusicbot.utils.FileSystemManager;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class FileSystemManagerTest {

    private FileSystemManager fsManager;
    private static String testFolderPath = "./test_folder";

    @Before
    public void setup() throws IOException {
        fsManager = new PlaylistFileUtil(getSomeBotConfig());
        cleanup();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        Path folder = Paths.get(testFolderPath);
        if(Files.exists(folder))
            Files.delete(folder);
    }

    @Test
    public void test_createFolder() throws IOException {
        fsManager.createFolder();

        Assert.assertTrue(Files.isDirectory(Paths.get(testFolderPath)));
    }

    @Test
    public void test_folderExists_initiallyDoesNotExist(){
        Assert.assertFalse(fsManager.folderExists());
    }

    @Test
    public void test_folderExists_afterCreation(){
        fsManager.createFolder();

        Assert.assertTrue(fsManager.folderExists());
    }

    private BotConfig getSomeBotConfig(){
        BotConfig mock = mock(BotConfig.class);
        when(mock.getPlaylistsFolder()).thenReturn(testFolderPath);
        return mock;
    }

}

