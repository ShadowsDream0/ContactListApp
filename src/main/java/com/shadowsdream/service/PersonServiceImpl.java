package com.shadowsdream.service;


import com.shadowsdream.dao.*;
import com.shadowsdream.dto.*;
import com.shadowsdream.dto.mappers.*;
import com.shadowsdream.exception.*;
import com.shadowsdream.service.implementations.PersonService;
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
            ContactListLogger.getLog().error("Critical error occurred: " + daoEx.getMessage() + " " + daoEx.getCause());
            PrettyPrinter.printError("Server critical error. Exiting...");
            System.exit(1);
        }
        return id;
    }


    public List<PersonViewDto> findAll() {
        ContactListLogger.getLog().info("Invoked findAll() method...");

        PersonViewDtoMapper mapper = Mappers.getMapper(PersonViewDtoMapper.class);

        List<PersonViewDto> personViewDtoList = null;
        try {
            personViewDtoList = personDao.findAll().stream()
                        .map(mapper::toDto)
                    .collect(Collectors.toList());
        } catch (DaoOperationException e) {
            ContactListLogger.getLog().error("Critical error occurred: " + e.getMessage() + " " + e.getCause());
            PrettyPrinter.printError("Server critical error. Exiting...");
            System.exit(1);
        }

        return personViewDtoList;
    }


    public PersonDto findById(Long id) throws PersonServiceException {
        ContactListLogger.getLog().info("Invoked findById() method...");

        PersonDtoMapper mapper = Mappers.getMapper(PersonDtoMapper.class);
        PersonDto personDto = null;
        try {
            personDto =  mapper.toDto(personDao.findById(id));
        } catch (SelectOperationException selectEx) {
            throw new PersonServiceException("could not get details: " + selectEx.getMessage());
        } catch (DaoOperationException daoEx) {
            ContactListLogger.getLog().error("Critical error occurred: " + daoEx.getMessage() + " " + daoEx.getCause());
            PrettyPrinter.printError("Server critical error. Exiting...");
            System.exit(1);
        }
        return personDto;
    }


    public void updatePerson(PersonDto personDto) {
        ContactListLogger.getLog().info("Invoked updatePerson() method...");

        PersonDtoMapper mapper = Mappers.getMapper(PersonDtoMapper.class);

        try {
            personDao.updatePerson(mapper.fromDto(personDto));
        } catch (DaoOperationException e) {
            ContactListLogger.getLog().error("Critical error occurred: " + e.getMessage() + " " + e.getCause());
            PrettyPrinter.printError("Server critical error. Exiting...");
            System.exit(1);
        }
    }


    public void updatePhoneNumber(PhoneNumberDto phoneNumberDto) {
        ContactListLogger.getLog().info("Invoked updatePhoneNumber() method...");

        PhoneNumberDtoMapper mapper = Mappers.getMapper(PhoneNumberDtoMapper.class);

        try {
            personDao.updatePhoneNumber(mapper.fromDto(phoneNumberDto));
        } catch (DaoOperationException e) {
            ContactListLogger.getLog().error("Critical error occurred: " + e.getMessage() + " " + e.getCause());
            PrettyPrinter.printError("Server critical error. Exiting...");
            System.exit(1);
        }
    }


    public void removePerson(Long id) throws PersonServiceException {
        ContactListLogger.getLog().info("Invoked removePerson() method...");
        try {
            personDao.removePerson(id);
        } catch (DeleteOperationException deleteEx) {
            throw new PersonServiceException("could not remove contact: " + deleteEx.getMessage());
        } catch (DaoOperationException daoEx) {
            ContactListLogger.getLog().error("Critical error occurred: " + daoEx.getMessage() + " " + daoEx.getCause());
            PrettyPrinter.printError("Server critical error. Exiting...");
            System.exit(1);
        }
    }


    public void removePhoneNumber(Long id) throws PersonServiceException {
        ContactListLogger.getLog().info("Invoked removePhoneNumber() method...");
        try {
            personDao.removePhoneNumber(id);
        } catch (DeleteOperationException deleteEx) {
            throw new PersonServiceException("could not remove: " + deleteEx.getMessage());
        } catch (DaoOperationException daoEx) {
            ContactListLogger.getLog().error("Critical error occurred: " + daoEx.getMessage() + " " + daoEx.getCause());
            PrettyPrinter.printError("Server critical error. Exiting...");
            System.exit(1);
        }
    }
}
