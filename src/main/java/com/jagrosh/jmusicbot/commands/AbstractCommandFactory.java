package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jmusicbot.Bot;

public abstract class AbstractCommandFactory {
    final Bot bot;

    public AbstractCommandFactory(Bot bot) {
        this.bot = bot;
    }

    public abstract Command[] buildCommands();

    public Bot getBot() {
        return bot;
    }
}
