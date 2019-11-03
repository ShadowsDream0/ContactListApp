package com.shadowsdream;

import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.service.PersonService;
import com.shadowsdream.service.PersonServiceImpl;
import com.shadowsdream.util.FileReader;
import com.shadowsdream.util.JdbcUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Runner {

    private final static String TABLE_INITIALIZATION_SQL_FILE = "db/migration/table_initialization.sql";
    private final static String TABLE_POPULATION_SQL_FILE = "db/migration/table_population.sql";

    private static DataSource dataSource;
    private static PersonService personService;


    public static void main(String[] args) {

        initDatasource();
        initPersonservice();
        initTablesInDB();
        populateTablesInDB();

        /*personService.findAll().stream()
                .forEach(System.out::println);
        System.out.println("==========================");*/

        System.out.println(personService.findById(5L));
        System.out.println("==========================");
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