package com.shadowsdream.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Person {
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private String city;
    private String email;
    private List<PhoneNumber> phoneNumbers;
}
