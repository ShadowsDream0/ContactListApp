package com.shadowsdream.util.foldermonitor;

import com.shadowsdream.util.logging.ContactListLogger;

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
    private boolean isScanning = true;
    private List<Path> filesList = null;
    private String fileSeparator = null;
    private Path archive = null;

    public FolderMonitorImpl(Path folder) throws IOException {
        this.folder = folder;
        this.fileSeparator = FileSystems.getDefault().getSeparator();
        this.archive = Paths.get(this.folder + fileSeparator + "archive" + fileSeparator);
    }


    @Override
    public List<Path> getListOfFilesFromFolder() {
        return filesList;
    }

    public boolean getMonitorStatus() {
        return this.isScanning;
    }

    @Override
    public void setMonitorStatus(boolean status) {
        this.isScanning = status;
    }

    @Override
    public void start() throws IOException, InterruptedException {
        while (this.isScanning) {
            filesList = new ArrayList<>();

            if (!Files.isDirectory(folder)) {
                throw new FileNotFoundException("folder not found " + folder);
            }

            // get all files from folder
            List<Path> bufferList = Files.list(folder).collect(Collectors.toList());

            // get files found in the folder
            for (Path file : bufferList) {

                if (!Files.isRegularFile(file) || Files.isHidden(file)) {
                    ContactListLogger.getLog().debug("File " + file + " not a regular file or it is hidden");
                    continue;
                }

                createArchiveFolderIfNotExists();

                // sent read files to archive
                Files.move(file, archive, StandardCopyOption.REPLACE_EXISTING);

                filesList.add(file);

                Thread.sleep(5000);
            }
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
