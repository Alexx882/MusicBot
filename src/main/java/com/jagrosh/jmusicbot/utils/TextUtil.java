package com.jagrosh.jmusicbot.utils;

import com.jagrosh.jmusicbot.BotConfig;
import com.jagrosh.jmusicbot.audio.AudioManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public interface TextUtil {

    String getTopicPattern(JDA jda, AudioManager audioManager);

    Message getNowPlaying(JDA jda, AudioManager audioManager, BotConfig config, Guild guild);

    Message getNoMusicPlaying(BotConfig config, Guild guild, AudioPlayer audioPlayer);
}
