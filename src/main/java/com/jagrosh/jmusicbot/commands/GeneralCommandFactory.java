package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.general.SettingsCmd;

public class GeneralCommandFactory extends AbstractCommandFactory {
    public GeneralCommandFactory(Bot bot) {
        super(bot);
    }

    @Override
    public Command[] buildCommands() {
        return new Command[]{
                new PingCommand(),
                new SettingsCmd(bot)
        };
    }
}
