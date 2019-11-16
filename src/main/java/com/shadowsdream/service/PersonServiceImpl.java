package com.shadowsdream.service;


import com.shadowsdream.dao.*;
import com.shadowsdream.dto.*;
import com.shadowsdream.dto.mappers.*;
import com.shadowsdream.exception.DaoOperationException;
import com.shadowsdream.exception.InsertOperationException;
import com.shadowsdream.exception.PersonServiceException;
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


    public Long save(PersonSaveDto personSaveDto) throws PersonServiceException{
        ContactListLogger.getLog().info("Invoked save() method...");

        PersonSaveDtoMapper mapper = Mappers.getMapper(PersonSaveDtoMapper.class);
        Long id = 0L;
        try {
            id = personDao.save(mapper.fromDto(personSaveDto));
        } catch (InsertOperationException insertEx) {
            throw new PersonServiceException("contact saving failed: " + insertEx.getMessage(), insertEx.getCause());
        } catch (DaoOperationException daoEx) {
            ContactListLogger.getLog().error("Critical error ocured: " + daoEx.getMessage() + " " + daoEx.getCause());
            PrettyPrinter.printError("Server critical error. Exiting...");
            System.exit(1);
        }
        return id;
    }


    public List<PersonViewDto> findAll() throws PersonServiceException {
        ContactListLogger.getLog().info("Invoked findAll() method...");

        PersonViewDtoMapper mapper = Mappers.getMapper(PersonViewDtoMapper.class);

        try {
            return personDao.findAll().stream()
                        .map(mapper::toDto)
                    .collect(Collectors.toList());
        } catch (DaoOperationException e) {
           throw new PersonServiceException("TODO"); //todo: inform about error properly
        }

    }


    public PersonDto findById(Long id) throws PersonServiceException {
        ContactListLogger.getLog().info("Invoked findById() method...");

        PersonDtoMapper mapper = Mappers.getMapper(PersonDtoMapper.class);

        try {
            return mapper.toDto(personDao.findById(id));
        } catch (DaoOperationException e) {
            throw new PersonServiceException("TODO"); // todo: inform about error properly
        }
    }


    public void updatePerson(PersonDto personDto) throws PersonServiceException {
        ContactListLogger.getLog().info("Invoked updatePerson() method...");

        PersonDtoMapper mapper = Mappers.getMapper(PersonDtoMapper.class);

        try {
            personDao.updatePerson(mapper.fromDto(personDto));
        } catch (DaoOperationException e) {
            throw new PersonServiceException("TODO"); // todo: inform about error properly
        }
    }


    public void updatePhoneNumber(PhoneNumberDto phoneNumberDto) throws PersonServiceException {
        ContactListLogger.getLog().info("Invoked updatePhoneNumber() method...");

        PhoneNumberDtoMapper mapper = Mappers.getMapper(PhoneNumberDtoMapper.class);

        try {
            personDao.updatePhoneNumber(mapper.fromDto(phoneNumberDto));
        } catch (DaoOperationException e) {
           throw new PersonServiceException("TODO"); // todo: inform about error properly
        }
    }


    public void removePerson(Long id) throws PersonServiceException {
        ContactListLogger.getLog().info("Invoked removePerson() method...");
        try {
            personDao.removePerson(id);
        } catch (DaoOperationException e) {
            throw new PersonServiceException("TODO"); // todo: inform about error properly
        }
    }


    public void removePhoneNumber(Long id) throws PersonServiceException {
        ContactListLogger.getLog().info("Invoked removePhoneNumber() method...");
        try {
            personDao.removePhoneNumber(id);
        } catch (DaoOperationException e) {
            throw new PersonServiceException("TODO"); // todo: inform about error properly
        }
    }
}
