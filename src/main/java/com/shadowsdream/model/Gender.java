package com.shadowsdream.model;

public enum Gender {
    MALE("male"),
    FEMALE("female"),
    TRANSGENDER("transgender");

    private String gender;

    Gender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return this.gender;
    }
}
