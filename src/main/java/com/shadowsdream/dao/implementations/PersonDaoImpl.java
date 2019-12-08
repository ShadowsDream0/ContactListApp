package com.shadowsdream.dao.implementations;

import com.shadowsdream.dao.PersonDao;
import com.shadowsdream.dao.PhoneNumberDao;
import com.shadowsdream.exception.*;
import com.shadowsdream.model.enums.Gender;
import com.shadowsdream.model.Person;
import com.shadowsdream.model.PhoneNumber;
import com.shadowsdream.util.logging.ContactListLogger;

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

    private static final String SELECT_ALL_SQL_STATEMENT = "SELECT * FROM persons ORDER BY id;";

    private static final String SELECT_BY_ID_SQL_STATEMENT = "SELECT * FROM persons WHERE id = ?;";

    private static final String DELETE_BY_ID_SQL_STATEMENT = "DELETE FROM persons WHERE id = ?;";


    public PersonDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.phoneNumberDao = new PhoneNumberDaoImpl(dataSource);
    }


    @Override
    public Long save(Person person) throws DaoOperationException {
        ContactListLogger.getLog().debug("Invoked save() method in PersonDaoImpl...");

        Objects.requireNonNull(person, "Argument person must not be null");

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL_STATEMENT,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            setPreparedStatementExceptId(preparedStatement, person);

            try {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new InsertOperationException("such contact already exists");
            }

            ResultSet generatedKey = preparedStatement.getGeneratedKeys();
            long id = 0L;
            if (generatedKey.next()) {
                id = generatedKey.getLong("id");
                person.setId(id);

                ContactListLogger.getLog().debug("Person state before setting phone numbers " + person);

                phoneNumberDao.savePhoneNumbers(person.getId(), person.getPhoneNumbers());

                ContactListLogger.getLog().debug("Person state after setting phone numbers " + person);
                return id;

            } else {
                throw new DaoOperationException("no such id " + id + " in database");
            }

        } catch (SQLException e) {
            throw new DaoOperationException("Error during inserting a record( " + person +" ) into table", e);
        }
    }


    @Override
    public List<Person> findAll() throws DaoOperationException{
        ContactListLogger.getLog().debug("Invoked findAll() method in PersonDaoImpl...");

        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL_STATEMENT);
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            throw new DaoOperationException("error occurred during executing statement", e);
        }

        return (List<Person>) getPersonCollection(resultSet);
    }


    @Override
    public Person findById(Long id) throws DaoOperationException {
        ContactListLogger.getLog().debug("Invoked findById() method in PersonDaoImpl...");

        Objects.requireNonNull(id, "Argument id must not be null");

        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL_STATEMENT);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();

        } catch (SQLException e) {
            throw new DaoOperationException("error occurred while selecting from table", e);
        }


        Person person = new Person();
        if (!setPersonFromResultSet(resultSet, person) ) {
            throw new SelectOperationException("no such contact");
        }

        ContactListLogger.getLog().debug("Returned person " + person);
        return person;
    }


    @Override
    public void updatePerson(Person person) throws DaoOperationException {
        ContactListLogger.getLog().debug("Invoked updatePerson() method in PersonDaoImpl...");

        Objects.requireNonNull(person, "Argument person must not be null");

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL_STATEMENT);
            setPreparedStatementFull(preparedStatement, person);

            ContactListLogger.getLog().debug("Person before executing statement " + person);

            try {
                preparedStatement.executeUpdate();
            } catch (SQLException e){
                throw new DaoOperationException("error during updating contact", e);
            }

        } catch (SQLException e) {
            throw new DaoOperationException("error during preparing update statement", e);
        }

        ContactListLogger.getLog().debug("Returned from updatePerson() method in PersonDaoImpl...");
    }


    public void updatePhoneNumber(PhoneNumber phoneNumber) throws DaoOperationException {
        ContactListLogger.getLog().debug("Invoked updatePhoneNumber() method in PhoneNumberDaoImpl...");
        try {
            phoneNumberDao.updatePhoneNumber(phoneNumber);
        } catch (DaoOperationException e) {
            throw new DaoOperationException(e.getMessage(), e);
        }
        ContactListLogger.getLog().debug("Returned from updatePhoneNumber() method in PhoneNumberDaoImpl...");
    }


    @Override
    public void removePerson(Long id) throws DaoOperationException {
        ContactListLogger.getLog().debug("Invoked removePerson() method in PersonDaoImpl...");

        Objects.requireNonNull(id, "Argument id must not be null");

        phoneNumberDao.removeAllPersonPhoneNumbers(id);

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_SQL_STATEMENT);
            preparedStatement.setLong(1, id);

            try {
                if(preparedStatement.executeUpdate() != 1) {
                    throw new DeleteOperationException("no such person with id " + id);
                }
            } catch (SQLException e){
                throw new DaoOperationException("error during deleting record (person id: " + id + ") from table", e);
            }

        } catch (SQLException e) {
            throw new DaoOperationException("Error during deleting record from table", e);
        }

        ContactListLogger.getLog().debug("Returned from removePerson() method in PersonDaoImpl...");
    }


    @Override
    public void removePhoneNumber(Long phoneNumberId) throws DaoOperationException {
        ContactListLogger.getLog().debug("Invoked removePhoneNumber() method in PersonDaoImpl...");

        try {
            phoneNumberDao.removePhoneNumber(phoneNumberId);
        } catch (DeleteOperationException deleteEx) {
            throw new DeleteOperationException(deleteEx.getMessage());
        } catch ( DaoOperationException daoEx) {
            throw new DaoOperationException(daoEx.getMessage());
        }

        ContactListLogger.getLog().debug("Returned from removePhoneNumber() method in PersonDaoImpl...");
    }


    private void setPreparedStatementFull(PreparedStatement preparedStatement, Person person) throws DaoOperationException {
        Objects.requireNonNull(preparedStatement, "Argument preparedStatement must not be null");
        Objects.requireNonNull(person, "Argument person must not be null");

        int parameterIndex = setPreparedStatementExceptId(preparedStatement, person);
        try {
            preparedStatement.setLong(++parameterIndex, person.getId());
        } catch (SQLException e) {
            throw new DaoOperationException("Error during setting id on prepared statement", e);
        }
    }


    private int setPreparedStatementExceptId (PreparedStatement preparedStatement, Person person)
            throws DaoOperationException {

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


    private boolean setPersonFromResultSet(ResultSet resultSet, Person person) throws DaoOperationException {
        ContactListLogger.getLog().debug("Invoked setPersonFromResultSet() method in PersonDaoImpl...");

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
            throw new DaoOperationException("error during reading result set", e);
        }

        ContactListLogger.getLog().debug("Person has been set with values:" + person);
        return hasNext;
    }


    private void setPhoneNumbersForPerson(ResultSet resultSet, Person person) throws DaoOperationException {
        Objects.requireNonNull(resultSet, "Argument resultSet must not be null");
        Objects.requireNonNull(person, "Argument person must not be null");

        Map<Long, List<PhoneNumber>> mapOfPhonesNumbers = phoneNumberDao.getPhoneNumbersGroupedByPersonId();

        //ignore person with no phone numbers
        Long person_id = person.getId();
        if (mapOfPhonesNumbers.get(person_id) == null) {
            return;
        }

        List<PhoneNumber> buffer = mapOfPhonesNumbers.get(person_id);
        List<PhoneNumber> resultList = new ArrayList<>(buffer.size());
        for (PhoneNumber pn : buffer) {
            resultList.add(new PhoneNumber(pn.getId(), pn.getPhone(), pn.getType()));
        }

        person.setPhoneNumbers(resultList);
    }


    private Collection<Person> getPersonCollection(ResultSet resultSet) throws DaoOperationException {
        ContactListLogger.getLog().debug("Invoked getPersonCollection() method in PersonDaoImpl...");
        Collection<Person> collectionOfPersons = new ArrayList<>();

        while (true) {
            Person person = new Person();
            if (!setPersonFromResultSet(resultSet, person)) {
                break;
            }
            collectionOfPersons.add(person);
        }

        ContactListLogger.getLog().debug("Collection of persons returned " + collectionOfPersons);
        return collectionOfPersons;
    }
}
