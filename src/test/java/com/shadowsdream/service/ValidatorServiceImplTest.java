package com.shadowsdream.service;

import com.shadowsdream.exception.InvalidInputException;
import com.shadowsdream.service.implementations.ValidatorService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorServiceImplTest {

    private static ValidatorService validatorService = null;

    @BeforeAll
    static void setUp() {
        validatorService = ValidatorServiceImpl.getInstance();
    }

    @Test
    @DisplayName("[mail-null] Should throw NPE with message")
    void validateEmailNullPointerExceptionTest() {
        Throwable actual = assertThrows(NullPointerException.class,
                () -> validatorService.validateEmail(null));
        assertEquals("Argument email must not be null", actual.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[mail-short] Should throw InvalidInputException when passed in email under 5 chars")
    @ValueSource(strings = { "", "n", "a@b.c" })
    void validateEmailShortTest(String email) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validateEmail(email));

        assertAll(
                () -> assertEquals("email is too short", actual.getMessage())
        );
    }

    
    @ParameterizedTest
    @DisplayName("[mail-long] Should throw InvalidInputException when passed in email over 254 chars")
    @ValueSource(strings = { "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567" +
            "8901234567890123456789012345678901234567890123456789@m.com", // 255 chars
            "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567" +
                    "890123456789012345678901234567890123456789012345678912345678901234567890@m.com"}) // over 255 chars
    void validateEmailLongTest(String email) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validateEmail(email));

        assertAll(
                () -> assertEquals("email is too long", actual.getMessage())
        );
    }

    
    @ParameterizedTest
    @DisplayName("[mail-invalid] Should throw InvalidInputException when passed in invalid email")
    @ValueSource(strings = { "$asd@gmail.com", "@mail.com", "foo@*.com", "foo@gmail", "foo@gmail.abcdefh" })
    void validateEmailInvalidTest(String email) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validateEmail(email));

        assertAll(
                () -> assertEquals("email is not valid", actual.getMessage())
        );
    }


    @ParameterizedTest
    @DisplayName("[mail-valid] Nothing should be thrown and returned")
    @ValueSource(strings = { "a@m.co", "foo.bar@mail.co", "-blah-@b.uk", "foo@gmail.com", "foo@gmail.barbaz" })
    void validateEmailValidTest(String email) throws InvalidInputException {
        validatorService.validateEmail(email);
    }


    @Test
    @DisplayName("[birthday-null] Should throw NPE with message")
    void validateAndGetBirthdayNpeTest() {
        Throwable actual = assertThrows(NullPointerException.class,
                () -> validatorService.validateAndGetBirthday(null));
        assertEquals("Argument dateString must not be null", actual.getMessage());
    }

    
    @ParameterizedTest
    @DisplayName("[birthday-invalid] Should throw InvalidInputException with message 'date is not valid'")
    @ValueSource(strings = { "", "1989", "12-07-1989", "1989.07.12", "100000-07-12", "1989-13-07", "1989-07-32" })
    void validateAndGetBirthdayParsingDateTest(String date) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validateAndGetBirthday(date));
        assertEquals("date is not valid", actual.getMessage());
    }

    
    @ParameterizedTest
    @DisplayName("[birthday-old] Should throw InvalidInputException with message 'you can't be that old'")
    @ValueSource(strings = { "1654-09-11", "1898-11-18" })
    void validateAndGetBirthdayManyYearsTest(String date) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validateAndGetBirthday(date));
        assertEquals("you can't be that old", actual.getMessage());
    }

    
    @ParameterizedTest
    @DisplayName("[birthday-young] Should throw InvalidInputException with message 'you are underage to use this application'")
    @ValueSource(strings = { "2019-11-18", "2002-11-18" })
    void validateAndGetBirthdayUnderageTest(String date) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validateAndGetBirthday(date));
        assertEquals("you are underage to use this application", actual.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[birthday-valid] Nothing should be thrown or returned")
    @ValueSource(strings = { "1899-11-18", "2001-11-18", "1989-07-12", "1965-02-05" })
    void validateAndGetBirthdayValidTest(String date) throws InvalidInputException {
        validatorService.validateAndGetBirthday(date);
    }


    @Test
    @DisplayName("[number-null] Should throw NPE with message")
    void validatePhoneNumberNpeTest() {
        Throwable actual = assertThrows(NullPointerException.class,
                () -> validatorService.validatePhoneNumber(null));
        assertEquals("Argument phoneNumber must not be null", actual.getMessage());
    }


    @ParameterizedTest
    @DisplayName("[number-length] Should throw InvalidInputException with message 'length of phone number must be 15 symbols'")
    @ValueSource(strings = { "", "12345678901234", "1234567890123456" })
    void validatePhoneNumberShortTest(String phoneNumber) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validatePhoneNumber(phoneNumber));
        assertEquals("length of phone number must be 15 symbols", actual.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[number-invalid] Should throw InvalidInputException with message 'phone number is not valid'")
    @ValueSource(strings = { "-1-123-456-7890", "+1-234-abc-abcd", "+1.234.567.8901", "+1-   -567-8901", "+1-234-567-890a"})
    void validatePhoneNumberInvalidTest(String phoneNumber) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validatePhoneNumber(phoneNumber));
        assertEquals("phone number is not valid", actual.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[number-valid] Should throw InvalidInputException with message 'phone number is not valid'")
    @ValueSource(strings = { "+1-234-567-8901", "+0-987-654-3210"})
    void validatePhoneNumberValidTest(String phoneNumber) throws InvalidInputException {
        validatorService.validatePhoneNumber(phoneNumber);
    }


    @Test
    @DisplayName("[name-null] Should throw NPE with message")
    void validateProperNameNpeTest() {
        Throwable actual = assertThrows(NullPointerException.class,
                () -> validatorService.validateProperName(null));
        assertEquals("Argument name must not be null", actual.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[name-short] Should throw InvalidInputException with message 'name is too short'")
    @ValueSource(strings = { "", "a"})
    void validateProperNameShortTest(String name) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validateProperName(name));
        assertEquals("name is too short", actual.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[name-long] Should throw InvalidInputException with message 'name is too long'")
    @ValueSource(strings = { "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901", // 91 chars
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012"}) // 92 chars
    void validateProperNameLongTest(String name) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validateProperName(name));
        assertEquals("name is too long", actual.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[name-invalid] Should throw InvalidInputException with message 'name is too long'")
    @ValueSource(strings = { "  ", "Foo   Bar", "1Foo", "Foo--Bar", "Foo 456", "*$", "- -"}) // 92 chars
    void validateProperNameInvalidTest(String name) {
        Throwable actual = assertThrows(InvalidInputException.class,
                () -> validatorService.validateProperName(name));
        assertEquals("it is not a name", actual.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[name-valid] Nothing should be thrown or returned'")
    @ValueSource(strings = { "Foo Bar", "Foo-Bar", "Fo", "Foo", "foo"})
    void validateProperNameValidTest(String name) throws InvalidInputException {
        validatorService.validateProperName(name);
    }

    @AfterAll
    static void tearDownAll() {
        validatorService = null;
    }
}