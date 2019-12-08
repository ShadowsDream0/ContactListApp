package com.shadowsdream.dto;

import com.shadowsdream.model.enums.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@ToString
@Builder
public class PersonDto {

    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private String city;
    private String email;

    private List<PhoneNumberDto> phoneNumbers;

    public PersonDto() {}

    public PersonDto(Long id, String firstName, String lastName, Gender gender, LocalDate birthday,
                     String city, String email, List<PhoneNumberDto> phoneNumbers) {
        Objects.requireNonNull(id, "Argument id must not be null");
        Objects.requireNonNull(firstName, "Argument firstName must not be null");
        Objects.requireNonNull(lastName, "Argument lastName must not be null");
        Objects.requireNonNull(gender, "Argument gender must not be null");
        Objects.requireNonNull(birthday, "Argument birthday must not be null");
        Objects.requireNonNull(city, "Argument city must not be null");
        Objects.requireNonNull(email, "Argument email must not be null");

        if (id < 1) {
            throw new IllegalArgumentException("Argument must not be less then 1");
        }

        this.id = id;
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

        if (!(o instanceof PersonDto)) {
            return false;
        }

        PersonDto that = (PersonDto) o;

        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
