package com.shadowsdream.dao;

import com.shadowsdream.model.PhoneNumber;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PhoneNumberDao {
    Map<Long, List<PhoneNumber>> getPhoneNumbersGroupedByPersonId();
    Set<Long> savePhoneNumbers(Long personId, List<PhoneNumber> phoneNumberList);
    void remove(PhoneNumber phoneNumber);
    void updatePhoneNumber(Long Id, PhoneNumber phoneNumber);

}
