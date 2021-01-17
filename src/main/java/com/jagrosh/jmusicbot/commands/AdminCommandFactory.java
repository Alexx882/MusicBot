package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.admin.PrefixCmd;
import com.jagrosh.jmusicbot.commands.admin.SetdjCmd;
import com.jagrosh.jmusicbot.commands.admin.SettcCmd;
import com.jagrosh.jmusicbot.commands.admin.SetvcCmd;

public class AdminCommandFactory extends AbstractCommandFactory {
    public AdminCommandFactory(Bot bot) {
        super(bot);
    }

    @Override
    public Command[] buildCommands() {
        return new Command[]{
                new PrefixCmd(bot),
                new SetdjCmd(bot),
                new SettcCmd(bot),
                new SetvcCmd(bot)
        };
    }
}
