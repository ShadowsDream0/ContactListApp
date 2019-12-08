package com.shadowsdream.util.io;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;


public class FileReader {

    public static String readWholeFileFromResources(String fileName) {
        Path filePath = createPathFromFileName(fileName);
        try (Stream<String> fileLinesStream = openFileLinesStream(filePath)) {
            return fileLinesStream.collect(joining("\n"));
        }
    }

    public static Stream<String> readLinesFromFile(Path filePath) {
        return openFileLinesStream(filePath);
    }

    private static Stream<String> openFileLinesStream(Path filePath) {
        try {
            return Files.lines(filePath);
        } catch (IOException e) {
            throw new FileReaderException("Cannot create stream of file lines!", e);
        }
    }

    private static Path createPathFromFileName(String fileName) {
        Objects.requireNonNull(fileName);

        URL fileUrl = FileReader.class.getClassLoader().getResource(fileName);

        // handle files in jar appropriately
        try {
            URI uri = fileUrl.toURI();

            if("jar".equals(uri.getScheme())){
                for (FileSystemProvider provider: FileSystemProvider.installedProviders()) {
                    if (provider.getScheme().equalsIgnoreCase("jar")) {
                        try {
                            provider.getFileSystem(uri);
                        } catch (FileSystemNotFoundException e) {
                            // in this case we need to initialize it first:
                            System.out.println("hello...");
                            provider.newFileSystem(uri, Collections.emptyMap());
                        }
                    }
                }
            }

            return Paths.get(uri);
        } catch (URISyntaxException | IOException e) {
            throw new FileReaderException("Invalid file URL", e);
        }
    }
}
