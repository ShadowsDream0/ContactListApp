package com.shadowsdream.mapper;

import com.shadowsdream.dto.PersonDto;
import com.shadowsdream.dto.PersonViewDto;
import com.shadowsdream.model.Person;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public PersonMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public PersonViewDto fromEntityToViewDto(Person person) {
        return modelMapper.map(person, PersonViewDto.class);
    }

    public Person fromViewDtoToEntity(PersonViewDto personViewDto) {
        return modelMapper.map(personViewDto, Person.class);
    }
}
