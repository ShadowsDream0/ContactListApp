package com.shadowsdream.dto;

import com.shadowsdream.model.enums.Gender;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class PersonSaveDto {
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private String city;
    private String email;
}
