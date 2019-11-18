package com.shadowsdream.dto;

import com.shadowsdream.model.Person;
import com.shadowsdream.model.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
public class PersonSaveDto {
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private String city;
    private String email;

    private List<PhoneNumberSaveDto> phoneNumbers;

    public PersonSaveDto(){}

    public PersonSaveDto(String firstName, String lastName, Gender gender, LocalDate birthday,
                     String city, String email, List<PhoneNumberSaveDto> phoneNumbers) {
        Objects.requireNonNull(firstName, "Argument firstName must not be null");
        Objects.requireNonNull(lastName, "Argument lastName must not be null");
        Objects.requireNonNull(gender, "Argument gender must not be null");
        Objects.requireNonNull(birthday, "Argument birthday must not be null");
        Objects.requireNonNull(city, "Argument city must not be null");
        Objects.requireNonNull(email, "Argument email must not be null");

        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.city = city;
        this.email = email;
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return false;
        }

        if (!(o instanceof PersonSaveDto)) {
            return false;
        }

        PersonSaveDto that = (PersonSaveDto) o;

        return this.email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return this.email.hashCode();
    }
}
