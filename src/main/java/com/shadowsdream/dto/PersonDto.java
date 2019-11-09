package com.shadowsdream.dto;

import com.shadowsdream.model.enums.Gender;

import java.time.LocalDate;

public class PersonDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private String city;
    private String email;
}
