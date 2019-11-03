/*package com.shadowsdream.dao;

import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.model.Gender;
import com.shadowsdream.model.Person;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PhoneNumberDaoImpl implements PhoneNumberDao {
    private DataSource dataSource;

    private static final String INSERT_SQL_STATEMENT = "INSERT INTO persons " +
            "(first_name, last_name, gender, birthday, city, email) " +
            "VALUES (?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_SQL_STATEMENT = "UPDATE persons " +
            "SET first_name = ?, " +
            "last_name = ?, " +
            "gender = ?, " +
            "birthday = ?, " +
            "city = ?, " +
            "email = ? " +
            "WHERE id = ?;";

    private static final String SELECT_ALL_SQL_STATEMENT = "SELECT * FROM persons;";

    private static final String SELECT_BY_ID_SQL_STATEMENT = "SELECT * FROM persons WHERE id = ?";

    private static final String DELETE_BY_ID_SQL_STATEMENT = "DELETE FROM persons WHERE id = ?;";

    public PhoneNumberDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Long save(Person person) {
        Objects.requireNonNull(person, "Argument person must not be null");

        PreparedStatement preparedStatement = getPreparedStatementWithGeneratedKeys(INSERT_SQL_STATEMENT);
        setPreparedStatement(preparedStatement, person);

        if (executeUpdateAndHandleException(preparedStatement) != 1) {
            throw new DaoOperationException("Inserting into a table has failed because of some reasons");
        }

        try {
            ResultSet generatedKey = preparedStatement.getGeneratedKeys();
            if (generatedKey.next()) {
                long id = generatedKey.getLong("id");
                person.setId(id);
                return id;
            } else {
                throw new DaoOperationException("No Id returned after save book");
            }
        } catch (SQLException e) {
            throw new DaoOperationException("Error during getting id from the result set");
        }
    }

    @Override
    public List<Person> findAll() {

        PreparedStatement preparedStatement = getPreparedStatement(SELECT_ALL_SQL_STATEMENT);

        try {
            ResultSet resultSet = preparedStatement.executeQuery();

            return (List<Person>) getPersonCollection(resultSet);

        } catch (SQLException e) {
            throw new DaoOperationException("Error during executing query", e);
        }
    }

    @Override
    public Person findById(Long id) {
        Objects.requireNonNull(id, "Argument id must not be null");

        ResultSet resultSet = null;

        PreparedStatement preparedStatement = getPreparedStatement(SELECT_BY_ID_SQL_STATEMENT);

        try {
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new DaoOperationException("Error during finding entity by id", e);
        }

        Person person = new Person();
        setPersonFromResultSet(resultSet, person);

        return person;
    }

    @Override
    public void update(Person person) {
        Objects.requireNonNull(person, "Argument person must not be null");

        PreparedStatement preparedStatement = getPreparedStatement(UPDATE_SQL_STATEMENT);
        setPreparedStatement(preparedStatement, person);

        if (executeUpdateAndHandleException(preparedStatement) == 0) {
            throw new DaoOperationException("Updating table has failed because of some reasons");
        }
    }

    @Override
    public void remove(Long id) {
        Objects.requireNonNull(id, "Argument id must not be null");

        PreparedStatement preparedStatement = getPreparedStatement(DELETE_BY_ID_SQL_STATEMENT);

        try {
            preparedStatement.setLong(1, id);
        } catch (SQLException e) {
            throw new DaoOperationException("Error during setting prepared statement", e);
        }

        if (executeUpdateAndHandleException(preparedStatement) == 0) {
            throw new DaoOperationException("Deleting from table has failed because of some reasons");
        }
    }


    private PreparedStatement getPreparedStatement (String sqlStatement) {

        try (Connection connection = dataSource.getConnection()) {
            return connection.prepareStatement(sqlStatement);

        } catch (SQLException e) {
            throw  new DaoOperationException("Error during preparing sql statement", e);
        }
    }


    private PreparedStatement getPreparedStatementWithGeneratedKeys (String sqlStatement) {

        Objects.requireNonNull(sqlStatement, "Argument sqlStatement must not be null");

        try (Connection connection = dataSource.getConnection()) {
            return connection.prepareStatement(sqlStatement, PreparedStatement.RETURN_GENERATED_KEYS);

        } catch (SQLException e) {
            throw  new DaoOperationException("Error during preparing sql statement", e);
        }
    }


    private void setPreparedStatement(PreparedStatement preparedStatement, Person person) {
        Objects.requireNonNull(preparedStatement, "Argument preparedStatement must not be null");
        Objects.requireNonNull(person, "Argument person must not be null");

        try {
            int parameterIndex = 1;
            preparedStatement.setString(parameterIndex++, person.getFirstName());
            preparedStatement.setString(parameterIndex++, person.getLastName());
            preparedStatement.setString(parameterIndex++, person.getGender().getGenderAsString());
            preparedStatement.setDate(parameterIndex++, Date.valueOf(person.getBirthday()));
            preparedStatement.setString(parameterIndex++, person.getCity());
            preparedStatement.setString(parameterIndex, person.getEmail());
        } catch (SQLException e) {
            throw new DaoOperationException("Error during setting prepared statement", e);
        }
    }


    private int executeUpdateAndHandleException(PreparedStatement preparedStatement) {
        Objects.requireNonNull(preparedStatement, "Argument preparedStatement must not be null");

        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoOperationException("Error during executing update on prepared statement");
        }
    }


    private boolean setPersonFromResultSet(ResultSet resultSet, Person person) {
        Objects.requireNonNull(resultSet, "Argument resultSet must not be null");

        boolean hasNext = false;

        try {
            if (!(hasNext = resultSet.next())) {
                return hasNext;
            }

            person.setId(resultSet.getLong("id"));
            person.setFirstName(resultSet.getString("first_name"));
            person.setLastName(resultSet.getString("last_name"));
            person.setGender(Gender.valueOf(resultSet.getString("gender")));
            person.setBirthday(resultSet.getDate("birthday").toLocalDate());
            person.setCity(resultSet.getString("city"));
            person.setEmail(resultSet.getString("email"));

        } catch (SQLException e) {
            throw new DaoOperationException("Error during reading result set");
        }

        return hasNext;
    }


    private Collection<Person> getPersonCollection(ResultSet resultSet) {

        Collection<Person> collectionOfPersons = new ArrayList<>();

        while (true) {
            Person person = new Person();
            if (setPersonFromResultSet(resultSet, person)) {
                break;
            }
            collectionOfPersons.add(person);
        }

        return collectionOfPersons;
    }
}*/
