package com.shadowsdream;

import com.shadowsdream.dao.PhoneNumberDao;
import com.shadowsdream.dao.PhoneNumberDaoImpl;
import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.model.Gender;
import com.shadowsdream.model.Person;
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

    private static PhoneNumberDao phoneNumberDao;


    public static void main(String[] args) {

        initDatasource();
        initPersonservice();

        initPhoneDao();

        initTablesInDB();
        populateTablesInDB();


        /*
        Scanner scanner = new Scanner(System.in);

        Pattern namePattern = Pattern.compile("^[A-Za-z]{2,15}$");


        String choice = null;
        while (true) {
            System.out.println("Select an action on data base:\n" +
                                "<1>                 - show all records of persons\n" +
                                "<2> <id>            - find person by id\n" +
                                "<3> <id> <new name> - update name of person\n" +
                                "<4> <id>            - save new default person\n" +
                                "<5> <id>            - remove record by person id\n" +
                                "<6> <id>            - show all phone numbers by person id\n" +
                                "<7> ");

            choice = scanner.next();
            switch (choice) {
                case "1":
                    System.out.println(personService.findAll());
                    break;
                case "2":
                    System.out.println(personService.findById());
                    break;
            }
        }*/

        /*PhoneNumber phoneNumber = new PhoneNumber("123", PhoneType.MOBILE);
        PhoneNumber phoneNumber1 = new PhoneNumber("456", PhoneType.DESKTOP);
        PhoneNumber phoneNumber2 = new PhoneNumber("657", PhoneType.MOBILE);

        System.out.println(phoneNumberDao.savePhoneNumbers(1L, Arrays.asList(phoneNumber, phoneNumber1)));
        System.out.println(phoneNumberDao.savePhoneNumbers(2L, Arrays.asList(phoneNumber2)));*/

        PhoneNumber phoneNumber = new PhoneNumber("123", PhoneType.MOBILE);
        phoneNumberDao.updatePhoneNumber(1L, phoneNumber);


        /*personService.findAll().stream()
                .forEach(System.out::println);
        System.out.println("==========================");*/

       /* Person person = personService.findById(7L);
        System.out.println(person);
        System.out.println("==========================");*/

        /*
        person.setFirstName("UPDATED");
        personService.update(person);
        System.out.println(personService.findById(5L));
        System.out.println("==========================");*/

        /*Person person = new Person();
        person.setId(11L);
        person.setFirstName("Blah");
        person.setLastName("Blahson");
        person.setEmail("blah.com");
        person.setCity("Blah");
        person.setGender(Gender.MALE);
        person.setBirthday(LocalDate.now());
        person.setPhoneNumbers(Arrays.asList(new PhoneNumber("12345", PhoneType.MOBILE),
                                                new PhoneNumber("54321", PhoneType.DESKTOP)));
        Long id = personService.save(person);
        System.out.println(personService.findById(id));
        System.out.println("==========================");*/


//        personService.remove(id);
    }

    private static void initPhoneDao() {
         phoneNumberDao = new PhoneNumberDaoImpl(dataSource);
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