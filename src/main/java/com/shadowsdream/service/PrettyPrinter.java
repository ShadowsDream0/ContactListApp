package com.shadowsdream.service;

import com.shadowsdream.dto.*;

import java.util.List;
import java.util.Objects;

public class PrettyPrinter {

    private PrettyPrinter(){}

    private static final String MENU =
            "|======================================================================|\n" +
            "|                   Select an action on contact list                   |\n" +
            "|----------------------------------------------------------------------|\n" +
            "|                        <1> - show all contacts                       |\n" +
            "|                        <2> - view details of contact                 |\n" +
            "|                        <3> - remove contact                          |\n" +
            "|                        <4> - remove phone number                     |\n" +
            "|                        <5> - save contact                            |\n" +
            "|                        <6> - update contact                          |\n" +
            "|                        <7> - update phone number                     |\n" +
            "|                        <8> - import contacts from file               |\n" +
            "|        <9> - export contacts from contact list (not implemented)     |\n" +
            "|                        <10> - exit                                   |\n" +
            "|======================================================================|\n" +
            "->";

    public static void printMenu() {
        System.out.print(MENU);
    }

    public static void printPersonInfo(PersonDto person) {
        Objects.requireNonNull(person, "Argument person must not be null");

        System.out.println("================Contact info===================\n" +
                            "ID: " + person.getId() + "\n" +
                            "Name: " + person.getFirstName() + " " +
                                        person.getLastName() + "\n" +
                            "Gender: " + person.getGender() + "\n" +
                            "Birthday: " + person.getBirthday() + "\n" +
                            "City: " + person.getCity() + "\n" +
                            "Email: " + person.getEmail()
        );
        printPhoneNumbers(person.getPhoneNumbers());
    }

    public static void printPersonInfo(PersonViewDto person) {
        Objects.requireNonNull(person, "Argument person must not be null");

        System.out.println("============Contact==============\n" +
                            "ID: " + person.getId() + "\n" +
                            "Name: " + person.getFirstName() + " " +
                                        person.getLastName() + "\n" +
                            "================================="
        );
    }

    public static void printPersonInfo(PersonSaveDto person) {
        Objects.requireNonNull(person, "Argument person must not be null");

        System.out.println("================Contact info===================\n" +
                "Name: " + person.getFirstName() + " " +
                person.getLastName() + "\n" +
                "Gender: " + person.getGender() + "\n" +
                "Birthday: " + person.getBirthday() + "\n" +
                "City: " + person.getCity() + "\n" +
                "Email: " + person.getEmail()
        );
        printPhoneNumbers(person.getPhoneNumbers());
    }

    public static void printPhoneNumber(PhoneNumberDto phoneNumber) {
        if (phoneNumber == null) {
            return;
        }

        System.out.println(
                            "ID: " + phoneNumber.getId() + "; " +
                            "number: " + phoneNumber.getPhone() + " (" +
                                    phoneNumber.getType() + ")"
        );
    }


    public static void printPhoneNumber(PhoneNumberSaveDto phoneNumber) {
        if (phoneNumber == null) {
            return;
        }

        System.out.println(
                        "number: " + phoneNumber.getPhone() + " (" +
                        phoneNumber.getType() + ")"
        );
    }

    public static void printPressAnyKey() {
        System.out.println("\n--------------------------\nPress any key to continue\n--------------------------");
    }

    private static void printPhoneNumbers(List<PhoneNumberDto> phoneNumberDtoList) {
        if (phoneNumberDtoList == null) {
            return;
        }
        System.out.println("- - - - - - - Phone numbers - - - - - - - - -");
        phoneNumberDtoList.forEach(PrettyPrinter::printPhoneNumber);
        System.out.println("- - - - - - - - - - - - - -  - - - - - - - - -");

    }

    private static void printPhoneNumbers(List<PhoneNumberSaveDto> phoneNumberDtoList) {
        if (phoneNumberDtoList == null) {
            return;
        }
        System.out.println("- - - - - - - Phone numbers - - - - - - - - -");
        phoneNumberDtoList.forEach(PrettyPrinter::printPhoneNumber);
        System.out.println("- - - - - - - - - - - - - -  - - - - - - - - -");

    }

}
