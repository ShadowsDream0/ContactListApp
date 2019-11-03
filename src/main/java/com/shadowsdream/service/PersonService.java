package com.shadowsdream.service;

import com.shadowsdream.model.Person;

import java.util.List;

public interface PersonService {

    Long save(Person person);

    List<Person> findAll();

    Person findById(Long id);

    void update(Person person);

    void remove(Long id);
}
