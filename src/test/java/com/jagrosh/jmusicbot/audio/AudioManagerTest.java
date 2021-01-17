package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.settings.SettingsManager;
import com.jagrosh.jmusicbot.settings.SettingsProvider;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.entities.GuildImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class AudioManagerTest {

    AudioManager audioManager;
    AudioTrack track;
    AudioPlayer player;
    JDA jda;

    long guildId = 0L;

    private Guild getMockGuild() {
        Guild mockGuild = mock(Guild.class, RETURNS_DEEP_STUBS);
        when(mockGuild.getIdLong()).thenReturn(guildId);
        when(mockGuild.getSelfMember().getVoiceState().inVoiceChannel()).thenReturn(true);
        return mockGuild;
    }

    @Before
    public void Setup() {
        track = getSomeTrack();
        player = getMockedPlayer();
        jda = getMockedJDA();

        audioManager = new AudioHandler(
                null,
                null,
                null,
                null,
                null,
                getMockGuild(),
                player
        );
    }

    @Test
    public void test_isMusicPlaying_initiallyFalse() {
        Guild guild = new GuildImpl(null, 0);
        Assert.assertFalse(audioManager.isMusicPlaying(jda));
    }

    @Test
    public void test_addTrack_musicIsPlaying() {
        audioManager.addTrack(new QueuedTrack(track, 0));

        ArgumentCaptor<AudioTrack> trackCaptor = ArgumentCaptor.forClass(AudioTrack.class);
        verify(player).playTrack(trackCaptor.capture());
        playingTrack = trackCaptor.getValue();

        Assert.assertTrue(audioManager.isMusicPlaying(jda));
    }

    @Test
    public void test_addTrackTwice_queueSize() {
        audioManager.addTrack(new QueuedTrack(track, 0));

        ArgumentCaptor<AudioTrack> trackCaptor = ArgumentCaptor.forClass(AudioTrack.class);
        verify(player).playTrack(trackCaptor.capture());
        playingTrack = trackCaptor.getValue();

        audioManager.addTrack(new QueuedTrack(track, 0));
        assertEquals(1, audioManager.getQueue().size());

        Assert.assertTrue(audioManager.isMusicPlaying(jda));
    }

    @Test
    public void test_addTrackToFront_musicIsPlaying() {
        audioManager.addTrackToFront(new QueuedTrack(track, 0));

        ArgumentCaptor<AudioTrack> trackCaptor = ArgumentCaptor.forClass(AudioTrack.class);
        verify(player).playTrack(trackCaptor.capture());
        playingTrack = trackCaptor.getValue();

        Assert.assertTrue(audioManager.isMusicPlaying(jda));
    }

    @Test
    public void test_addTrackToFrontTwice_queueSize() {
        audioManager.addTrackToFront(new QueuedTrack(track, 0));

        ArgumentCaptor<AudioTrack> trackCaptor = ArgumentCaptor.forClass(AudioTrack.class);
        verify(player).playTrack(trackCaptor.capture());
        playingTrack = trackCaptor.getValue();

        audioManager.addTrackToFront(new QueuedTrack(track, 0));
        assertEquals(1, audioManager.getQueue().size());

        Assert.assertTrue(audioManager.isMusicPlaying(jda));
    }

    @Test
    public void test_stopAndClear_isNotPlayingAfterwards() {
        audioManager.addTrack(new QueuedTrack(track, 0));
        ArgumentCaptor<AudioTrack> trackCaptor = ArgumentCaptor.forClass(AudioTrack.class);
        verify(player).playTrack(trackCaptor.capture());
        playingTrack = trackCaptor.getValue();

        Assert.assertTrue(audioManager.isMusicPlaying(jda));

        audioManager.stopAndClear();
        verify(player).stopTrack();
    }

    private SettingsProvider getSomeSettingProvider() {
        SettingsProvider settingsProvider = mock(SettingsManager.class, RETURNS_DEEP_STUBS);
        when(settingsProvider.getSettings(guildId).getRepeatMode()).thenReturn(true);
        return settingsProvider;
    }

    @Test
    public void test_playFromDefault_butNoDefaultPlaylist() {
        SettingsProvider provider = getSomeSettingProvider();

        AudioManager manager = audioManager = new AudioHandler(
                null,
                null,
                provider,
                null,
                null,
                getMockGuild(),
                player
        );

        when(provider.getSettings(guildId).getDefaultPlaylistName()).thenReturn(null);
        assertFalse(manager.playFromDefault());
    }

    @Test
    public void test_playFromDefault() {
        SettingsProvider provider = getSomeSettingProvider();

        AudioManager manager = audioManager = new AudioHandler(
                null,
                null,
                provider,
                null,
                null,
                getMockGuild(),
                player
        );

        when(provider.getSettings(guildId).getDefaultPlaylistName()).thenReturn(null);
        assertFalse(manager.playFromDefault());
    }


    private AudioTrack getSomeTrack() {
        AudioTrack mockedTrack = mock(AudioTrack.class);
        return mockedTrack;
    }

    AudioTrack lastTrack = null;

    AudioTrack playingTrack = null;

    private AudioPlayer getMockedPlayer() {
        AudioPlayer mockedPlayer = mock(AudioPlayer.class);
        when(mockedPlayer.getPlayingTrack()).thenAnswer(
                (Answer<AudioTrack>) invocationOnMock -> playingTrack
        );
        return mockedPlayer;
    }

    private JDA getMockedJDA() {
        JDA mock = mock(JDA.class, RETURNS_DEEP_STUBS);
        when(mock.getGuildById(anyInt()).getSelfMember().getVoiceState().inVoiceChannel())
                .thenReturn(true);
        return mock;
    }
}
