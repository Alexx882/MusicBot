package com.jagrosh.jmusicbot.settings;

public class SettingsManagerTestSubclass extends SettingsManager {

    public void setSettings(Long id, Settings s) {
        settings.put(id, s);
    }

}
