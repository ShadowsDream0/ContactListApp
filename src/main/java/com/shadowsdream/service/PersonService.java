package com.shadowsdream.service;

import com.shadowsdream.dto.PersonDto;
import com.shadowsdream.dto.PersonSaveDto;
import com.shadowsdream.dto.PersonViewDto;
import com.shadowsdream.dto.PhoneNumberDto;
import com.shadowsdream.model.Person;
import com.shadowsdream.model.PhoneNumber;

import java.util.List;

public interface PersonService {

    Long save(PersonSaveDto personSaveDto);

    List<PersonViewDto> findAll();

    PersonDto findById(Long id);

    void updatePerson(PersonDto person);

    void updatePhoneNumber(PhoneNumberDto phoneNumberDto);

    void removePerson(Long id);

    void removePhoneNumber(Long id);
}
