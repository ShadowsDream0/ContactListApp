package com.shadowsdream.facade.impl;

import com.shadowsdream.dto.PersonDto;
import com.shadowsdream.dto.PersonSaveDto;
import com.shadowsdream.facade.PersonFacade;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PersonFacadeImpl implements PersonFacade {
    @Override
    public List<PersonDto> findAll() {
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
