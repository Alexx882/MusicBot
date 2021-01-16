package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.BotConfig;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class PlaylistTest {

//    private IPlaylist playlist;
//    private AudioTrack track;
//    private AudioPlaylist audioPlaylist;
//    private FriendlyException friendlyException;
//    private String loadItem = "i1";
//
//    @Before
//    public void before() {
//        playlist = new Playlist("My Playlist", Arrays.asList(loadItem), false, getSomeBotConfig(), null);
//
//        track = getSomeTrack();
//        audioPlaylist = getSomeAudioPlaylist();
//        friendlyException = new FriendlyException("Failed", FriendlyException.Severity.SUSPICIOUS, new Exception("gg"));
//    }
//
//    @Test
//    public void test_loadTracks_trackLoaded() {
//        boolean[] handled = {false, false};
//
//        playlist.loadTracks(getMockedPlayerManager("trackLoaded"), new Consumer<AudioTrack>() {
//                    @Override
//                    public void accept(AudioTrack audioTrack) {
//                        Assert.assertEquals(track, audioTrack);
//                        handled[0] = true;
//                    }
//                },
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        handled[1] = true;
//                    }
//                });
//
//        Assert.assertTrue(handled[0]);
//        Assert.assertTrue(handled[1]);
//    }
//
//    @Test
//    public void test_loadTracks_playlistLoaded() {
//        boolean[] handled = {false, false};
//        int[] ran = {0};
//
//        List<AudioTrack> tracks = Arrays.asList(getSomeTrack(), getSomeTrack());
//        when(audioPlaylist.getTracks()).thenReturn(tracks);
//
//        playlist.loadTracks(getMockedPlayerManager("playlistLoaded"), new Consumer<AudioTrack>() {
//                    @Override
//                    public void accept(AudioTrack audioTrack) {
//                        if (!handled[0]) {
//                            // handle first
//                            Assert.assertEquals(tracks.get(0), audioTrack);
//                            handled[0] = true;
//                        } else {
//                            // handle second
//                            Assert.assertEquals(tracks.get(1), audioTrack);
//                            handled[1] = true;
//                        }
//                    }
//                },
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        ++ran[0];
//                    }
//                });
//
//        Assert.assertTrue(handled[0]);
//        Assert.assertTrue(handled[1]);
//        Assert.assertEquals(1, ran[0]);
//    }
//
//    @Test
//    public void test_loadTracks_noMatches() {
//        boolean[] handled = {false, false};
//
//        playlist.loadTracks(getMockedPlayerManager("noMatches"), new Consumer<AudioTrack>() {
//                    @Override
//                    public void accept(AudioTrack audioTrack) {
//                        handled[0] = true;
//                    }
//                },
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        handled[1] = true;
//                    }
//                });
//
//        Assert.assertFalse(handled[0]);
//        Assert.assertTrue(handled[1]);
//
//        Assert.assertEquals(1, playlist.getErrors().size());
//        Assert.assertEquals(loadItem, playlist.getErrors().get(0).getItem());
//    }
//
//    @Test
//    public void test_loadTracks_loadFailed() {
//        boolean[] handled = {false, false};
//
//        playlist.loadTracks(getMockedPlayerManager("loadFailed"), new Consumer<AudioTrack>() {
//                    @Override
//                    public void accept(AudioTrack audioTrack) {
//                        handled[0] = true;
//                    }
//                },
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        handled[1] = true;
//                    }
//                });
//
//        Assert.assertFalse(handled[0]);
//        Assert.assertTrue(handled[1]);
//
//        Assert.assertEquals(1, playlist.getErrors().size());
//        Assert.assertEquals(loadItem, playlist.getErrors().get(0).getItem());
//        Assert.assertEquals("Failed to load track: " + friendlyException.getMessage(),
//                playlist.getErrors().get(0).getReason());
//    }
//
//    private BotConfig getSomeBotConfig() {
//        BotConfig mock = mock(BotConfig.class);
////        when(mock.getPlaylistsFolder()).thenReturn(testFolderPath);
//        return mock;
//    }
//
//    private AudioTrack getSomeTrack() {
//        AudioTrack mock = mock(AudioTrack.class);
//        return mock;
//    }
//
//    private AudioPlaylist getSomeAudioPlaylist() {
//        AudioPlaylist mock = mock(AudioPlaylist.class);
//        return mock;
//    }
//
//    /**
//     * Executes the AudioLoadResultHandler methods
//     *
//     * @param exec one of {trackLoaded, playlistLoaded, NoMatches, loadFailed}
//     * @return
//     */
//    private PlayerManager getMockedPlayerManager(String exec) {
//        PlayerManager mock = mock(PlayerManager.class, RETURNS_DEEP_STUBS);
//
//        when(mock.loadItemOrdered(any(), anyString(), any(AudioLoadResultHandler.class)))
//                .thenAnswer(new Answer<Object>() {
//                    @Override
//                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
//                        AudioLoadResultHandler h = invocationOnMock.getArgument(2);
//
//                        if (exec == "trackLoaded")
//                            h.trackLoaded(track);
//                        else if (exec == "playlistLoaded")
//                            h.playlistLoaded(audioPlaylist);
//                        else if (exec == "noMatches")
//                            h.noMatches();
//                        else if (exec == "loadFailed")
//                            h.loadFailed(friendlyException);
//                        else
//                            throw new Exception("Lol clown read javadoc");
//
//                        return null;
//                    }
//                });
//        return mock;
//    }

}
