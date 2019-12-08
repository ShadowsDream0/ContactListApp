package com.shadowsdream.model;

import com.shadowsdream.model.enums.PhoneType;
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
