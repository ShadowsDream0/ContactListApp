package com.shadowsdream.service.implementations;

import com.shadowsdream.exception.ServiceException;

import java.nio.file.Path;

public interface ImportExportService {
    void importFromFile(Path filePath) throws ServiceException;
    void exportToFile(Path filePath) throws ServiceException;
    String getContactsLines() throws ServiceException;
}
