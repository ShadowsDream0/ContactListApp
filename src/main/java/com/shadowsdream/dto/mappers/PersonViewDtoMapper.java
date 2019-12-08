package com.shadowsdream.dto.mappers;

import com.shadowsdream.dto.PersonViewDto;
import com.shadowsdream.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface PersonViewDtoMapper {

    @Mappings({
            @Mapping(target = "id", source = "personViewDto.id"),
            @Mapping(target = "firstName", source = "personViewDto.firstName"),
            @Mapping(target = "lastName", source = "personViewDto.lastName"),
    })
    public Person fromDto(PersonViewDto personViewDto);

    @Mappings({
            @Mapping(target = "id", source = "person.id"),
            @Mapping(target = "firstName", source = "person.firstName"),
            @Mapping(target = "lastName", source = "person.lastName"),
    })
    public PersonViewDto toDto(Person person);
}
