package com.shadowsdream.dto.mappers;

import com.shadowsdream.dto.PersonSaveDto;
import com.shadowsdream.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface PersonSaveDtoMapper {
    @Mappings({
            @Mapping(target = "firstName", source = "personSaveDto.firstName"),
            @Mapping(target = "lastName", source = "personSaveDto.lastName"),
            @Mapping(target = "gender", source = "personSaveDto.gender"),
            @Mapping(target = "birthday", source = "personSaveDto.birthday"),
            @Mapping(target = "city", source = "personSaveDto.city"),
            @Mapping(target = "email", source = "personSaveDto.email"),
            @Mapping(target = "phoneNumbers", source = "personSaveDto.phoneNumbers")
    })
    public Person fromDto(PersonSaveDto personSaveDto);

    @Mappings({
            @Mapping(target = "firstName", source = "person.firstName"),
            @Mapping(target = "lastName", source = "person.lastName"),
            @Mapping(target = "gender", source = "person.gender"),
            @Mapping(target = "birthday", source = "person.birthday"),
            @Mapping(target = "city", source = "person.city"),
            @Mapping(target = "email", source = "person.email"),
            @Mapping(target = "phoneNumbers", source = "person.phoneNumbers")
    })
    public PersonSaveDto toDto(Person person);
}
