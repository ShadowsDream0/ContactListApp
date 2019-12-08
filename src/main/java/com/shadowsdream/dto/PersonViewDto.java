package com.shadowsdream.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;


@Setter
@Getter
@ToString
public class PersonViewDto {

    private Long id;
    private String firstName;
    private String lastName;

    public PersonViewDto() {}

    public PersonViewDto(Long id, String firstName, String lastName) {
        Objects.requireNonNull(id, "Argument id must not be null");
        Objects.requireNonNull(firstName, "Argument firstName must not be null");
        Objects.requireNonNull(lastName, "Argument lastName must not be null");

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return false;
        }

        if (!(o instanceof PersonViewDto)) {
            return false;
        }

        PersonViewDto that = (PersonViewDto) o;

        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
