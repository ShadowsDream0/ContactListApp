package com.shadowsdream;

import com.shadowsdream.dao.PersonDao;
import com.shadowsdream.dao.implementations.PersonDaoImpl;
import com.shadowsdream.dto.*;
import com.shadowsdream.dto.mappers.*;
import com.shadowsdream.exception.InvalidInputException;
import com.shadowsdream.exception.PersonServiceException;
import com.shadowsdream.exception.ServiceException;
import com.shadowsdream.model.enums.*;
import com.shadowsdream.service.*;
import com.shadowsdream.service.implementations.*;
import com.shadowsdream.util.JdbcUtil;
import com.shadowsdream.util.io.FileReader;
import com.shadowsdream.util.logging.ContactListLogger;
import org.mapstruct.factory.Mappers;

import javax.sql.DataSource;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Runner {

    private final static String TABLE_INITIALIZATION_SQL_FILE = "db/migration/table_initialization.sql";
    private final static String TABLE_POPULATION_SQL_FILE = "db/migration/table_population.sql";

    private static DataSource dataSource;
    private static PersonService personService;
    private static ValidatorService validatorService;
    private static ImportExportService importExportService;
    private static EmailSenderServiceImpl emailSenderServiceImpl;


    private static final Scanner scanner = new Scanner(System.in);

    private static String dataBase;

    private static boolean scanningEnabled = false;
    private static Path folderToScan = null;
    private static PersonDao personDao = null;
    private static boolean emailSendingNotSupported = false;


    public static void main(String[] args) {

        if(args.length != 1) {
            System.err.println("Usage: java -jar cl-ap.jar <database_name>");
            System.exit(1);
        }

        dataBase = args[0];

        initializeAll();

        String choice = null;
        while (true) {
            PrettyPrinter.printMenu();

            choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    showAll();
                    break;
                case "2":
                    showDetails();
                    break;
                case "3":
                    removeContact();
                    break;
                case "4":
                    removePhoneNumber();
                    break;
                case "5":
                    savePerson();
                    break;
                case "6":
                    updatePerson();
                    break;
                case "7":
                    updatePhoneNumbers();
                    break;
                case "8":
                    importContacts();
                    break;
                case "9":
                    exportContacts();
                    break;
                case "10":
                    sendContactsByEmail();
                    break;
                case "11":
                    setScanningMode();
                    break;
                case "12":
                    PrettyPrinter.print("Exiting...\n");
                    System.exit(0);
                default:
                    PrettyPrinter.print("You should choose number from the menu below\n");
                    break;
            }

            PrettyPrinter.printPressAnyKey();
            scanner.nextLine();
        }
    }

    private static void setScanningMode(){
        MAIN_LOOP: do {
            PrettyPrinter.print("Do you want to activate import from folder?\ny/n: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "y":
                    if (scanningEnabled) {
                        PrettyPrinter.print("Already set to scan folder: " + folderToScan.getFileName());
                        break;
                    }

                    scanningEnabled = true;
                    folderToScan = getFilePath();
                    PrettyPrinter.print("Folder scanning activated\n");
                    ContactListLogger.getLog().debug("Path to folder to scan got from input: " + folderToScan);
                    break MAIN_LOOP;

                case "n":
                    scanningEnabled = false;
                    folderToScan = null;
                    PrettyPrinter.print("Folder scanning deactivated\n");
                    break MAIN_LOOP;

                default:
                    PrettyPrinter.print("Try again\n");
                    break;
            }
        } while (true);

    }

    private static Path getFilePath() {
        boolean successfulInput = false;
        String fileName = null;
        Path filePath = null;

        do {
            PrettyPrinter.print("Enter file path\n");
            fileName = scanner.nextLine();
            filePath = Path.of(fileName);
            if (!(Files.exists(filePath))) {
                PrettyPrinter.print("You must enter valid path to the file\n");
            } else {
                successfulInput = true;
            }

        } while (!successfulInput);

        return filePath;
    }


    private static void savePerson() {
        try {
            PersonSaveDto personSaveDto = getPersonFromInputForSaving();
            personService.save(personSaveDto);
            PrettyPrinter.print("Contact was saved successfully\n");
            PrettyPrinter.printPersonInfo(personSaveDto);
        } catch (PersonServiceException e) {
            PrettyPrinter.printError("Could not save contact because of " + e.getMessage() + "\n");
        }
    }


    private static PersonSaveDto getPersonFromInputForSaving() {
        PrettyPrinter.print("Provide information for a new contact:\n");

        PersonSaveDto personSaveDto = new PersonSaveDto();

        // get first name
        do {
            PrettyPrinter.print("Enter first name\n->");
            String firstName = scanner.nextLine();
            try {
                validatorService.validateProperName(firstName);
            } catch (InvalidInputException e) {
                PrettyPrinter.print("You failed to enter correct first name because " + e.getMessage() + "\n");
                continue;
            }
            personSaveDto.setFirstName(firstName);
            PrettyPrinter.print("First name has been set successfully\n");
            break;
        } while (true);

        // get last name
        do {
            PrettyPrinter.print("Enter last name\n->");
            String lastName = scanner.nextLine();
            try {
                validatorService.validateProperName(lastName);
            } catch (InvalidInputException e) {
                PrettyPrinter.print("You failed to enter correct last name because " + e.getMessage() + "\n");
                continue;
            }
            personSaveDto.setLastName(lastName);
            PrettyPrinter.print("Last name has been set successfully\n");
            break;
        } while (true);

        // get gender
        personSaveDto.setGender(scanGender());
        PrettyPrinter.print("Gender has been set successfully\n");

        // get birthday
        personSaveDto.setBirthday(scanBirthday());
        PrettyPrinter.print("Birthday has been set successfully\n");

        // get city
        do {
            PrettyPrinter.print("Enter city\n->");
            String city = scanner.nextLine();
            try {
                validatorService.validateProperName(city);
            } catch (InvalidInputException e) {
                PrettyPrinter.print("You failed to enter correct name of the city because " + e.getMessage() + "\n");
                continue;
            }
            personSaveDto.setCity(city);
            PrettyPrinter.print("City has been set successfully\n");
            break;
        } while (true);

        // get email
        do {
            PrettyPrinter.print("Enter email\n->");
            String email = scanner.nextLine();
            try {
                validatorService.validateEmail(email);
            } catch (InvalidInputException e) {
                PrettyPrinter.print("You failed to enter correct email because " + e.getMessage() + "\n");
                continue;
            }
            personSaveDto.setEmail(email);
            PrettyPrinter.print("Email has been set successfully\n");
            break;
        } while (true);

        // get phone numbers
        personSaveDto.setPhoneNumbers(scanPhoneNumbers());
        PrettyPrinter.print("All phone numbers have been set successfully\n");

        return personSaveDto;
    }


    private static void removePhoneNumber() {
        PrettyPrinter.print("Enter phone id which you want to delete from contact list\n");
        Long id = scanLong();

        try {
            personService.removePhoneNumber(id);
            PrettyPrinter.print("Phone number was successfully removed from contact list\n");
        } catch (PersonServiceException e) {
            PrettyPrinter.print("Operation failed because " + e.getMessage() + "\n");
        }
    }


    private static void removeContact() {
        PrettyPrinter.print("Enter person id who you want to delete from contact list\n");
        Long id = scanLong();

        try {
            personService.removePerson(id);
            PrettyPrinter.print("Contact was successfully removed from contact list\n");
        } catch (PersonServiceException e) {
            PrettyPrinter.print("Operation failed because " + e.getMessage() + "\n");
        }
    }


    private static void showDetails() {
        PrettyPrinter.print("Enter person id to view contact details\n");

        Long id = scanLong();

        try {
            PrettyPrinter.printPersonInfo(personService.findById(id));
        } catch (PersonServiceException e) {
            PrettyPrinter.print("Could not show information because " + e.getMessage());
        }
    }


    private static void showAll() {
        personService.findAll().forEach(PrettyPrinter::printPersonInfo);
    }


    private static void sendContactsByEmail() {
        boolean successfulInput = false;

        if (emailSendingNotSupported) {
            PrettyPrinter.printError("Email sending feature not supported due to missing file with credentials\n");
            return;
        }

        do {
            PrettyPrinter.print("Select action: 1 - send contacts in a message, 2 - send contacts in an attached file\n");
            String choice = scanner.nextLine();
            PrettyPrinter.print("Enter email address\n");
            String recipient = scanner.nextLine();

            switch (choice) {
                case "1":
                    try {
                        emailSenderServiceImpl.sendEmail(importExportService.getContactsLines(), recipient);
                        PrettyPrinter.print("Email was sent successfully\n");
                        successfulInput = true;
                    } catch (ServiceException e) {
                        PrettyPrinter.printError("Can not send email: " + e.getMessage() + "\n");
                        break;
                    }

                    break;

                case "2":
                    PrettyPrinter.print("Enter path of a file you want to send\n");
                    String attachmentPath = scanner.nextLine();
                    try {
                        emailSenderServiceImpl.sendEmailWithAttachment(recipient, attachmentPath);
                        PrettyPrinter.print("Email was sent successfully\n");
                        successfulInput = true;
                    } catch (ServiceException e) {
                        PrettyPrinter.printError("Can not send email: " + e.getMessage() + "\n");
                        break;
                    }

                    break;

                default:
                    PrettyPrinter.print("You must choose only from two options below\n");
                    break;
            }

        } while (!successfulInput);

    }

    private static void importContacts() {

        if (scanningEnabled) {

            try {
                importExportService.importFromFolder(folderToScan);
                PrettyPrinter.print("Contacts have been imported successfully\n");
            } catch (ServiceException e) {
                PrettyPrinter.print("Could not import contacts: " + e.getMessage() + "\n");
            }

        } else {
            Path filePath = getFilePath();

            ContactListLogger.getLog().debug("Path to file got from input: " + filePath);

            try {
                importExportService.importFromFile(filePath);
                PrettyPrinter.print("Contacts have been imported successfully\n");
            } catch (ServiceException e) {
                PrettyPrinter.print("Could not import contacts: " + e.getMessage() + "\n");
            }
        }


    }

    private static void exportContacts() {
        String fileName = null;

        boolean successfulInput = false;
        do {
            PrettyPrinter.print("Enter file path\n");
            fileName = scanner.nextLine();
            String fileSeparator = FileSystems.getDefault().getSeparator();

            if (!fileName.contains(fileSeparator)) {
                PrettyPrinter.print("You must enter valid directory path\n");
                continue;
            }

            String directory = fileName.substring(0, fileName.lastIndexOf(fileSeparator));
            Path directoryPath = Path.of(directory);

            if (!(Files.exists(directoryPath))) {
                PrettyPrinter.print("You must enter valid directory path\n");
                ContactListLogger.getLog().debug("Directory path from input");
            } else {
                successfulInput = true;
            }
        } while (!successfulInput);

        try {
            importExportService.exportToFile(Path.of(fileName));
            PrettyPrinter.print("Contacts have been exported successfully\n");
        } catch (ServiceException e) {
            PrettyPrinter.print("Could not export contacts to the file: " + e.getMessage());
        }
    }


    private static void updatePerson() {

        PrettyPrinter.print("Enter person id whose contact you want to update\n->");
        Long id = scanLong();

        PersonDto personDto = null;

        try {
            PersonDto bufferPersonDto = personService.findById(id);
            personDto = setPersonFromInputForUpdate(bufferPersonDto);
        } catch (PersonServiceException e) {
            PrettyPrinter.print("Could not show information because " + e.getMessage());
            return;
        }

        personService.updatePerson(personDto);
        PrettyPrinter.print("Contact updated successfully:\n");
        PrettyPrinter.printPersonInfo(personDto);
    }


    private static void updatePhoneNumbers() {
        PrettyPrinter.print("Enter person id whose phone number you want to update\n");
        Long id = scanLong();

        PersonDto personDto = null;
        try {
            personDto = personService.findById(id);
        } catch (PersonServiceException e) {
            PrettyPrinter.print("Could not update because " + e.getMessage());
            return;
        }

        List<PhoneNumberDto> dtoPhoneNumbers = personDto.getPhoneNumbers();

        if (dtoPhoneNumbers == null) {
            PrettyPrinter.print("Contact has no phone numbers yet\n");
            return;
        }

        boolean done = false;
        do {
            // get new phone number from input
            PrettyPrinter.print("Select action:\n1 - update phone number\n2 - done\n->");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        PhoneNumberDto updatedPhoneNumberDto = getUpdatedPhoneNumber(dtoPhoneNumbers);
                        personService.updatePhoneNumber(updatedPhoneNumberDto);
                        PrettyPrinter.print("Phone number has been set successfully\n");
                        PrettyPrinter.printPhoneNumber(updatedPhoneNumberDto);
                    } catch (PersonServiceException e) {
                        PrettyPrinter.print("Could not update phone number because of: " + e.getMessage() + "\n");
                    }
                    break;
                case "2":
                    done = true;
                    break;
                default:
                    PrettyPrinter.print("You must choose from 2 options only\n");
                    break;
            }
        } while (!done);
    }


    private static PersonDto setPersonFromInputForUpdate(PersonDto inputPersonDto) {
        final String menu = "Select a number below to update information about person accordingly:\n" +
                "1 - first name\n" +
                "2 - last name\n" +
                "3 - gender\n" +
                "4 - birthday\n" +
                "5 - city\n" +
                "6 - email\n" +
                "7 - done\n" +
                "->";
        boolean successfulInput = false;
        String choice = "";
        PersonDto personDto = PersonDto.builder()
                .id(inputPersonDto.getId())
                .firstName(inputPersonDto.getFirstName())
                .lastName(inputPersonDto.getLastName())
                .gender(inputPersonDto.getGender())
                .birthday(inputPersonDto.getBirthday())
                .city(inputPersonDto.getCity())
                .email(inputPersonDto.getEmail())
                .build();

        do {
            PrettyPrinter.print(menu);

            choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    PrettyPrinter.print("Enter first name\n->");
                    String firstName = scanner.nextLine();
                    try {
                        validatorService.validateProperName(firstName);
                    } catch (InvalidInputException e) {
                        PrettyPrinter.print("You failed to enter correct name " + e.getMessage() + "\n");
                        continue;
                    }
                    personDto.setFirstName(firstName);
                    PrettyPrinter.print("First name has been set successfully\n");
                    break;
                case "2":
                    PrettyPrinter.print("Enter last name\n->");
                    String lastName = scanner.nextLine();
                    try {
                        validatorService.validateProperName(lastName);
                    } catch (InvalidInputException e) {
                        PrettyPrinter.print("You failed to enter correct last name " + e.getMessage() + "\n");
                        continue;
                    }
                    personDto.setLastName(lastName);
                    PrettyPrinter.print("Last name has been set successfully\n");
                    break;
                case "3":
                    personDto.setGender(scanGender());
                    PrettyPrinter.print("Gender has been set successfully\n");
                    break;
                case "4":
                    personDto.setBirthday(scanBirthday());
                    PrettyPrinter.print("Birthday has been set successfully\n");
                    break;
                case "5":
                    PrettyPrinter.print("Enter city\n->");
                    String city = scanner.nextLine();
                    try {
                        validatorService.validateProperName(city);
                    } catch (InvalidInputException e) {
                        PrettyPrinter.print("You failed to enter correct city name " + e.getMessage() + "\n");
                        continue;
                    }
                    personDto.setCity(city);
                    PrettyPrinter.print("City has been set successfully\n");
                    break;
                case "6":
                    PrettyPrinter.print("Enter email\n->");
                    String email = scanner.nextLine();
                    try {
                        validatorService.validateEmail(email);
                    } catch (InvalidInputException e) {
                        PrettyPrinter.print("You failed to enter correct email " + e.getMessage() + "\n");
                        continue;
                    }
                    personDto.setEmail(email);
                    PrettyPrinter.print("Email has been set successfully\n");
                    break;
                case "7":
                    successfulInput = true;
                    break;
                default:
                    PrettyPrinter.print("You must choose a number from menu below\n");
                    break;
            }
        } while (!successfulInput);

        return personDto;
    }


    private static PhoneNumberDto getUpdatedPhoneNumber(List<PhoneNumberDto> inputDtoPhoneNumbers) {
        PrettyPrinter.print("Enter phone number id\n->");
        Long id = scanLong();

        String phoneNumber = null;
        do {
            PrettyPrinter.print("Enter new phone number (+x-xxx-xxx-xxxx)\n->");
            phoneNumber = scanner.nextLine();
            try {
                validatorService.validatePhoneNumber(phoneNumber);
            } catch (InvalidInputException e) {
                PrettyPrinter.print("You failed to enter correct phone number " + e.getMessage() + "\n");
                continue;
            }
            break;
        } while (true);


        PhoneNumberDto phoneNumberDto = null;

        // find old phone number to be updated
        for (PhoneNumberDto pn: inputDtoPhoneNumbers) {
            // update phone number
            if (id.equals(pn.getId())) {
                phoneNumberDto = new PhoneNumberDto(pn.getId(), phoneNumber, pn.getType());
                break;
            }
        }
        return phoneNumberDto;
    }


    private static List<PhoneNumberSaveDto> scanPhoneNumbers() {
        boolean done = false;

        List<PhoneNumberSaveDto> dtoPhoneNumbers = new ArrayList<>();

        do {
            // get new phone number from input
            PrettyPrinter.print("Select action:\n1 - save phone number\n2 - done\n->");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    PhoneNumberSaveDto phoneNumberSaveDto = new PhoneNumberSaveDto();
                    PrettyPrinter.print("Enter phone number (+x-xxx-xxx-xxxx)\n->");
                    String phoneNumber = scanner.nextLine();
                    try {
                        validatorService.validatePhoneNumber(phoneNumber);
                    } catch (InvalidInputException e) {
                        PrettyPrinter.print("You failed to enter correct phone number because " + e.getMessage() + "\n");
                        continue;
                    }
                    phoneNumberSaveDto.setPhone(phoneNumber);

                    boolean successfulInput = false;
                    do {
                        PrettyPrinter.print("Enter phone type (1 - home, 2 - work)\n->");
                        String typeNumber = scanner.nextLine();
                        switch (typeNumber) {
                            case "1":
                                phoneNumberSaveDto.setType(PhoneType.HOME);
                                successfulInput = true;
                                break;
                            case "2":
                                phoneNumberSaveDto.setType(PhoneType.WORK);
                                successfulInput = true;
                                break;
                            default:
                                PrettyPrinter.print("You must choose from 2 options only\n");
                                break;
                        }
                    } while (!successfulInput);

                    dtoPhoneNumbers.add(phoneNumberSaveDto);
                    PrettyPrinter.print("Phone number has been set successfully:\n");

                    //todo: fix bad code
                    PhoneNumberSaveDtoMapper saveDtoMapper = Mappers.getMapper(PhoneNumberSaveDtoMapper.class);
                    PhoneNumberDtoMapper dtoMapper = Mappers.getMapper(PhoneNumberDtoMapper.class);
                    PrettyPrinter.printPhoneNumber(dtoMapper.toDto(saveDtoMapper.fromDto(phoneNumberSaveDto)));

                    break;
                case "2":
                    done = true;
                    break;
                default:
                    PrettyPrinter.print("You must choose from 2 options only\n");
                    break;
            }
        } while (!done);

        return dtoPhoneNumbers;
    }


    private static LocalDate scanBirthday() {
        LocalDate birthday = null;
        do {
            PrettyPrinter.print("Enter date of birth (yyyy-mm-dd)\n->");
            String birthdayString = scanner.nextLine();
            try {
               birthday = validatorService.validateAndGetBirthday(birthdayString);
            } catch (InvalidInputException e) {
                PrettyPrinter.print("You failed to enter correct date because " + e.getMessage() + "\n");
                continue;
            }
            break;
        } while (true);

        return  birthday;
    }


    private static Gender scanGender() {
        boolean successfulInput = false;
        Gender gender = null;
        do {
            PrettyPrinter.print("Choose number to select gender (1 - male, 2 - female, 3 - transgender\n->");
            String choiceGender = scanner.nextLine();

            switch (choiceGender) {
                case "1":
                    gender = Gender.MALE;
                    successfulInput = true;
                    break;
                case "2":
                    gender = Gender.FEMALE;
                    successfulInput = true;
                    break;
                case "3":
                    gender = Gender.TRANSGENDER;
                    successfulInput = true;
                    break;
                default:
                    PrettyPrinter.print("You must choose from 3 options only\n");
            }
        } while (!successfulInput);
        return gender;
    }


    private static Long scanLong() {
        boolean successfulInput = false;
        Long id = 0L;
        do {
            try {
                id = Long.parseLong(scanner.nextLine());
                successfulInput = true;
            } catch (NumberFormatException e) {
                PrettyPrinter.print("You must enter a number\n");
            }
        } while (!successfulInput);
        return id;
    }


    private static void initializeAll() {
        initDatasource();
        initTablesInDB();
        populateTablesInDB();
        initPersonDao();
        initPersonservice();
        initValidatorService();
        initImportExportService();
        initEmailSenderService();
    }

    private static void initPersonDao() {
        personDao = new PersonDaoImpl(dataSource);
    }


    private static void initDatasource() {
        dataSource = JdbcUtil.createPostgresDataSource(
                "jdbc:postgresql://localhost:5432/" + dataBase, "postgres", "Password1");
    }


    private static void initPersonservice() {
        personService = new PersonServiceImpl(personDao);
    }


    private static void initTablesInDB() {
        String createTablesSql = FileReader.readWholeFileFromResources(TABLE_INITIALIZATION_SQL_FILE);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(createTablesSql);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error during tables initialization", e);
        }
    }


    private static void populateTablesInDB() {
        String createTablesSql = FileReader.readWholeFileFromResources(TABLE_POPULATION_SQL_FILE);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(createTablesSql);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error during tables population.", e);
        }
    }


    private static void initValidatorService() {
        validatorService = ValidatorServiceImpl.getInstance();
    }


    private static void initImportExportService() {
        importExportService = ImportExportServiceImpl.getInstance(dataSource);
    }


    private static void initEmailSenderService() {
        try {
            emailSenderServiceImpl = EmailSenderServiceImpl.getInstance();
        } catch (ServiceException e) {
            emailSendingNotSupported = true;
            PrettyPrinter.printError("Warning! File with smtp credentials not found. " +
                    "Sending emails not available\n");
        }
    }
}