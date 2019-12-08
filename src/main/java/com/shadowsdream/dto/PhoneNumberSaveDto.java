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
public class PhoneNumberSaveDto {

    private String phone;
    private PhoneType type;

    public PhoneNumberSaveDto(){}

    public PhoneNumberSaveDto(String phone, PhoneType type) {
        Objects.requireNonNull(phone, "Argument phone must not be null");
        Objects.requireNonNull(type, "Argument type must not be null");

        this.phone = phone;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof PhoneNumberSaveDto)) {
            return false;
        }

        PhoneNumberSaveDto that = (PhoneNumberSaveDto) o;

        return this.phone.equals(that.phone) && this.type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = this.phone.hashCode();
        return result * 31 + this.type.hashCode();
    }

}
