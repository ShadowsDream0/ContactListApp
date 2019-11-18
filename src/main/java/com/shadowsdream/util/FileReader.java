package com.shadowsdream.util;

import com.shadowsdream.util.logging.ContactListLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        try (Stream<String> fileLinesStream = new BufferedReader(new InputStreamReader(getInputStreamFromFile(fileName))).lines()) {
            return fileLinesStream.collect(joining("\n"));
        }
    }

    private static InputStream getInputStreamFromFile(String fileName) {
        Objects.requireNonNull(fileName);

        return FileReader.class.getClassLoader().getResourceAsStream(fileName);
    }
}
