package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.settings.SettingsManager;
import com.jagrosh.jmusicbot.settings.SettingsProvider;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.internal.entities.GuildImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AudioEventManagerTest {

    AudioHandler audioHandler;
    AudioTrack track;
    AudioPlayer player;
    JDA jda;

    private final long guildId = 0L;

    @Before
    public void Setup() {
        track = getSomeTrack();
        player = getMockedPlayer();
        jda = getMockedJDA();

        audioHandler = new AudioHandler(null, null, null, null, null, new GuildImpl(null, guildId), player);
    }

    private AudioEventManager getSomeAudioEventAdapter() {
        return mock(AudioEventHandler.class, RETURNS_DEEP_STUBS);
    }

    private SettingsProvider getSomeSettingProvider(boolean repeatMode) {
        SettingsProvider settingsProvider = mock(SettingsManager.class, RETURNS_DEEP_STUBS);
        when(settingsProvider.getSettings(guildId).getRepeatMode()).thenReturn(repeatMode);
        return settingsProvider;
    }

    private AudioManager getSomeAudioManager(boolean isQueueEmpty, boolean canPlayFromDefault) {
        AudioManager handler = mock(AudioHandler.class, RETURNS_DEEP_STUBS);
        when(handler.playFromDefault()).thenReturn(canPlayFromDefault);   // no repeat
        when(handler.getQueue().isEmpty()).thenReturn(isQueueEmpty); // queue is empty

        return handler;
    }

    @Test
    public void test_onTrackEnd_noRepeat_endAfterLastSong() {
        SettingsProvider settingsProvider = getSomeSettingProvider(false);
        StatusMessageManager statusMessageManager = mock(NowplayingHandler.class, RETURNS_DEEP_STUBS);

        AudioManager handler = getSomeAudioManager(true, false);

        AudioEventManager listener = new AudioEventHandler(
                guildId,
                settingsProvider,
                handler,
                statusMessageManager,
                () -> {
                },
                false
        );

        listener.onTrackEnd(player, track, AudioTrackEndReason.FINISHED);

        verify(player, never()).playTrack(any());
    }

    @Test
    public void test_onTrackEnd_repeat_restartAfterLastSong() {
        SettingsProvider settingsProvider = getSomeSettingProvider(true);
        StatusMessageManager statusMessageManager = mock(NowplayingHandler.class, RETURNS_DEEP_STUBS);

        AudioManager handler = getSomeAudioManager(true, false);

        AudioEventManager listener = new AudioEventHandler(
                guildId,
                settingsProvider,
                handler,
                statusMessageManager,
                () -> {
                },
                false
        );

        listener.onTrackEnd(player, track, AudioTrackEndReason.FINISHED);
        getSomeAudioEventAdapter().onTrackEnd(player, track, AudioTrackEndReason.FINISHED);

        verify(handler).addTrack(any());
    }

    @Test
    public void test_onTrackEnd_playNextSongFromQueue() {
        SettingsProvider settingsProvider = getSomeSettingProvider(true);
        StatusMessageManager statusMessageManager = mock(NowplayingHandler.class, RETURNS_DEEP_STUBS);

        AudioManager handler = getSomeAudioManager(false, false);

        QueuedTrack trackNew = mock(QueuedTrack.class);
        AudioTrack actualTrack = getSomeTrack();
        when(trackNew.getTrack()).thenReturn(actualTrack);

        when(handler.getQueue().pull()).thenReturn(trackNew);

        AudioEventManager listener = new AudioEventHandler(
                guildId,
                settingsProvider,
                handler,
                statusMessageManager,
                () -> {
                },
                false
        );

        listener.onTrackEnd(player, track, AudioTrackEndReason.FINISHED);
        getSomeAudioEventAdapter().onTrackEnd(player, track, AudioTrackEndReason.FINISHED);

        verify(player).playTrack(trackNew.getTrack());
    }

    @Test
    public void testAudioEventHandlerCallbackCalls() {
        SettingsProvider settingsProvider = getSomeSettingProvider(true);
        StatusMessageManager statusMessageManager = mock(NowplayingHandler.class, RETURNS_DEEP_STUBS);

        AudioManager handler = getSomeAudioManager(false, false);

        AudioEventManager listener = new AudioEventHandler(
                guildId,
                settingsProvider,
                handler,
                statusMessageManager,
                () -> {
                },
                false
        );

        AtomicBoolean wasExecuted = new AtomicBoolean(false);
        listener.registerOnTrackStartCallback(() -> wasExecuted.set(true));

        listener.onTrackStart(player, track);

        assertTrue(wasExecuted.get());
    }

    @Test
    public void getGuildIdTest() {
        SettingsProvider settingsProvider = getSomeSettingProvider(true);
        StatusMessageManager statusMessageManager = mock(NowplayingHandler.class, RETURNS_DEEP_STUBS);

        AudioManager handler = getSomeAudioManager(false, false);

        AudioEventManager listener = new AudioEventHandler(
                guildId,
                settingsProvider,
                handler,
                statusMessageManager,
                () -> {
                },
                false
        );

        assertEquals(listener.getGuildId(), guildId);
    }

    @Test
    public void getSettingsProviderTest() {
        SettingsProvider settingsProvider = getSomeSettingProvider(true);
        StatusMessageManager statusMessageManager = mock(NowplayingHandler.class, RETURNS_DEEP_STUBS);

        AudioManager handler = getSomeAudioManager(false, false);

        AudioEventManager listener = new AudioEventHandler(
                guildId,
                settingsProvider,
                handler,
                statusMessageManager,
                () -> {
                },
                false
        );

        assertEquals(listener.getSettingProvider(), settingsProvider);
    }

    private AudioTrack getSomeTrack() {
        AudioTrack mockedTrack = mock(AudioTrack.class, RETURNS_DEEP_STUBS);
        when(mockedTrack.makeClone()).thenReturn(mockedTrack);
        when(mockedTrack.getInfo()).thenReturn(getSomeInfo());
        when(mockedTrack.getUserData(any())).thenReturn(0L);
        return mockedTrack;
    }

    private AudioTrackInfo getSomeInfo() {
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
}
