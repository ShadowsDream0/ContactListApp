package com.shadowsdream.service;


import com.shadowsdream.dao.*;
import com.shadowsdream.dto.*;
import com.shadowsdream.dto.mappers.*;
import com.shadowsdream.util.logging.ContactListLogger;

import org.mapstruct.factory.Mappers;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class PersonServiceImpl implements PersonService {

    private PersonDao personDao = null;


    public PersonServiceImpl(DataSource dataSource) {
        Objects.requireNonNull(dataSource, "Arguments dataSource must not be null");

        this.personDao = new PersonDaoImpl(dataSource);
    }


    public Long save(PersonSaveDto personSaveDto) {
        ContactListLogger.getLog().info("Started save() method...");

        PersonSaveDtoMapper mapper = Mappers.getMapper(PersonSaveDtoMapper.class);

        return personDao.save(mapper.fromDto(personSaveDto));
    }


    public List<PersonViewDto> findAll() {
        ContactListLogger.getLog().info("Started findAll() method...");

        PersonViewDtoMapper mapper = Mappers.getMapper(PersonViewDtoMapper.class);

        return personDao.findAll().stream()
                    .map(mapper::toDto)
                .collect(Collectors.toList());

    }


    public PersonDto findById(Long id) {
        ContactListLogger.getLog().info("Started findById() method...");

        PersonDtoMapper mapper = Mappers.getMapper(PersonDtoMapper.class);

        return mapper.toDto(personDao.findById(id));
    }


    public void updatePerson(PersonDto personDto) {
        ContactListLogger.getLog().info("Started updatePerson() method...");

        PersonDtoMapper mapper = Mappers.getMapper(PersonDtoMapper.class);

        personDao.updatePerson(mapper.fromDto(personDto));
    }


    public void updatePhoneNumber(PhoneNumberDto phoneNumberDto) {
        ContactListLogger.getLog().info("Started updatePhoneNumber() method...");

        PhoneNumberDtoMapper mapper = Mappers.getMapper(PhoneNumberDtoMapper.class);

        personDao.updatePhoneNumber(mapper.fromDto(phoneNumberDto));
    }


    public void removePerson(Long id) {
        ContactListLogger.getLog().info("Started removePerson() method...");
        personDao.removePerson(id);
    }


    public void removePhoneNumber(Long id) {
        ContactListLogger.getLog().info("Started removePhoneNumber() method...");
        personDao.removePhoneNumber(id);
    }
}
