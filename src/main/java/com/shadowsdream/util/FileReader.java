package com.shadowsdream.util;

import com.shadowsdream.util.logging.ContactListLogger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * {@link FileReader} provides an API that allow to read whole file into a {@link String} by file name.
 */
public class FileReader {
    /**
     * Returns a {@link String} that contains whole text from the file specified by name.
     *
     * @param fileName a name of a text file
     * @return string that holds whole file content
     */
    public static String readWholeFileFromResources(String fileName) {
        Objects.requireNonNull(fileName, "Argument fileName must not be null");

        try ( Stream<String> fileLinesStream = new BufferedReader(
                                                new InputStreamReader(getInputStreamFromFile(fileName))).lines() ) {
            return fileLinesStream.collect(joining("\n"));
        }
    }

    public static List<String[]> getListOfStringArraysFromPath(Path filePath) throws IOException {
        Objects.requireNonNull(filePath, "Argument filepath must not be null");
        String delimiter = PropertyLoader.getProperties(); // todo: fix this very bad decision

        List<String[]> listOfStringArrays = null;
        try (Stream<String> linesStream = Files.newBufferedReader(filePath).lines()) {
            listOfStringArrays = linesStream.map(line -> line.split(delimiter))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error during reading file\nCaused by: " + e.getMessage());
            System.exit(1);
        }
        return listOfStringArrays;
    }

    private static InputStream getInputStreamFromFile(String fileName) {
        Objects.requireNonNull(fileName, "Argument fileName must not be null");

        return FileReader.class.getClassLoader().getResourceAsStream(fileName);
    }
}
