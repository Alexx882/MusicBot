package com.jagrosh.jmusicbot.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

/**
 * manages messages in the text-channel which displays the current bot-status
 */
public interface StatusMessageManager {

    /**
     * update reference to the given message.
     *
     * @param statusMessage contains guild and text-channel information needed
     */
    void setLastNPMessage(Message statusMessage);

    /**
     * clear the message information for the given guild
     *
     * @param guild target guild which information should be cleared
     */
    void clearLastNPMessage(Guild guild);

    /**
     * updates the topic of a channel
     *
     * @param guildId discord server ID
     * @param handler audio handler which manages tracks, playlists ...
     * @param wait    TODO ?
     */
    void updateTopic(long guildId, AudioManager handler, boolean wait);

    /**
     * event handler triggered when a new track is played or the current one is stopped
     *
     * @param guildId discord server ID
     * @param track   new track which starts playing, is null when the track is stopped
     * @param handler audio handler which manages tracks, playlists ...
     */
    void onTrackUpdate(long guildId, AudioTrack track, AudioManager handler);

    /**
     * event handler triggered when a message is deleted from a text-channel in discord
     *
     * @param guild     server where the question was deleted
     * @param messageId id of the deleted message
     */
    void onMessageDelete(Guild guild, long messageId);
}
