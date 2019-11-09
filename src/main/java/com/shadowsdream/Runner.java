package com.shadowsdream;

import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.exception.DeleteOperationException;
import com.shadowsdream.exception.InsertOperationException;
import com.shadowsdream.exception.UpdateOperationException;
import com.shadowsdream.model.enums.Gender;
import com.shadowsdream.model.enums.Person;
import com.shadowsdream.model.PhoneNumber;
import com.shadowsdream.model.PhoneType;
import com.shadowsdream.service.PersonService;
import com.shadowsdream.service.PersonServiceImpl;
import com.shadowsdream.util.FileReader;
import com.shadowsdream.util.JdbcUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Runner {

    private final static String TABLE_INITIALIZATION_SQL_FILE = "db/migration/table_initialization.sql";
    private final static String TABLE_POPULATION_SQL_FILE = "db/migration/table_population.sql";

    private static DataSource dataSource;
    private static PersonService personService;

    private static final int SECOND_ARGUMENT_POSITION = 2;

    private static final String menu =
            "|======================================================================|\n" +
            "|                   Select an action on contact list                   |\n" +
            "|----------------------------------------------------------------------|\n" +
            "|<1>                         - show all contacts                       |\n" +
            "|<2> <id>                    - view details of contact                 |\n" +
            "|<3> <id>                    - remove contact                          |\n" +
            "|<4> <id>                    - remove phone number                     |\n" +
            "|<5>                         - save default contact                    |\n" +
            "|<6> <id> <name>             - update contact name                     |\n" +
            "|<7> <id> <x-xxx-xxx-xxx>    - update phone number (guess an id)       |\n" +
            "|<8>                         - show menu                               |\n" +
            "|<9>                         - exit                                    |\n" +
            "|======================================================================|";

    public static void main(String[] args) {

        initDatasource();
        initPersonservice();
        initTablesInDB();
        populateTablesInDB();


        Scanner scanner = new Scanner(System.in);

        Pattern firstOption = Pattern.compile("^1$");
        Pattern secondOption = Pattern.compile("^2 \\d+$");
        Pattern thirdOption = Pattern.compile("^3 \\d+$");
        Pattern forthOption = Pattern.compile("^4 \\d+$");
        Pattern fifthOption = Pattern.compile("^5$");
        Pattern sixthOption = Pattern.compile("^6 \\d+ \\w+$");
        Pattern seventhOption = Pattern.compile("^7 \\d+ [0-9]+-[0-9]+-[0-9]+-[0-9]+$");
        Pattern menuOption = Pattern.compile("^8$");
        Pattern exitOption = Pattern.compile("^9$");

        System.out.println(menu);
        String choice = null;
        while (true) {
            choice = scanner.nextLine();

            if (firstOption.matcher(choice).matches()) {
                personService.findAll().stream()
                        .map(contact -> new StringBuilder(contact.getId().toString())
                                .append(" ")
                                .append(contact.getFirstName())
                                .append(" ")
                                .append(contact.getLastName()))
                        .forEach(System.out::println);

            } else if (secondOption.matcher(choice).matches()) {
                System.out.println(personService.findById(getSecondArgumentFromStringAsLong(choice)));

            } else if (thirdOption.matcher(choice).matches()) {
                try {
                    personService.removePerson(getSecondArgumentFromStringAsLong(choice));
                    System.out.println("Contact was successfully removed from contact list");
                } catch (DeleteOperationException e) {                     //does exception name violates encapsulation?
                    System.out.println("Could not remove contact list because of: " + e.getMessage());
                }

            } else if (forthOption.matcher(choice).matches()) {
                try {
                    personService.removePhoneNumber(getSecondArgumentFromStringAsLong(choice));
                    System.out.println("Phone number was successfully removed from contact list");
                } catch (DeleteOperationException e) {
                    System.out.println("Could not remove contact list because of: " + e.getMessage());
                }

            } else if (fifthOption.matcher(choice).matches()) {
                try {
                    personService.save(getDefaultPerson());
                    System.out.println("Contact saved successfully");
                } catch (InsertOperationException e) {                    // doesexception name violates encapsulation?
                    System.out.println("Could not save contact because of: " + e.getMessage());
                }

            } else if (sixthOption.matcher(choice).matches()) {
                try {
                    // on this stage working with 1 digit only
                    Person person = personService.findById(Long.parseLong(choice.substring(SECOND_ARGUMENT_POSITION, 3)));
                    person.setFirstName(getThirdArgumentFromString(choice));
                    personService.updatePerson(person);
                    System.out.println("Contact updated successfully");
                } catch (UpdateOperationException e) {                  //does exception name violates encapsulation?
                    System.out.println("Could not update person because of: " + e.getMessage());
                }

            } else  if (seventhOption.matcher(choice).matches()) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setId(1L);
                phoneNumber.setPhone(getThirdArgumentFromString(choice));
                phoneNumber.setType(PhoneType.MOBILE);
                try {
                    personService.updatePhoneNumber(phoneNumber);
                } catch (UpdateOperationException e) {
                    System.out.println("Could not update phone number " + e.getMessage());
                }
                System.out.println("Phone number updated successfully");
            } else if (menuOption.matcher(choice).matches()) {
                System.out.println(menu);

            } else if (exitOption.matcher(choice).matches()) {
                break;

            } else {
                System.out.println("You should choose number from the menu below\n" + menu);
            }
        }
    }

    private static Person getDefaultPerson() {
        Person person = new Person();
        person.setFirstName("Blah");
        person.setLastName("Blaher");
        person.setGender(Gender.TRANSGENDER);
        person.setBirthday(LocalDate.now());
        person.setCity("Blah Blahcisco");
        person.setEmail("blah@b.com");
        PhoneNumber phoneNumber1 = new PhoneNumber();
        phoneNumber1.setPhone("123");
        phoneNumber1.setType(PhoneType.MOBILE);
        PhoneNumber phoneNumber2 = new PhoneNumber();
        phoneNumber2.setPhone("421");
        phoneNumber2.setType(PhoneType.DESKTOP);
        person.setPhoneNumbers(Arrays.asList(phoneNumber1, phoneNumber2));

        return person;
    }


    private static String getThirdArgumentFromString(String str) {
        return str.substring(SECOND_ARGUMENT_POSITION + 1);
    }


    private static Long getSecondArgumentFromStringAsLong(String str) {
       return Long.parseLong(str.substring(SECOND_ARGUMENT_POSITION));
    }


    private static void initDatasource() {
        dataSource = JdbcUtil.createPostgresDataSource(
                "jdbc:postgresql://localhost:5432/contact_list_db", "postgres", "Password1");
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