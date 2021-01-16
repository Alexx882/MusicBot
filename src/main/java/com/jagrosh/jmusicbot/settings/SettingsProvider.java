package com.jagrosh.jmusicbot.settings;

import com.jagrosh.jmusicbot.BotConfig;
import net.dv8tion.jda.api.entities.Guild;

/**
 * maintains instances of Settings for each guild, the bot is running on
 */
public interface SettingsProvider {

    /**
     * Gets non-null settings for a Guild
     *
     * @param guildId numeric guild ID
     * @return the existing settings, or new settings for that guild
     */
    Settings getSettings(long guildId);

    default Settings getSettings(Guild guild) {
        return getSettings(guild.getIdLong());
    }
}
