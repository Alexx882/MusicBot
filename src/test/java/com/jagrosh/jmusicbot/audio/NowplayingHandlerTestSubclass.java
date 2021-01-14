package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.entities.Pair;

import java.util.HashMap;

/**
 * Subclass used during testing
 */
public class NowplayingHandlerTestSubclass extends NowplayingHandler {

    public NowplayingHandlerTestSubclass(Bot bot) {
        super(bot);
    }

    /**
     * Returns the message list as value from map: guild -> channel,message
     * @return
     */
    public Pair<Long, Long> getLastNP(Long guildId){
        return super.lastNP.get(guildId);
    }
}
