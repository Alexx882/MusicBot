package com.jagrosh.jmusicbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.internal.entities.GuildImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

public class AudioManagerTest {

    AudioManager audioManager;
    AudioTrack track;
    AudioPlayer player;
    JDA jda;

    @Before
    public void Setup() {
        track = getSomeTrack();
        player = getMockedPlayer();
        jda = getMockedJDA();

        audioManager = new AudioHandler(null, new GuildImpl(null, 0), player);
    }

    @Test
    public void test_isMusicPlaying_initiallyFalse() {
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
    public void test_stopAndClear_isNotPlayingAfterwards() {
        audioManager.addTrack(new QueuedTrack(track, 0));
        ArgumentCaptor<AudioTrack> trackCaptor = ArgumentCaptor.forClass(AudioTrack.class);
        verify(player).playTrack(trackCaptor.capture());
        playingTrack = trackCaptor.getValue();

        Assert.assertTrue(audioManager.isMusicPlaying(jda));

        audioManager.stopAndClear();
        verify(player).stopTrack();
    }

    private AudioTrack getSomeTrack() {
        AudioTrack mockedTrack = mock(AudioTrack.class);
        return mockedTrack;
    }

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
