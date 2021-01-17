package com.jagrosh.jmusicbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.entities.GuildImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

public class AudioHandlerTest {

    AudioHandler audioHandler;
    AudioTrack track;
    AudioPlayer player;
    JDA jda;
    PlayerManager manager;

    @Before
    public void Setup() {
        track = getSomeTrack();
        player = getMockedPlayer();
        jda = getMockedJDA();
        manager = getMockedPlayerManager();

        audioHandler = new AudioHandler((PlayerManager)manager, new GuildImpl(null, 0), player);
    }

    @Test
    public void test_onTrackEnd_noRepeat_endAfterLastSong(){
        when(manager.getBot().getSettingsManager().getSettings(anyLong()).getRepeatMode())
                .thenReturn(false); // disable repeat

        audioHandler.onTrackEnd(player, track, AudioTrackEndReason.FINISHED);

        verify(player, never()).playTrack(any());
    }

    @Test
    public void test_onTrackEnd_repeat_restartAfterLastSong(){
        when(manager.getBot().getSettingsManager().getSettings(anyLong()).getRepeatMode())
                .thenReturn(true); // disable repeat

        audioHandler.onTrackEnd(player, track, AudioTrackEndReason.FINISHED);

        verify(player).playTrack(track);
    }

    @Test
    public void test_getNowPlaying(){
        // simulate playing track
        playingTrack = track;

        Message res = audioHandler.getNowPlaying(jda);

        Assert.assertEquals("null **Now Playing in null...**", res.getContentRaw());

        Assert.assertEquals(1, res.getEmbeds().size());
        Assert.assertEquals(getSomeInfo().title, res.getEmbeds().get(0).getTitle());
    }

    private AudioTrack getSomeTrack() {
        AudioTrack mockedTrack = mock(AudioTrack.class, RETURNS_DEEP_STUBS);
        when(mockedTrack.makeClone()).thenReturn(mockedTrack);
        when(mockedTrack.getInfo()).thenReturn(getSomeInfo());
        when(mockedTrack.getUserData(any())).thenReturn(0L);
        return mockedTrack;
    }

    private AudioTrackInfo getSomeInfo(){
        return new AudioTrackInfo("Some Title", "Some Author", 0L, "Some Id", false, "Some URI");
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

    private PlayerManager getMockedPlayerManager() {
        PlayerManager mock = mock(PlayerManager.class, RETURNS_DEEP_STUBS);
        return mock;
    }
}
