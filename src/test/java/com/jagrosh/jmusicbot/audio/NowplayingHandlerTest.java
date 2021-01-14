package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.entities.Pair;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.api.entities.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class NowplayingHandlerTest {

    NowplayingHandler npHandler;
    Long guildId;

    @Before
    public void Setup() {
        guildId = 5L;
        npHandler = new NowplayingHandlerTestSubclass(getSomeBot());
    }

    @Test
    public void test_setLastNPMessage() {
        Message m = getSomeMessage();
        npHandler.setLastNPMessage(m);

        Pair<Long, Long> addedEntry = ((NowplayingHandlerTestSubclass) npHandler).getLastNP(this.guildId);
        Assert.assertEquals(m.getTextChannel().getIdLong(), (long)addedEntry.getKey());
        Assert.assertEquals(m.getIdLong(), (long)addedEntry.getValue());
    }

    @Test
    public void test_clearLastNPMessage() {
        Message m = getSomeMessage();
        npHandler.setLastNPMessage(m);
        npHandler.clearLastNPMessage(m.getGuild());

        Pair<Long, Long> addedEntry = ((NowplayingHandlerTestSubclass) npHandler).getLastNP(this.guildId);
        Assert.assertNull(addedEntry);
    }

    private Bot getSomeBot() {
        Bot mock = mock(Bot.class);
        return mock;
    }

    private Message getSomeMessage() {
        Message mock = mock(Message.class, RETURNS_DEEP_STUBS);

        // concrete long value does not matter
        when(mock.getGuild().getIdLong()).thenReturn(this.guildId);
        when(mock.getTextChannel().getIdLong()).thenReturn(7L);
        when(mock.getIdLong()).thenReturn(3L);

        return mock;
    }


}
