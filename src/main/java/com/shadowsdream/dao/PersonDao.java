package com.shadowsdream.dao;

import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.model.Person;
import com.shadowsdream.model.PhoneNumber;

import java.util.List;

public interface PersonDao {

    Long save(Person person) throws DaoOperationException;

    List<Person> findAll() throws DaoOperationException;

    Person findById(Long id) throws DaoOperationException;

    void updatePerson(Person person) throws DaoOperationException;

    void updatePhoneNumber(PhoneNumber phoneNumber) throws DaoOperationException;

    void removePerson(Long id) throws DaoOperationException;

    void removePhoneNumber(Long phoneNumberId) throws DaoOperationException;
}
