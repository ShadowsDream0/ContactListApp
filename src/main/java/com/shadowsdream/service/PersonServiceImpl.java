package com.shadowsdream.service;

import com.shadowsdream.dao.PersonDao;
import com.shadowsdream.dao.PersonDaoImpl;
import com.shadowsdream.model.Person;

import javax.sql.DataSource;
import java.util.List;

public class PersonServiceImpl implements PersonService {

    private PersonDao personDao = null;

    public PersonServiceImpl(DataSource dataSource) {
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

    public void update(Person person) {
        personDao.update(person);
    }

    public void remove(Long id) {
        personDao.remove(id);
    }
}
