package com.shadowsdream.dao;

import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.model.PhoneNumber;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PhoneNumberDao {

    Map<Long, List<PhoneNumber>> getPhoneNumbersGroupedByPersonId() throws DaoOperationException;

    Set<Long> savePhoneNumbers(Long personId, List<PhoneNumber> phoneNumberList) throws DaoOperationException;

    void updatePhoneNumber(PhoneNumber phoneNumber) throws DaoOperationException;

    void removeAllPersonPhoneNumbers(Long personId) throws DaoOperationException;

    void removePhoneNumber(Long id) throws DaoOperationException;
}
