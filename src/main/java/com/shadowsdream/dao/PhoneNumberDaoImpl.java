package com.shadowsdream.dao;

import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.model.Person;
import com.shadowsdream.model.PhoneNumber;
import com.shadowsdream.model.PhoneType;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class PhoneNumberDaoImpl implements PhoneNumberDao{
    private DataSource dataSource;

    private final String SELECT_ALL_SQL_STATEMENT = "SELECT * FROM phones;";

    private final String INSERT_SQL_STATEMENT = "INSERT INTO phones (phone_number, phone_type, person_id)" +
                                                " VALUES (?, CAST(? AS TYPE_OF_PHONE), ?);";

    private final String UPDATE_SQL_STATEMENT = "UPDATE phones " +
                                                    "SET " +
                                                        "phone_number = ?, " +
                                                        "phone_type = CAST(? AS TYPE_OF_PHONE) " +
                                                        "WHERE id = ?;";


    public PhoneNumberDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Map<Long, List<PhoneNumber>> getPhoneNumbersGroupedByPersonId() {

        ResultSet resultSet = null;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL_STATEMENT);
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            throw new DaoOperationException("Error during selecting phone numbers by id");
        }

        return getMapOfPhoneNumbers(resultSet);
    }


    @Override
    public Set<Long> savePhoneNumbers(Long personId, List<PhoneNumber> phoneNumberList) {
        Objects.requireNonNull(personId, "Argument personId must not be null");
        Objects.requireNonNull(phoneNumberList, "Argument phoneNumberList must not be null");

        Set<Long> idSet = new HashSet<>();
        for (PhoneNumber pn : phoneNumberList) {
            idSet.add(savePhoneNumber(personId, pn));
        }

        return idSet;
    }


    @Override
    public void remove(PhoneNumber phoneNumber) {
        return;
    }



    @Override
    public void updatePhoneNumber(Long phoneId, PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Argument phoneNumber must not be null");

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL_STATEMENT);
            setPreparedStatement(preparedStatement, phoneNumber, phoneId);

            if (executeUpdateAndHandleException(preparedStatement) != 1) {
                throw new DaoOperationException("Failed to update table");
            }

        } catch (SQLException e) {
            throw new DaoOperationException("Error during updating table", e);
        }
    }


    private boolean setPhoneNumberFromResultSet(ResultSet resultSet, PhoneNumber phoneNumber) {
        Objects.requireNonNull(resultSet, "Argument resultSet must not be null");
        Objects.requireNonNull(phoneNumber, "Argument phoneNumber must not be null");

        boolean hasNext = false;

        try {
            if (!(hasNext = resultSet.next())) {
                return hasNext;
            }
            phoneNumber.setPhone(resultSet.getString("phone_number"));
            phoneNumber.setType(PhoneType.valueOf(resultSet.getString("phone_type").toUpperCase()));
        } catch (SQLException e) {
            throw new DaoOperationException("Error during reading result set", e);
        }

        return hasNext;
    }


    private Map<Long, List<PhoneNumber>> getMapOfPhoneNumbers(ResultSet resultSet) {
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


    private int executeUpdateAndHandleException(PreparedStatement preparedStatement) {
        Objects.requireNonNull(preparedStatement, "Argument preparedStatement must not be null");

        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoOperationException("Error during executing update on prepared statement", e);
        }
    }


    private Long savePhoneNumber(Long personId, PhoneNumber phoneNumber) {
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL_STATEMENT,
                                                    PreparedStatement.RETURN_GENERATED_KEYS);
            setPreparedStatement(preparedStatement, phoneNumber, personId);

            if (executeUpdateAndHandleException(preparedStatement) != 1) {
                throw new DaoOperationException("Failed to insert into table");
            }


            ResultSet generatedKey = preparedStatement.getGeneratedKeys();
            if (generatedKey.next()) {
                long id = generatedKey.getLong("id");
                return id;
            } else {
                throw new DaoOperationException("No Id returned after save book");
            }

        } catch (SQLException e) {
            throw new DaoOperationException("Error during saving phone number");
        }
    }


    private int setPreparedStatement(PreparedStatement preparedStatement, PhoneNumber phoneNumber, Long id) {
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
}
