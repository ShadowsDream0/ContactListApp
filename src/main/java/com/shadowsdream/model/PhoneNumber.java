package com.shadowsdream.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@ToString
public class PhoneNumber {
    private Long id;
    private String phone;
    private PhoneType type;
}
