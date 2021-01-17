package com.jagrosh.jmusicbot;

import com.jagrosh.jmusicbot.commands.*;
import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.List;

public class CommandFactoryProvider implements CommandProvider {
    @Override
    public List<AbstractCommandFactory> getAvailableCommandFactories(Bot bot) {
        return Arrays.asList(new AbstractCommandFactory[]{
                        new AdminCommandFactory(bot),
                        new DjCommandFactory(bot),
                        new GeneralCommandFactory(bot),
                        new MusicCommandFactory(bot),
                        new OwnerCommandFactory(bot)
                }
        );
    }
}
