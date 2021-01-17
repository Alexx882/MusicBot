package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.dj.*;

public class DjCommandFactory extends AbstractCommandFactory {

    public DjCommandFactory(Bot bot) {
        super(bot);
    }

    @Override
    public Command[] buildCommands() {
        return new Command[]{
                new ForceRemoveCmd(bot),
                new ForceskipCmd(bot),
                new MoveTrackCmd(bot),
                new PauseCmd(bot),
                new PlaynextCmd(bot),
                new RepeatCmd(bot),
                new SkiptoCmd(bot),
                new StopCmd(bot),
                new VolumeCmd(bot)
        };
    }
}
