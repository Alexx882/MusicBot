package com.jagrosh.jmusicbot;

import com.jagrosh.jmusicbot.commands.AbstractCommandFactory;

import java.util.List;

public interface CommandProvider {
    List<AbstractCommandFactory> getAvailableCommandFactories(Bot bot);
}
