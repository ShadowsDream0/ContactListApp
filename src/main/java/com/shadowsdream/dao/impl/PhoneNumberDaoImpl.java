package com.shadowsdream.dao.impl;

import com.shadowsdream.dao.PhoneNumberDao;
import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.exception.InsertOperationException;
import com.shadowsdream.model.PhoneNumber;
import com.shadowsdream.model.enums.PhoneType;
import com.shadowsdream.util.logging.ContactListLogger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PhoneNumberDaoImpl implements PhoneNumberDao {

    private DataSource dataSource;

    private final String SELECT_ALL_SQL_STATEMENT = "SELECT * FROM phones ORDER BY person_id;";

    private final String INSERT_SQL_STATEMENT = "INSERT INTO phones (phone_number, phone_type, person_id)" +
                                                " VALUES (?, CAST(? AS TYPE_OF_PHONE), ?);";

    private final String UPDATE_SQL_STATEMENT = "UPDATE phones " +
                                                    "SET " +
                                                        "phone_number = ?, " +
                                                        "phone_type = CAST(? AS TYPE_OF_PHONE) " +
                                                        "WHERE id = ?;";

    private final String DELETE_ALL_SQL_STATEMENT = "DELETE FROM phones WHERE person_id = ?;";
    private final String DELETE_ONE_SQL_STATEMENT = "DELETE FROM phones WHERE id = ?;";

    public PhoneNumberDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Map<Long, List<PhoneNumber>> getPhoneNumbersGroupedByPersonId() throws DaoOperationException{

        ResultSet resultSet = null;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL_STATEMENT);
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            throw new DaoOperationException("error during selecting phone numbers", e);
        }

        return getMapOfPhoneNumbers(resultSet);
    }


    @Override
    public Set<Long> savePhoneNumbers(Long personId, List<PhoneNumber> phoneNumberList) throws DaoOperationException {

        Objects.requireNonNull(personId, "Argument personId must not be null");
        Objects.requireNonNull(phoneNumberList, "Argument phoneNumberList must not be null");

        Set<Long> idSet = new HashSet<>();
        for (PhoneNumber pn : phoneNumberList) {
            idSet.add(savePhoneNumber(personId, pn));
        }

        return idSet;
    }


    @Override
    public void removeAllPersonPhoneNumbers(Long personId) throws DaoOperationException{
        ContactListLogger.getLog().debug("Invoked removeAllPersonPhoneNumbers() method in PhoneNumberDaoImpl...");
        Objects.requireNonNull(personId, "Argumernt personId must not be null");

        remove(DELETE_ALL_SQL_STATEMENT, personId);
        ContactListLogger.getLog().debug("Returned from removeAllPersonPhoneNumbers() method in PhoneNumberDaoImpl...");
    }


    @Override
    public void removePhoneNumber(Long id) throws DaoOperationException {
        ContactListLogger.getLog().debug("Invoked removePhoneNumber() method in PhoneNumberDaoImpl...");

        Objects.requireNonNull(id, "Argument id must not be null");

        remove(DELETE_ONE_SQL_STATEMENT, id);

        ContactListLogger.getLog().debug("Returned removePhoneNumber() method in PhoneNumberDaoImpl...");
    }



    @Override
    public void updatePhoneNumber(PhoneNumber phoneNumber) throws DaoOperationException{
        ContactListLogger.getLog().debug("Invoked updatePhoneNumber() method in PhoneNumberDaoImpl...");

        Objects.requireNonNull(phoneNumber, "Argument phoneNumber must not be null");

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL_STATEMENT);
            setPreparedStatement(preparedStatement, phoneNumber, phoneNumber.getId());

            ContactListLogger.getLog().debug("Phone number before executing statement " + phoneNumber);

            if (executeUpdateAndHandleException(preparedStatement) != 1) {
                throw new DaoOperationException("error during updating phone number in the table");
            }

        } catch (SQLException e) {
            throw new DaoOperationException("error during updating phone number in the table", e);
        }

        ContactListLogger.getLog().debug("Returned from updatePhoneNumber() method in PhoneNumberDaoImpl...");
    }


    private boolean setPhoneNumberFromResultSet(ResultSet resultSet, PhoneNumber phoneNumber) 
            throws DaoOperationException{
        
        Objects.requireNonNull(resultSet, "Argument resultSet must not be null");
        Objects.requireNonNull(phoneNumber, "Argument phoneNumber must not be null");

        boolean hasNext = false;

        try {
            if (!(hasNext = resultSet.next())) {
                return hasNext;
            }
            phoneNumber.setId(resultSet.getLong("id"));
            phoneNumber.setPhone(resultSet.getString("phone_number"));
            phoneNumber.setType(PhoneType.valueOf(resultSet.getString("phone_type").toUpperCase()));
        } catch (SQLException e) {
            throw new DaoOperationException("error during reading result set", e);
        }

        return hasNext;
    }


    private Map<Long, List<PhoneNumber>> getMapOfPhoneNumbers(ResultSet resultSet) throws DaoOperationException {
        Objects.requireNonNull(resultSet, "Argument resultSet must not null");

        Map<Long, List<PhoneNumber>> mapOfPhoneNumbers = new HashMap<>();
        Long id = null;
        List<PhoneNumber> phoneNumberList = null;
        PhoneNumber phoneNumber = null;
        try {
            while (true) {
                phoneNumber = new PhoneNumber();
                if (!setPhoneNumberFromResultSet(resultSet, phoneNumber)) {
                    break;
                }

                id = resultSet.getLong("person_id");
                if (!mapOfPhoneNumbers.containsKey(id)) {
                    phoneNumberList = new ArrayList<>();
                    mapOfPhoneNumbers.put(id, phoneNumberList);
                    phoneNumberList.add(phoneNumber);
                } else {
                    phoneNumberList.add(phoneNumber);
                }
            }

        } catch (SQLException e) {
            throw new DaoOperationException("Error during reading result set", e);
        }
        return mapOfPhoneNumbers;
    }


    private int executeUpdateAndHandleException(PreparedStatement preparedStatement) throws DaoOperationException {
        Objects.requireNonNull(preparedStatement, "Argument preparedStatement must not be null");

        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoOperationException("Error during executing update", e);
        }
    }


    private Long savePhoneNumber(Long personId, PhoneNumber phoneNumber) throws DaoOperationException {
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL_STATEMENT,
                                                    PreparedStatement.RETURN_GENERATED_KEYS);
            setPreparedStatement(preparedStatement, phoneNumber, personId);

            if (executeUpdateAndHandleException(preparedStatement) != 1) {
                throw new InsertOperationException("one of the contacts already has such phone number");
            }


            ResultSet generatedKey = preparedStatement.getGeneratedKeys();
            long id = 0L;
            if (generatedKey.next()) {
                 id = generatedKey.getLong("id");
                return id;
            } else {
                throw new DaoOperationException("no such id " + id + " in database");
            }

        } catch (SQLException e) {
            throw new DaoOperationException("Error during saving phone number", e);
        }
    }


    private int setPreparedStatement(PreparedStatement preparedStatement, PhoneNumber phoneNumber, Long id)
            throws DaoOperationException {

        Objects.requireNonNull(preparedStatement, "Argument preparedStatement must not be null");
        Objects.requireNonNull(phoneNumber, "Argument phoneNumber must not be null");
        Objects.requireNonNull(id, "Argument id must not be null");

        int parameterIndex = 1;
        try {
            preparedStatement.setString(parameterIndex++, phoneNumber.getPhone());
            preparedStatement.setString(parameterIndex++, phoneNumber.getType().toString());
            preparedStatement.setLong(parameterIndex, id);
        } catch (SQLException e) {
            throw new DaoOperationException("Error during setting prepared statement", e);
        }

        return parameterIndex;
    }


    private void remove(String sqlStatement, Long id) throws DaoOperationException{
        ContactListLogger.getLog().debug("Invoked remove() method in PhoneNumberDaoImpl...");

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setLong(1, id);

        } catch (SQLException e) {
            throw new DaoOperationException("Error during removing phone numbers from table");
        }

        ContactListLogger.getLog().debug("Returned from remove() method in PhoneNumberDaoImpl...");
    }
}
