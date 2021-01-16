package com.jagrosh.jmusicbot.utils;

import com.jagrosh.jmusicbot.audio.AudioManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.JDA;

public interface TextUtil {

    String getTopicPattern(JDA jda, AudioManager audioManager);



}
