package com.shadowsdream.dto.mappers;

import com.shadowsdream.dto.PhoneNumberDto;
import com.shadowsdream.dto.PhoneNumberSaveDto;
import com.shadowsdream.model.PhoneNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface PhoneNumberSaveDtoMapper {
    @Mappings({
            @Mapping(target = "phone", source = "phoneNumberSaveDto.phone"),
            @Mapping(target = "type", source = "phoneNumberSaveDto.type")
    })
    public PhoneNumber fromDto(PhoneNumberSaveDto phoneNumberSaveDto);

    @Mappings({
            @Mapping(target = "phone", source = "phoneNumber.phone"),
            @Mapping(target = "type", source = "phoneNumber.type")
    })
    public PhoneNumberSaveDto toDto(PhoneNumber phoneNumber);
}
