package com.shadowsdream.facade.impl;

import com.shadowsdream.dto.PersonDto;
import com.shadowsdream.dto.PersonSaveDto;
import com.shadowsdream.dto.PersonViewDto;
import com.shadowsdream.facade.PersonFacade;
import com.shadowsdream.mapper.PersonMapper;
import com.shadowsdream.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PersonFacadeImpl implements PersonFacade {

    private final PersonService personService;
    private final PersonMapper personMapper;

    @Autowired
    public PersonFacadeImpl(PersonService personService, PersonMapper personMapper){
        this.personService = personService;
        this.personMapper = personMapper;
    }

    @Override
    public List<PersonViewDto> findAll() {
        /*return personService.findAll().stream()
                .map(personMapper::fromEntityToViewDto)
                .collect(Collectors.toList());*/
        return null;
    }

    @Override
    public PersonDto save(PersonSaveDto object) {
        return null;
    }

    @Override
    public PersonDto findById(UUID id) {
        return null;
    }

    @Override
    public PersonDto update(PersonDto object) {
        return null;
    }

    @Override
    public PersonDto deleteById(Long id) {
        return null;
    }
}
