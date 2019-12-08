package com.shadowsdream.util.foldermonitor;

import com.shadowsdream.util.logging.ContactListLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FolderMonitorImpl implements FolderMonitor {

    private Path folder = null;
    private List<Path> filesList = null;
    private String fileSeparator = null;
    private Path archive = null;


    public FolderMonitorImpl(Path folder) throws IOException {
        this.folder = folder;
        this.fileSeparator = FileSystems.getDefault().getSeparator();
        this.archive = Paths.get(this.folder + fileSeparator + "archive");
    }


    @Override
    public List<Path> getListOfFilesFromFolder() {
        return filesList;
    }


    @Override
    public void start() throws IOException, InterruptedException {
        filesList = new ArrayList<>();

        if (!Files.isDirectory(folder)) {
            throw new FileNotFoundException("folder not found " + folder);
        }

        // scan all files from folder
        List<Path> bufferList = Files.list(folder).collect(Collectors.toList());

        // add files in collection
        for (Path file : bufferList) {

            if (!Files.isRegularFile(file) || Files.isHidden(file)) {
                ContactListLogger.getLog().debug("File " + file + " not a regular file or it is hidden");
                continue;
            }

            createArchiveFolderIfNotExists();

            // sent read files to archive
            Files.move(file, this.archive.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            filesList.add(this.archive.toAbsolutePath().resolve(file.getFileName()));
        }
    }

    private void createArchiveFolderIfNotExists() throws IOException {
        try {
            Files.createDirectory(this.archive);
        } catch (FileAlreadyExistsException e) {
            return;
        }
    }

}
