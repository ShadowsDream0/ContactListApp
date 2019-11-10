package com.shadowsdream.model.enums;

public enum PhoneType {
    MOBILE("mobile"),
    DESKTOP("desktop");

    private String phoneType;

    PhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    @Override
    public String toString() {
        return this.phoneType;
    }
}
