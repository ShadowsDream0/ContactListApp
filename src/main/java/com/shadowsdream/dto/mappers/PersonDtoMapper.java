package com.shadowsdream.dto.mappers;

import com.shadowsdream.dto.PersonDto;
import com.shadowsdream.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface PersonDtoMapper {

    @Mappings({
            @Mapping(target = "id", source = "personDto.id"),
            @Mapping(target = "firstName", source = "personDto.firstName"),
            @Mapping(target = "lastName", source = "personDto.lastName"),
            @Mapping(target = "gender", source = "personDto.gender"),
            @Mapping(target = "birthday", source = "personDto.birthday"),
            @Mapping(target = "city", source = "personDto.city"),
            @Mapping(target = "email", source = "personDto.email"),
            @Mapping(target = "phoneNumbers", source = "personDto.phoneNumbers")
    })
    public Person fromDto(PersonDto personDto);

    @Mappings({
            @Mapping(target = "id", source = "person.id"),
            @Mapping(target = "firstName", source = "person.firstName"),
            @Mapping(target = "lastName", source = "person.lastName"),
            @Mapping(target = "gender", source = "person.gender"),
            @Mapping(target = "birthday", source = "person.birthday"),
            @Mapping(target = "city", source = "person.city"),
            @Mapping(target = "email", source = "person.email"),
            @Mapping(target = "phoneNumbers", source = "person.phoneNumbers")
    })
    public PersonDto toDto(Person person);
}
