package com.jagrosh.jmusicbot.audio;

import net.dv8tion.jda.api.entities.Guild;

public interface AudioPlayerManager {
    /**
     * create or retrieve the AudioHandler of a guild
     *
     * @param guild guild, the audio gets sent to
     * @return ready-to-use AudioHandler
     */
    AudioHandler setUpHandler(Guild guild);
}
