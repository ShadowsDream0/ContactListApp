package com.shadowsdream.service;

import com.shadowsdream.dao.PersonDao;
import com.shadowsdream.dao.PersonDaoImpl;
import com.shadowsdream.model.enums.Person;
import com.shadowsdream.model.PhoneNumber;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

public class PersonServiceImpl implements PersonService {

    private PersonDao personDao = null;

    public PersonServiceImpl(DataSource dataSource) {
        Objects.requireNonNull(dataSource, "Arguments dataSource must not be null");

        this.personDao = new PersonDaoImpl(dataSource);
    }

    public Long save(Person person) {
        return personDao.save(person);
    }

    public List<Person> findAll() {
        return personDao.findAll();
    }

    public Person findById(Long id) {
        return personDao.findById(id);
    }

    public void updatePerson(Person person) {
        personDao.updatePerson(person);
    }

    public void updatePhoneNumber(PhoneNumber phoneNumber) {
        personDao.updatePhoneNumber(phoneNumber);
    }

    public void removePerson(Long id) {
        personDao.removePerson(id);
    }

    public void removePhoneNumber(Long id) {
        personDao.removePhoneNumber(id);
    }
}
