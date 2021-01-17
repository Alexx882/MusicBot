package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.owner.*;

public class OwnerCommandFactory extends AbstractCommandFactory {
    public OwnerCommandFactory(Bot bot) {
        super(bot);
    }

    @Override
    public Command[] buildCommands() {
        return new Command[]{
                new AutoplaylistCmd(bot),
                new DebugCmd(bot),
                new PlaylistCmd(bot),
                new SetavatarCmd(bot),
                new SetgameCmd(bot),
                new SetnameCmd(bot),
                new SetstatusCmd(bot),
                new ShutdownCmd(bot)
        };
    }
}
