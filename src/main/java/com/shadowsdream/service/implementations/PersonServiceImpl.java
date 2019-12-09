package com.shadowsdream.service.implementations;


import com.shadowsdream.dao.PersonDao;
import com.shadowsdream.dto.PersonDto;
import com.shadowsdream.dto.PersonSaveDto;
import com.shadowsdream.dto.PersonViewDto;
import com.shadowsdream.dto.PhoneNumberDto;
import com.shadowsdream.dto.mappers.PersonDtoMapper;
import com.shadowsdream.dto.mappers.PersonSaveDtoMapper;
import com.shadowsdream.dto.mappers.PersonViewDtoMapper;
import com.shadowsdream.dto.mappers.PhoneNumberDtoMapper;
import com.shadowsdream.exception.*;
import com.shadowsdream.service.PersonService;
import com.shadowsdream.service.PrettyPrinter;
import com.shadowsdream.util.logging.ContactListLogger;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class PersonServiceImpl implements PersonService {

    private PersonDao personDao = null;


    public PersonServiceImpl(PersonDao personDao) {
        Objects.requireNonNull(personDao, "Argument personDao must not be null");

        this.personDao = personDao;
    }


    public Long save(PersonSaveDto personSaveDto) throws PersonServiceException{
        Objects.requireNonNull(personSaveDto, "Argument personSaveDto must not be null");

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
            personViewDtoList = personDao.findAll()
                                            .stream()
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
        Objects.requireNonNull(id, "Argument id must not be null");


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


    public void updatePerson(PersonDto personDto) throws PersonServiceException {
        ContactListLogger.getLog().info("Invoked updatePerson() method...");
        Objects.requireNonNull(personDto, "Argument personDto must not be null");


        PersonDtoMapper mapper = Mappers.getMapper(PersonDtoMapper.class);

        try {
            personDao.updatePerson(mapper.fromDto(personDto));
        } catch (DaoOperationException e) {
            ContactListLogger.getLog().error("SQLException caught: " + e.getMessage() + " " + e.getCause());
            throw new PersonServiceException("Could not update contact. Try another email. If it doesn't work, please, inform developers");
        }
    }


    public void updatePhoneNumber(PhoneNumberDto phoneNumberDto) {
        ContactListLogger.getLog().info("Invoked updatePhoneNumber() method...");
        Objects.requireNonNull(phoneNumberDto, "Argument phoneNumberDto must not be null");


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
        Objects.requireNonNull(id, "Argument id must not be null");

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
        Objects.requireNonNull(id, "Argument id must not be null");

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
