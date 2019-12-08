package com.shadowsdream.dto;

import com.shadowsdream.model.enums.PhoneType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
public class PhoneNumberDto {

    private Long id;
    private String phone;
    private PhoneType type;

    public PhoneNumberDto(){}

    public PhoneNumberDto(Long id, String phone, PhoneType type) {
        Objects.requireNonNull(id, "Argument id must not be null");
        Objects.requireNonNull(phone, "Argument phone must not be null");
        Objects.requireNonNull(type, "Argument type must not be null");

        if (id < 1) {
            throw new IllegalArgumentException("Argument id must not be less then 1");
        }

        this.id = id;
        this.phone = phone;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof PhoneNumberDto)) {
            return false;
        }

        PhoneNumberDto that = (PhoneNumberDto) o;

        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

}
