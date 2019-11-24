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

//        URL fileUrl = FileReader.class.getClassLoader().getResource(fileName);
//        //jar:file:/Users/serhiiluhovyi/k8sdevoxx/contact-list-app/target/contact-list-app-1.0-SNAPSHOT.jar!/db/migration/table_initialization.sql
//        try {
//            return Paths.get(fileUrl.toURI());
//        } catch (URISyntaxException e) {
//            throw new FileReaderException("Invalid file URL",e);
//        }

        URL fileUrl = FileReader.class.getClassLoader().getResource(fileName);
        System.out.println(fileName);
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
            throw new FileReaderException("Invalid file URL",e);
        }
    }
}
