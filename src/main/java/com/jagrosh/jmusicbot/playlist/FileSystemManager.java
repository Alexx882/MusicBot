package com.jagrosh.jmusicbot.playlist;

public interface FileSystemManager {

    /**
     * creates folder where files can be safely stored by the implementing class
     */
    void createFolder();

    /**
     * @return if folder for this class exists
     */
    boolean folderExists();
}
