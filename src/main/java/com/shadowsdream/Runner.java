package com.shadowsdream;

import com.shadowsdream.dto.PersonDto;
import com.shadowsdream.dto.PersonSaveDto;
import com.shadowsdream.dto.PhoneNumberDto;
import com.shadowsdream.dto.PhoneNumberSaveDto;
import com.shadowsdream.dto.mappers.PhoneNumberDtoMapper;
import com.shadowsdream.dto.mappers.PhoneNumberSaveDtoMapper;
import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.exception.DeleteOperationException;
import com.shadowsdream.exception.InsertOperationException;
import com.shadowsdream.exception.UpdateOperationException;
import com.shadowsdream.model.enums.Gender;
import com.shadowsdream.model.enums.PhoneType;
import com.shadowsdream.service.PersonService;
import com.shadowsdream.service.PersonServiceImpl;
import com.shadowsdream.service.PrettyPrinter;
import com.shadowsdream.util.FileReader;
import com.shadowsdream.util.JdbcUtil;
import com.shadowsdream.util.logging.ContactListLogger;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Runner {

    private final static String TABLE_INITIALIZATION_SQL_FILE = "db/migration/table_initialization.sql";
    private final static String TABLE_POPULATION_SQL_FILE = "db/migration/table_population.sql";

    private static DataSource dataSource;
    private static PersonService personService;

    private static final Scanner scanner = new Scanner(System.in);

    private static String dataBase;


    public static void main(String[] args) {

        if(args.length != 1) {
            System.err.println("Usage: java -jar cl-ap.jar <database_name>");
            System.exit(1);
        }

        dataBase = args[0];

        initializeDataBase();

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
                    break;
                case "10":
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("You should choose number from the menu below\n");
                    break;
            }

            PrettyPrinter.printPressAnyKey();
            scanner.nextLine();
        }
    }


    private static void savePerson() {
        try {
            PersonSaveDto personSaveDto = getPersonFromInputForSaving();
            personService.save(personSaveDto);
            System.out.println("Contact was saved successfully");
            PrettyPrinter.printPersonInfo(personSaveDto);
        } catch (InsertOperationException e) {
            System.out.println("Could not save contact because of: " + e.getMessage());
        }
    }


    private static PersonSaveDto getPersonFromInputForSaving() {
        System.out.println("Provide information for a new contact:");

        PersonSaveDto personSaveDto = new PersonSaveDto();

        System.out.print("Enter first name\n->");
        String firstName = scanner.nextLine();
        personSaveDto.setFirstName(firstName);
        System.out.println("First name has been set successfully");

        System.out.print("Enter last name\n->");
        String lastName = scanner.nextLine();
        personSaveDto.setLastName(lastName);
        System.out.println("Last name has been set successfully");

        personSaveDto.setGender(scanGender());
        System.out.println("Gender has been set successfully");

        personSaveDto.setBirthday(scanBirthday());
        System.out.println("Birthday has been set successfully");

        System.out.print("Enter city\n->");
        String city = scanner.nextLine();
        personSaveDto.setCity(city);
        System.out.println("City has been set successfully");

        System.out.println("Enter email\n->");
        String email = scanner.nextLine();
        personSaveDto.setEmail(email);
        System.out.println("Email has been set successfully");

        personSaveDto.setPhoneNumbers(scanPhoneNumbers());
        System.out.println("All phone numbers have been set successfully");

        return personSaveDto;
    }


    private static void removePhoneNumber() {
        System.out.println("Enter phone id which you want to delete from contact list");
        Long id = scanLong();

        try {
            personService.removePhoneNumber(id);
            System.out.println("Phone number was successfully removed from contact list");
        } catch (DeleteOperationException e) {                     //does exception name violates encapsulation?
            System.out.println("Could not remove phone number list because of: " + e.getMessage());
        }
    }


    private static void removeContact() {

        System.out.println("Enter person id who you want to delete from contact list");
        Long id = scanLong();

        try {
            personService.removePerson(id);
            System.out.println("Contact was successfully removed from contact list");
        } catch (DeleteOperationException e) {                     //does exception name violates encapsulation?
            System.out.println("Could not remove contact list because of: " + e.getMessage());
        }
    }


    private static void showDetails() {
        System.out.println("Enter person id to view contact details");

        Long id = scanLong();

        PrettyPrinter.printPersonInfo(personService.findById(id));
    }


    private static void showAll() {
        personService.findAll().forEach(PrettyPrinter::printPersonInfo);
    }


    private static void importContacts() {
        // get path from input
        boolean successfulInput = false;
        String fileName = null;
        Path filePath = null;
        do {
            System.out.println("Enter file path");
            fileName = scanner.nextLine();
            filePath = Path.of(fileName);
            if (!(Files.exists(filePath))) {
                System.err.println("You must enter valid path to the file");
                continue;
            }

            ContactListLogger.getLog().debug("Path to file got from input: " + filePath);

            // read person data from file
            List<String[]> linesWithPersonData = FileReader.getListOfStringArraysFromPath(filePath);

            // save contacts to db
            PersonSaveDto personSaveDto = null;
            int sizeOfList = linesWithPersonData.size();
            for(int line = 0; line < sizeOfList; line++) {
                try {
                    personSaveDto = getPersonSaveDtoFromData(parsePersonData(linesWithPersonData.get(line)));
                } catch (IOException e) {
                    System.err.println("Corrupted data on line " + line + ": " + e.getMessage());
                    break;
                }
                personService.save(personSaveDto);
                line++;
            }

            successfulInput = true;

        } while (!successfulInput);

        System.out.println("Contacts have been imported successfully");
    }


    private static PersonSaveDto getPersonSaveDtoFromData(Map<String, String> personData) {
        Objects.requireNonNull(personData, "Argument personData must not be null");

        return PersonSaveDto.builder()
                .firstName(personData.get("firstName"))
                .lastName(personData.get("lastName"))
                .gender(Gender.valueOf(personData.get("gender").toUpperCase()))
                .birthday(LocalDate.parse(personData.get("birthday")))
                .city(personData.get("city"))
                .email(personData.get("email"))
                .phoneNumbers(getListOfPhoneNumbersFromData(personData))
                .build();
    }


    private static List<PhoneNumberSaveDto> getListOfPhoneNumbersFromData(Map<String, String> personData) {
        List<PhoneNumberSaveDto> phoneNumberDtos = new ArrayList<>();
        String workPhoneNumber = personData.get("workPhoneNumber");
        String homePhoneNumber = personData.get("homePhoneNumber");

        if (!workPhoneNumber.isEmpty()) {
            phoneNumberDtos.add(PhoneNumberSaveDto.builder()
                                .phone(workPhoneNumber)
                                .type(PhoneType.WORK)
                                .build());
        } else if (!homePhoneNumber.isEmpty()) {
            phoneNumberDtos.add(PhoneNumberSaveDto.builder()
                                .phone(homePhoneNumber)
                                .type(PhoneType.HOME)
                                .build());
        }

        return phoneNumberDtos;
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
                throw new IOException("person data contains null reference at " + (i + 1) + " position");
            } else {
                mapPersonData.put(keys[i], personData[i]);
            }
        }

        return mapPersonData;
    }


    private static void updatePerson() {

        System.out.print("Enter person id whose contact you want to update\n->");
        Long id = scanLong();

        PersonDto personDto = setPersonFromInputForUpdate(personService.findById(id));

        try {
            personService.updatePerson(personDto);
            System.out.println("Contact updated successfully:");
            PrettyPrinter.printPersonInfo(personDto);
        } catch (UpdateOperationException e) {                  //does exception name violates encapsulation?
            System.out.println("Could not update person because of: " + e.getMessage());
        }
    }


    private static void updatePhoneNumbers() {
        System.out.println("Enter person id whose phone number you want to update");
        Long id = scanLong();

        PersonDto personDto = personService.findById(id);

        List<PhoneNumberDto> dtoPhoneNumbers = personDto.getPhoneNumbers();

        boolean done = false;
        do {
            // get new phone number from input
            System.out.print("Select action:\n1 - update phone number\n2 - done\n->");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        PhoneNumberDto updatedPhoneNumberDto = getUpdatedPhoneNumber(dtoPhoneNumbers);
                        personService.updatePhoneNumber(updatedPhoneNumberDto);
                        System.out.println("Phone number has been set successfully");
                        PrettyPrinter.printPhoneNumber(updatedPhoneNumberDto);
                    } catch (UpdateOperationException e) {
                        System.out.println("Could not update phone number because of: " + e.getMessage());
                    }
                    break;
                case "2":
                    done = true;
                    break;
                default:
                    System.out.println("You must choose from 2 options only");
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
            System.out.print(menu);

            choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print("Enter first name\n->");
                    String firstName = scanner.nextLine();
                    personDto.setFirstName(firstName);
                    System.out.println("First name has been set successfully");
                    break;
                case "2":
                    System.out.print("Enter last name\n->");
                    String lastName = scanner.nextLine();
                    personDto.setLastName(lastName);
                    System.out.println("Last name has been set successfully");
                    break;
                case "3":
                    personDto.setGender(scanGender());
                    System.out.println("Gender has been set successfully");
                    break;
                case "4":
                    personDto.setBirthday(scanBirthday());
                    System.out.println("Birthday has been set successfully");
                    break;
                case "5":
                    System.out.print("Enter city\n->");
                    String city = scanner.nextLine();
                    personDto.setCity(city);
                    System.out.println("City has been set successfully");
                    break;
                case "6":
                    System.out.print("Enter email\n->");
                    String email = scanner.nextLine();
                    personDto.setEmail(email);
                    System.out.println("Email has been set successfully");
                    break;
                case "7":
                    successfulInput = true;
                    break;
                default:
                    System.out.print("You must choose a number from menu below\n");
                    break;
            }
        } while (!successfulInput);

        return personDto;
    }


    private static PhoneNumberDto getUpdatedPhoneNumber(List<PhoneNumberDto> inputDtoPhoneNumbers) {
        System.out.print("Enter phone number id\n->");
        Long id = scanLong();
        System.out.print("Enter new phone number (+x-xxx-xxx-xxxx)\n->");
        String phoneNumber = scanner.nextLine();
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
            System.out.print("Select action:\n1 - save phone number\n2 - done\n->");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    PhoneNumberSaveDto phoneNumberSaveDto = new PhoneNumberSaveDto();
                    System.out.print("Enter phone number (+x-xxx-xxx-xxxx)\n->");
                    String phoneNumber = scanner.nextLine();
                    phoneNumberSaveDto.setPhone(phoneNumber);

                    boolean successfulInput = false;
                    do {
                        System.out.print("Enter phone type (1 - home, 2 - work)\n->");
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
                                System.out.println("You must choose from 2 options only");
                                break;
                        }
                    } while (!successfulInput);

                    dtoPhoneNumbers.add(phoneNumberSaveDto);
                    System.out.println("Phone number has been set successfully:");

                    // bad code
                    PhoneNumberSaveDtoMapper saveDtoMapper = Mappers.getMapper(PhoneNumberSaveDtoMapper.class);
                    PhoneNumberDtoMapper dtoMapper = Mappers.getMapper(PhoneNumberDtoMapper.class);
                    PrettyPrinter.printPhoneNumber(dtoMapper.toDto(saveDtoMapper.fromDto(phoneNumberSaveDto)));

                    break;
                case "2":
                    done = true;
                    break;
                default:
                    System.out.println("You must choose from 2 options only");
                    break;
            }
        } while (!done);

        return dtoPhoneNumbers;
    }


    private static LocalDate scanBirthday() {
        LocalDate birthday = null;
        boolean successfulInput = false;
        do {
            System.out.print("Enter date of birth (yyyy-mm-dd)\n->");
            try {
                birthday = LocalDate.parse(scanner.nextLine());
                successfulInput = true;
            } catch (DateTimeParseException e) {
                System.out.println("You must enter valid date");
            }
        } while (!successfulInput);

        return  birthday;
    }


    private static Gender scanGender() {
        boolean successfulInput = false;
        Gender gender = null;
        do {
            System.out.print("Choose number to select gender (1 - male, 2 - female, 3 - transgender\n->");
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
                    System.out.println("You must choose from 3 options only");
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
                System.out.println("You must enter a number");
            }
        } while (!successfulInput);
        return id;
    }


    private static void initializeDataBase() {
        initDatasource();
        initPersonservice();
        initTablesInDB();
        populateTablesInDB();
    }

    private static void initDatasource() {
        dataSource = JdbcUtil.createPostgresDataSource(
                "jdbc:postgresql://localhost:5432/" + dataBase, "postgres", "Password1");
    }

    private static void initPersonservice() {
        personService = new PersonServiceImpl(dataSource);
    }

    private static void initTablesInDB() {
        String createTablesSql = FileReader.readWholeFileFromResources(TABLE_INITIALIZATION_SQL_FILE);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(createTablesSql);
            connection.commit();
        } catch (SQLException e) {
            throw new DaoOperationException("Error during tables init.", e);
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
            throw new DaoOperationException("Error during tables population.", e);
        }
    }
}