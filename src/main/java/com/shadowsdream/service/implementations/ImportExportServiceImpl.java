package com.shadowsdream.service.implementations;

import com.shadowsdream.dao.PersonDao;
import com.shadowsdream.dao.implementations.PersonDaoImpl;
import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.exception.InsertOperationException;
import com.shadowsdream.exception.ServiceException;
import com.shadowsdream.model.Person;
import com.shadowsdream.model.PhoneNumber;
import com.shadowsdream.model.enums.Gender;
import com.shadowsdream.model.enums.PhoneType;
import com.shadowsdream.service.ImportExportService;
import com.shadowsdream.service.PrettyPrinter;
import com.shadowsdream.util.foldermonitor.FolderMonitor;
import com.shadowsdream.util.foldermonitor.FolderMonitorImpl;
import com.shadowsdream.util.io.FileReader;
import com.shadowsdream.util.io.FileReaderException;
import com.shadowsdream.util.PropertyLoader;
import com.shadowsdream.util.logging.ContactListLogger;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public final class ImportExportServiceImpl implements ImportExportService {

    private static PersonDao personDao = null;
    private static ImportExportService importExportService = null;


    private ImportExportServiceImpl(){}

    public static ImportExportService getInstance(DataSource dataSource) {
        if (personDao == null) {
            personDao = new PersonDaoImpl(dataSource);
            importExportService = new ImportExportServiceImpl();
        }

        return importExportService;
    }

    @Override
    public void importFromFile(Path filePath) throws ServiceException {
        ContactListLogger.getLog().info("Invoked importFromFile() method...");

        // read person data from file
        List<String[]> linesWithPersonData = null;
        String delimiter = getDelimiter();
        try {
            linesWithPersonData = FileReader.readLinesFromFile(filePath)
                    .map(line -> line.split(delimiter))
                    .collect(Collectors.toList());
        } catch (FileReaderException e) {
            throw new ServiceException("could not read file\n", e);
        }

        // save contacts to db
        Person person = null;
        int sizeOfList = linesWithPersonData.size();
        for (int line = 0; line < sizeOfList; line++) {
            try {
                person = getPersonFromData(parsePersonData(linesWithPersonData.get(line)));
                personDao.save(person);
            } catch (InsertOperationException e) {
                throw new ServiceException("Could not save contact because " + e.getMessage());
            } catch (DaoOperationException daoEx) {
                ContactListLogger.getLog().error("Critical error occurred: " + daoEx.getMessage() + " " + daoEx.getCause());
                PrettyPrinter.print("Server critical error. Exiting...");
                System.exit(1);
            } catch (IOException e) {
                throw new ServiceException("Import failed on line " + (line + 1) + ". Cause: " + e.getMessage() + "\n");
            }
        }
    }


    @Override
    public void exportToFile(Path filePath) throws ServiceException {
        // write contacts to a file
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(getContactsLines() + "\n");
        } catch (IOException e) {
            throw new ServiceException("could not write to a file", e);
        }
    }


    @Override
    public String getContactsLines() throws ServiceException {
        // get all contacts from db
        List<Person> persons = null;
        try {
            persons = personDao.findAll();
        } catch (DaoOperationException e) {
            ContactListLogger.getLog().error("Critical error occurred: " + e.getMessage() + " " + e.getCause());
            PrettyPrinter.printError("Server critical error. Exiting...");
            System.exit(1);
        }

        // prepare contacts to writing to a file
        String delimiter = getDelimiter();
        return persons.stream()
                .map(p -> {

                    // get person's phone numbers
                    String workPhoneNumber = null;
                    String homePhoneNumber = null;
                    Map<PhoneType, List<String>> phoneNumbersByType = null;
                    if (p.getPhoneNumbers() != null) {
                        phoneNumbersByType = p.getPhoneNumbers()
                                .stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.groupingBy(PhoneNumber::getType,
                                        Collectors.mapping(PhoneNumber::getPhone, Collectors.toList()))
                                );
                        workPhoneNumber = phoneNumbersByType.get(PhoneType.WORK) != null ?
                                            phoneNumbersByType.get(PhoneType.WORK).get(0) :
                                            null;
                        homePhoneNumber = phoneNumbersByType.get(PhoneType.HOME) != null ?
                                            phoneNumbersByType.get(PhoneType.HOME).get(0) :
                                            null;
                    }

                    // prepare line with person data
                    return new StringBuilder()
                            .append(p.getFirstName())
                            .append(delimiter)
                            .append(p.getLastName())
                            .append(delimiter)
                            .append(p.getBirthday())
                            .append(delimiter)
                            .append(p.getGender())
                            .append(delimiter)
                            .append(p.getCity())
                            .append(delimiter)
                            .append(p.getEmail())
                            .append(delimiter)
                            .append(workPhoneNumber != null ?
                                    workPhoneNumber :
                                    "")
                            .append(delimiter)
                            .append(homePhoneNumber != null ?
                                    homePhoneNumber :
                                    "")
                            .toString();
                })
                .collect(Collectors.joining("\n"));
    }


    @Override
    public void importFromFolder(Path folderPath) throws ServiceException {
        this.initFolderMonitor(folderPath);
    }


    private void initFolderMonitor(Path folderPath) throws ServiceException {
        Objects.requireNonNull(folderPath, "Argument folderPath must not be null");

        FolderMonitor folderMonitor = null;
        try {
            folderMonitor = new FolderMonitorImpl(folderPath);
            folderMonitor.start();
        } catch (FileNotFoundException fe) {
            throw new ServiceException("could not read folder: " + fe.getMessage());
        } catch (IOException e) {
            throw new ServiceException("could not read file: " + e.getMessage());
        } catch (InterruptedException ie) {
            ContactListLogger.getLog().debug("Thread interrupted " + ie.getMessage() + " " + ie.getCause());
            return;
        }
        List<Path> files = folderMonitor.getListOfFilesFromFolder();

        for (Path file : files) {
            try {
                this.importFromFile(file);
            } catch (ServiceException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }


    private static Map<String, String> parsePersonData(String[] personData) throws IOException {
        Objects.requireNonNull(personData, "Argument personData must not be null");
        if (personData.length != 8) {
            throw new IOException("corrupted person data");
        }

        String[] keys = {"firstName", "lastName", "gender", "birthday",
                "city", "email", "workPhoneNumber", "homePhoneNumber"};

        Map<String, String> mapPersonData = new HashMap<>(8);
        for(int i = 0; i < keys.length; i++) {
            if(personData[i] == null) {
                throw new IOException("person data contains null reference at " + (i + 1) + " index");
            } else {
                mapPersonData.put(keys[i], personData[i]);
            }
        }

        return mapPersonData;
    }


    private static Person getPersonFromData(Map<String, String> personData) {
        Objects.requireNonNull(personData, "Argument personData must not be null");

        Person person = new Person();
        person.setFirstName(personData.get("firstName"));
        person.setLastName(personData.get("lastName"));
        person.setGender(Gender.valueOf(personData.get("gender").toUpperCase()));
        person.setBirthday(LocalDate.parse(personData.get("birthday")));
        person.setCity(personData.get("city"));
        person.setEmail(personData.get("email"));
        person.setPhoneNumbers(getListOfPhoneNumbersFromData(personData));

        return person;
    }


    private static List<PhoneNumber> getListOfPhoneNumbersFromData(Map<String, String> personData) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        String workPhoneNumber = personData.get("workPhoneNumber");
        String homePhoneNumber = personData.get("homePhoneNumber");

        if (!workPhoneNumber.isEmpty()) {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setPhone(workPhoneNumber);
            phoneNumber.setType(PhoneType.WORK);
            phoneNumbers.add(phoneNumber);

        }

        if (!homePhoneNumber.isEmpty()) {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setPhone(homePhoneNumber);
            phoneNumber.setType(PhoneType.HOME);
            phoneNumbers.add(phoneNumber);
        }

        return phoneNumbers;
    }


    private static String getDelimiter() throws ServiceException {
        // get delimiter from properties file
        try {
            return PropertyLoader.getDelimiter();
        } catch (IOException e) {
            throw new ServiceException("could not read properties file", e);
        }
    }
}
