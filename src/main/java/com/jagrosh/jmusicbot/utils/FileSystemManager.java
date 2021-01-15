package com.jagrosh.jmusicbot.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * used to manage files in one folder. the folder name gets specified by getFolderName() and each file gets
 * read/wrote relative to that folder
 */
public interface FileSystemManager {

    /**
     * @return name of the folder the files will be written to
     */
    String getFolderName();

    /**
     * creates folder where files can be safely stored by the implementing class
     */
    default void createFolder() {
        try {
            Files.createDirectory(OtherUtil.getPath(getFolderName()));
        } catch (IOException ignore) {
        }
    }

    /**
     * @return if folder for this class exists
     */
    default boolean folderExists() {
        return Files.exists(OtherUtil.getPath(getFolderName()));
    }

    default File getFolder() {
        if (!folderExists())
            createFolder();

        return new File(getFolderName());
    }

    /**
     * @param type type suffix like ".txt" or ".mp3"
     * @return all files of the given type
     */
    default List<String> getFilesOfType(String type) {
        if (folderExists()) {
            return Arrays.stream(
                    getFolder()
                            .listFiles((pathname) -> pathname.getName().endsWith(type)))
                    .map(f -> f.getName().substring(0, f.getName().length() - type.length()))
                    .collect(Collectors.toList());
        } else {
            createFolder();
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * creates the given empty file in the folder of the FileSystemManager
     *
     * @param fileName name of the new file including ending f.e. "newFile.txt"
     * @return path to the newly created file
     * @throws IOException something went wrong during creation (permission, ...)
     */
    default Path createFile(String fileName) throws IOException {
        return Files.createFile(OtherUtil.getPath(getFolderName() + File.separator + fileName));
    }

    /**
     * creates the given file in the folder of the FileSystemManager and writes
     * the given content to it. existing content will be deleted
     *
     * @param fileName name of the new file including ending f.e. "newFile.txt"
     * @param content  new content of the file
     * @return path to the newly created file
     * @throws IOException something went wrong during creation (permission, ...)
     */
    default Path createFile(String fileName, byte[] content) throws IOException {
        return Files.write(OtherUtil.getPath(getFolderName() + File.separator + fileName), content);
    }

    /**
     * @param fileName name of the new file including ending f.e. "newFile.txt"
     * @return all lines of the file in a list
     * @throws IOException something went wrong during creation (permission, ...)
     */
    default List<String> readAllLinesOfFile(String fileName) throws IOException {
        return Files.readAllLines(OtherUtil.getPath(getFolderName() + File.separator + fileName));
    }

    /**
     * deletes the given file in the folder of the FileSystemManager
     *
     * @param fileName name of the target file including ending f.e. "newFile.txt"
     * @throws IOException something went wrong during creation (permission, ...)
     */
    default void deleteFile(String fileName) throws IOException {
        Files.delete(OtherUtil.getPath(getFolderName() + File.separator + fileName));
    }
}
