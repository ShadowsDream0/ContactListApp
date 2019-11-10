package com.shadowsdream.service;

import com.shadowsdream.dao.*;

import com.shadowsdream.dto.*;

import com.shadowsdream.dto.mappers.*;

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

        PersonSaveDtoMapper mapper = Mappers.getMapper(PersonSaveDtoMapper.class);

        return personDao.save(mapper.fromDto(personSaveDto));
    }


    public List<PersonViewDto> findAll() {
        PersonViewDtoMapper mapper = Mappers.getMapper(PersonViewDtoMapper.class);

        return personDao.findAll().stream()
                    .map(mapper::toDto)
                .collect(Collectors.toList());

    }


    public PersonDto findById(Long id) {

        PersonDtoMapper mapper = Mappers.getMapper(PersonDtoMapper.class);

        return mapper.toDto(personDao.findById(id));
    }


    public void updatePerson(PersonDto personDto) {
        //todo: logging

        PersonDtoMapper mapper = Mappers.getMapper(PersonDtoMapper.class);

        personDao.updatePerson(mapper.fromDto(personDto));
    }


    public void updatePhoneNumber(PhoneNumberDto phoneNumberDto) {

        PhoneNumberDtoMapper mapper = Mappers.getMapper(PhoneNumberDtoMapper.class);

        personDao.updatePhoneNumber(mapper.fromDto(phoneNumberDto));
    }


    public void removePerson(Long id) {
        personDao.removePerson(id);
    }


    public void removePhoneNumber(Long id) {
        personDao.removePhoneNumber(id);
    }
}
