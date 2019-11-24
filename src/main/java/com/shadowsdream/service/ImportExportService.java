package com.shadowsdream.service;

import com.shadowsdream.dto.PersonSaveDto;
import com.shadowsdream.exception.PersonServiceException;
import com.shadowsdream.util.FileReader;
import com.shadowsdream.util.logging.ContactListLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImportExportService {

    private ImportExportService(){}


    public static void importFromFile(String filePath) {

        // read person data from file
        List<String[]> linesWithPersonData = null;
        try {
            linesWithPersonData = FileReader.getListOfStringArraysFromPath(filePath);
        } catch (FileNotFoundException e) {
            PrettyPrinter.printError("Could not find property file\n");
            return;
        } catch (IOException notFoundEx) {
            PrettyPrinter.printError("Could not read file\n");
            return;
        }

        ContactListLogger.getLog().debug("Lines with person data got: " + linesWithPersonData.get(0));

        // save contacts to db
        PersonSaveDto personSaveDto = null;
        int sizeOfList = linesWithPersonData.size();
        for(int line = 0 ; line < sizeOfList; line++) {
            try {
                personSaveDto = getPersonSaveDtoFromData(parsePersonData(linesWithPersonData.get(line)));
            } catch (IOException e) {
                PrettyPrinter.print("Import failed on line " + (line + 1) + ". Cause: " + e.getMessage() + "\n");
                PrettyPrinter.print("Exiting...\n");
                System.exit(1);
            }

            try {
                personService.save(personSaveDto);
            } catch (PersonServiceException e) {
                PrettyPrinter.print("Could not save contact because " + e.getMessage());
                return;
            }
        }

        PrettyPrinter.print("Contacts have been imported successfully\n");
    }

    public static void exortFromFile() {
        // todo: implement export()
    }
}
