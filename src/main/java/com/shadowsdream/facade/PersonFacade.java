package com.shadowsdream.facade;

import com.shadowsdream.dto.PersonDto;
import com.shadowsdream.dto.PersonSaveDto;

import java.util.List;

public interface PersonFacade extends Facade<PersonDto, PersonSaveDto> {
    List<PersonDto> findAll();
}
