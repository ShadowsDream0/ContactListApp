package com.shadowsdream.model.enums;

public enum PhoneType {
    WORK("work"),
    HOME("home");

    private String phoneType;

    PhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    @Override
    public String toString() {
        return this.phoneType;
    }
}
