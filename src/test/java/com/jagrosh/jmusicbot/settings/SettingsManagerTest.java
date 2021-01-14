package com.jagrosh.jmusicbot.settings;

import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

public class SettingsManagerTest {

    private SettingsManager settingsManager;
    private static String settingsFile ="serversettings.json";

    @Before
    public void before() throws IOException {
        settingsManager = new SettingsManagerTestSubclass();
        cleanup();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        if(Files.exists(Paths.get(settingsFile)))
            Files.delete(Paths.get(settingsFile));
    }

    @Test
    public void test_writeSettings_defaultValues_emptyJson() throws IOException {
        Settings s = settingsManager.createDefaultSettings();
        ((SettingsManagerTestSubclass) settingsManager).setSettings(1L, s);

        settingsManager.writeSettings();

        Path file = Paths.get(settingsFile);
        Assert.assertTrue(Files.isRegularFile(file));
        List<String> fileContent = Files.readAllLines(file);
        Assert.assertEquals(1, fileContent.size());
        Assert.assertEquals("{\"1\": {}}", fileContent.get(0));
    }

    @Test
    public void test_writeSettings_changedOneValue_persistedInJson() throws IOException {
        Settings s = settingsManager.createDefaultSettings();
        s.setTextChannel(getSomeChannel(4L));
        ((SettingsManagerTestSubclass) settingsManager).setSettings(1L, s);

        settingsManager.writeSettings();

        Path file = Paths.get(settingsFile);
        Assert.assertTrue(Files.isRegularFile(file));
        List<String> fileContent = Files.readAllLines(file);
        Assert.assertEquals(1, fileContent.size());
        Assert.assertEquals("{\"1\": {\"text_channel_id\": \"4\"}}", fileContent.get(0));
    }

    @Test
    public void test_writeSettings_changedMultipleValues_persistedInJson() throws IOException {
        Settings s = settingsManager.createDefaultSettings();
        s.setTextChannel(getSomeChannel(4L));
        s.setVolume(7);
        s.setDefaultPlaylist("1 Nice Playlist");
        s.setRepeatMode(false); // still default
        ((SettingsManagerTestSubclass) settingsManager).setSettings(1L, s);

        settingsManager.writeSettings();

        Path file = Paths.get(settingsFile);
        Assert.assertTrue(Files.isRegularFile(file));
        List<String> fileContent = Files.readAllLines(file);
        List<String> expFileContent = Arrays.asList(
                ("{\"1\": {\n" +
                "    \"volume\": 7,\n" +
                "    \"text_channel_id\": \"4\",\n" +
                "    \"default_playlist\": \"1 Nice Playlist\"\n" +
                "}}").split("\n")
        );

        Assert.assertEquals(expFileContent, fileContent);
    }

    private TextChannel getSomeChannel(long id){
        TextChannel mock = mock(TextChannel.class);
        when(mock.getIdLong()).thenReturn(id);
        return mock;
    }

}
