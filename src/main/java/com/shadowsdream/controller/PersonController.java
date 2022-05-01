package com.shadowsdream.controller;

import com.shadowsdream.dto.PersonDto;
import com.shadowsdream.dto.PersonViewDto;
import com.shadowsdream.facade.PersonFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonFacade personFacade;

    @Autowired
    PersonController(PersonFacade personFacade){
        this.personFacade = personFacade;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PersonDto> showAllContacts() {
        return personFacade.findAll();
    }
}
