package com.jagrosh.jmusicbot.utils;

import com.jagrosh.jmusicbot.entities.Prompt;

public interface Updater {

    String checkVersion(Prompt prompt);

    String getCurrentVersion();

    String getLatestVersion();
}
