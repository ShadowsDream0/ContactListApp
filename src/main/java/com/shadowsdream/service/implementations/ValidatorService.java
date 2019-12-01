package com.shadowsdream.service.implementations;

import com.shadowsdream.exception.InvalidInputException;

import java.time.LocalDate;

public interface ValidatorService {

    void validateEmail(String email) throws InvalidInputException;

    LocalDate validateAndGetBirthday(String dateString) throws InvalidInputException;

    void validatePhoneNumber(String phoneNumber) throws InvalidInputException;

    void validateProperName(String name) throws InvalidInputException;

}
