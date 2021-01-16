package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.entities.Pair;
import net.dv8tion.jda.api.JDA;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Subclass used during testing
 */
public class NowplayingHandlerTestSubclass extends NowplayingHandler {

    public NowplayingHandlerTestSubclass(Bot bot) {
        super(bot.getSettingsManager(), bot.getThreadpool(), bot.getConfig(), () -> {});
    }

    /**
     * Returns the message list as value from map: guild -> channel,message
     *
     * @return
     */
    public Pair<Long, Long> getLastNP(Long guildId) {
        return super.lastNP.get(guildId);
    }

    private static JDA getMockedJDA() {
        JDA mock = mock(JDA.class, RETURNS_DEEP_STUBS);
        when(mock.getGuildById(anyInt()).getSelfMember().getVoiceState().inVoiceChannel())
                .thenReturn(true);
        return mock;
    }
}
