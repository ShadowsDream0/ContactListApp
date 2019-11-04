package com.shadowsdream.dao;

import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.model.Gender;
import com.shadowsdream.model.Person;
import com.shadowsdream.model.PhoneNumber;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class PersonDaoImpl implements PersonDao {
    private DataSource dataSource;
    private PhoneNumberDao phoneNumberDao;

    private static final String INSERT_SQL_STATEMENT = "INSERT INTO persons " +
                                                "(first_name, last_name, gender, birthday, city, email) " +
                                                "VALUES (?, ?, CAST(? AS GENDER), ?, ?, ?);";

    private static final String UPDATE_SQL_STATEMENT = "UPDATE persons " +
                                                            "SET first_name = ?, " +
                                                                "last_name = ?, " +
                                                                "gender = CAST(? AS GENDER), " +
                                                                "birthday = ?, " +
                                                                "city = ?, " +
                                                                "email = ? " +
                                                            "WHERE id = ?;";

    private static final String SELECT_ALL_SQL_STATEMENT = "SELECT * FROM persons;";

    private static final String SELECT_BY_ID_SQL_STATEMENT = "SELECT * FROM persons WHERE id = ?;";

    private static final String DELETE_BY_ID_SQL_STATEMENT = "DELETE FROM persons WHERE id = ?;";


    public PersonDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.phoneNumberDao = new PhoneNumberDaoImpl(dataSource);
    }


    @Override
    public Long save(Person person) {
        Objects.requireNonNull(person, "Argument person must not be null");

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL_STATEMENT,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            setPreparedStatement(preparedStatement, person);

            if (executeUpdateAndHandleException(preparedStatement) != 1) {
                throw new DaoOperationException("Inserting into a table has failed");
            }

            phoneNumberDao.savePhoneNumbers(person.getId(), person.getPhoneNumbers());

            ResultSet generatedKey = preparedStatement.getGeneratedKeys();
            if (generatedKey.next()) {
                long id = generatedKey.getLong("id");
                person.setId(id);
                return id;
            } else {
                throw new DaoOperationException("No Id returned after save book");
            }

        } catch (SQLException e) {
            throw new DaoOperationException("Error during inserting record into a table", e);
        }
    }


    @Override
    public List<Person> findAll() {
        ResultSet resultSet = null;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL_STATEMENT);
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            throw new DaoOperationException("Query error occurred while executing statement", e);
        }

        return (List<Person>) getPersonCollection(resultSet);
    }


    @Override
    public Person findById(Long id) {
        Objects.requireNonNull(id, "Argument id must not be null");

        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL_STATEMENT);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();

        } catch (SQLException e) {
            throw new DaoOperationException("Query error occurred while selecting from table", e);
        }

        Person person = new Person();
        setPersonFromResultSet(resultSet, person);

        return person;
    }


    @Override
    public void update(Person person) {
        Objects.requireNonNull(person, "Argument person must not be null");

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL_STATEMENT);
            setPreparedStatementWithId(preparedStatement, person);

            phoneNumberDao.updatePhoneNumber();

            if (executeUpdateAndHandleException(preparedStatement) == 0) {
                throw new DaoOperationException("Updating table has failed");
            }

        } catch (SQLException e) {
            throw new DaoOperationException("Error during preparing update statement", e);
        }
    }


    @Override
    public void remove(Long id) {
        Objects.requireNonNull(id, "Argument id must not be null");

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_SQL_STATEMENT);
            preparedStatement.setLong(1, id);

            if (executeUpdateAndHandleException(preparedStatement) == 0) {
                throw new DaoOperationException("Deleting from table has failed");
            }

        } catch (SQLException e) {
            throw new DaoOperationException("Error during deleting record from table", e);
        }
    }


    private void setPreparedStatement(PreparedStatement preparedStatement, Person person) {
        Objects.requireNonNull(preparedStatement, "Argument preparedStatement must not be null");
        Objects.requireNonNull(person, "Argument person must not be null");

        setPreparedStatementExceptId(preparedStatement, person);
    }


    private void setPreparedStatementWithId(PreparedStatement preparedStatement, Person person) {
        Objects.requireNonNull(preparedStatement, "Argument preparedStatement must not be null");
        Objects.requireNonNull(person, "Argument person must not be null");

        int parameterIndex = setPreparedStatementExceptId(preparedStatement, person);
        try {
            preparedStatement.setLong(++parameterIndex, person.getId());
        } catch (SQLException e) {
            throw new DaoOperationException("Error during setting id on prepared statement");
        }
    }


    private int setPreparedStatementExceptId (PreparedStatement preparedStatement, Person person) {
        Objects.requireNonNull(preparedStatement, "Argument preparedStatement must not be null");
        Objects.requireNonNull(person, "Argument person must not be null");

        int parameterIndex = 1;
        try {
            preparedStatement.setString(parameterIndex++, person.getFirstName());
            preparedStatement.setString(parameterIndex++, person.getLastName());
            preparedStatement.setString(parameterIndex++, person.getGender().toString());
            preparedStatement.setDate(parameterIndex++, Date.valueOf(person.getBirthday()));
            preparedStatement.setString(parameterIndex++, person.getCity());
            preparedStatement.setString(parameterIndex, person.getEmail());
        } catch (SQLException e) {
            throw new DaoOperationException("Error during setting prepared statement", e);
        }

        return parameterIndex;
    }


    private int executeUpdateAndHandleException(PreparedStatement preparedStatement) {
        Objects.requireNonNull(preparedStatement, "Argument preparedStatement must not be null");

        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoOperationException("Error during executing update on prepared statement", e);
        }
    }


    private boolean setPersonFromResultSet(ResultSet resultSet, Person person) {
        Objects.requireNonNull(resultSet, "Argument resultSet must not be null");
        Objects.requireNonNull(person, "Argument person must not be null");

        boolean hasNext = false;

        try {
            if (!(hasNext = resultSet.next())) {
                return hasNext;
            }

            person.setId(resultSet.getLong("id"));
            person.setFirstName(resultSet.getString("first_name"));
            person.setLastName(resultSet.getString("last_name"));
            person.setGender(Gender.valueOf(resultSet.getString("gender").toUpperCase()));
            person.setBirthday(resultSet.getDate("birthday").toLocalDate());
            person.setCity(resultSet.getString("city"));
            person.setEmail(resultSet.getString("email"));

            setPhoneNumbersForPerson(resultSet, person);

        } catch (SQLException e) {
            throw new DaoOperationException("Error during reading result set");
        }

        return hasNext;
    }


    private void setPhoneNumbersForPerson(ResultSet resultSet, Person person) {
        Objects.requireNonNull(resultSet, "Argument resultSet must not be null");
        Objects.requireNonNull(person, "Argument person must not be null");

        Map<Long, List<PhoneNumber>> mapOfPhonesNumbers = phoneNumberDao.getPhoneNumbersGroupedByPersonId();

        //ignore person with no phone numbers
        Long person_id = person.getId();
        if (!mapOfPhonesNumbers.containsKey(person.getId())) {
            return;
        }

        List<PhoneNumber> buffer = mapOfPhonesNumbers.get(person_id);
        List<PhoneNumber> resultList = new ArrayList<>(buffer.size());
        for (PhoneNumber pn : buffer) {
            resultList.add(new PhoneNumber(pn.getPhone(), pn.getType()));
        }

        person.setPhoneNumbers(resultList);
    }


    private Collection<Person> getPersonCollection(ResultSet resultSet) {

        Collection<Person> collectionOfPersons = new ArrayList<>();

        while (true) {
            Person person = new Person();
            if (!setPersonFromResultSet(resultSet, person)) {
                break;
            }
            collectionOfPersons.add(person);
        }

        return collectionOfPersons;
    }
}
