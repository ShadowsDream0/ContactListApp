package com.shadowsdream.service;

import com.shadowsdream.exception.InvalidInputException;
import com.shadowsdream.util.logging.ContactListLogger;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Pattern;

public class ValidatorServiceImpl implements ValidatorService {

    private static final int MIN_EMAIL_LENGTH = 5;
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final Pattern VALID_EMAIL_PATTERN = Pattern.compile("^[\\w+-.%]+@[a-z0-9.-]+[.][a-z]{2,6}$",
                                                                        Pattern.CASE_INSENSITIVE);
    private static final int MIN_PROPER_NAME_LENGTH = 2;
    private static final int MAX_PROPER_NAME_LENGTH = 90;
    private static final Pattern VALID_PROPER_NAME_PATTERN = Pattern.compile("[a-z]{2,}[-\\s]?([a-z]{2,})?$",
                                                                            Pattern.CASE_INSENSITIVE);
    private static final int PHONE_NUMBER_LENGTH = 15;
    private static final Pattern VALID_PHONE_NUMBER_PATTERN = Pattern.compile("^[+]\\d(-\\d{3}){2}-\\d{4}$");

    private static ValidatorService validatorService = null;

    private ValidatorServiceImpl(){}

    public static ValidatorService getInstance() {
        if (validatorService == null) {
            return new ValidatorServiceImpl();
        }

        return validatorService;
    }


    @Override
    public void validateEmail(String email) throws InvalidInputException {
        Objects.requireNonNull(email, "Argument email must not be null");

        if (email.length() < MIN_EMAIL_LENGTH) {
            throw new InvalidInputException("email is too short");
        }

        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new InvalidInputException("email is too long");
        }

        if (!VALID_EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidInputException("email is not valid");
        }
    }


    @Override
    public LocalDate validateAndGetBirthday(String dateString) throws InvalidInputException {
        Objects.requireNonNull(dateString, "Argument dateString must not be null");

        LocalDate birthday = null;

        try {
            birthday = LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("date is not valid");
        }

        if (Period.between(birthday, LocalDate.now()).getYears() > 120) {
            throw new InvalidInputException("you can't be that old");
        }

        return birthday;
    }


    @Override
    public void validatePhoneNumber(String phoneNumber) throws InvalidInputException {
        Objects.requireNonNull(phoneNumber, "Argument phoneNumber must not be null");

        if (phoneNumber.length() != PHONE_NUMBER_LENGTH) {
            throw new InvalidInputException("length of phone number must be 15 symbols");
        }

        if (!VALID_PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new InvalidInputException("phone number is not valid");
        }
    }


    @Override
    public void validateProperName(String name) throws InvalidInputException {
        ContactListLogger.getLog().info("Invoked validateProperName() in Validator...");
        Objects.requireNonNull(name, "Argument name must not be null");
        ContactListLogger.getLog().debug("String from input: " + name);

        if (name.length() < MIN_PROPER_NAME_LENGTH) {
            throw new InvalidInputException("name is too short");
        }

        if (name.length() > MAX_PROPER_NAME_LENGTH) {
            throw new InvalidInputException("name is too long");
        }


        if (!VALID_PROPER_NAME_PATTERN.matcher(name).matches()) {
            throw new InvalidInputException("it is not a name");
        }
    }
}
