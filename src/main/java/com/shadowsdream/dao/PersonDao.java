package com.shadowsdream.dao;

import com.shadowsdream.model.enums.Person;
import com.shadowsdream.model.PhoneNumber;

import java.util.List;

public interface PersonDao {
    Long save(Person person);

    List<Person> findAll();

    Person findById(Long id);

    void updatePerson(Person person);

    void updatePhoneNumber(PhoneNumber phoneNumber);

    void removePerson(Long id);

    void removePhoneNumber(Long phoneNumberId);
}
