package com.shadowsdream.dto.mappers;

import com.shadowsdream.dto.PhoneNumberDto;
import com.shadowsdream.model.PhoneNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface PhoneNumberDtoMapper {

    @Mappings({
            @Mapping(target = "id", source = "phoneNumberDto.id"),
            @Mapping(target = "phone", source = "phoneNumberDto.phone"),
            @Mapping(target = "type", source = "phoneNumberDto.type")
    })
    public PhoneNumber fromDto(PhoneNumberDto phoneNumberDto);

    @Mappings({
            @Mapping(target = "id", source = "phoneNumber.id"),
            @Mapping(target = "phone", source = "phoneNumber.phone"),
            @Mapping(target = "type", source = "phoneNumber.type")
    })
    public PhoneNumberDto toDto(PhoneNumber phoneNumber);
}
