package com.shadowsdream.model;

import com.shadowsdream.model.PhoneNumber;
import com.shadowsdream.model.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Person {

    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private String city;
    private String email;
    private List<PhoneNumber> phoneNumbers;
}
