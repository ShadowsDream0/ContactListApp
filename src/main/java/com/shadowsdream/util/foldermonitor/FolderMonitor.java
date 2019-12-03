package com.shadowsdream.util.foldermonitor;


import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FolderMonitor {
    public void start() throws IOException, InterruptedException;
    public List<Path> getListOfFilesFromFolder();
}
