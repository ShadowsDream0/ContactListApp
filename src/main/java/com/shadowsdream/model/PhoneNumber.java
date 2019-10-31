package com.shadowsdream.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class PhoneNumber {
    private String phone;
    private PhoneType type;
}
