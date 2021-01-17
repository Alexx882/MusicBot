package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.music.*;

public class MusicCommandFactory extends AbstractCommandFactory {
    public MusicCommandFactory(Bot bot) {
        super(bot);
    }

    @Override
    public Command[] buildCommands() {
        return new Command[]{
                new LyricsCmd(bot),
                new NowplayingCmd(bot),
                new PlayCmd(bot),
                new PlaylistsCmd(bot),
                new QueueCmd(bot),
                new RemoveCmd(bot),
                new SearchCmd(bot),
                new SCSearchCmd(bot),
                new ShuffleCmd(bot),
                new SkipCmd(bot),
        };
    }
}
