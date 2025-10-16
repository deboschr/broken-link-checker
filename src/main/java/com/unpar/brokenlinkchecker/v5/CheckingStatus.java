package com.unpar.brokenlinkchecker.v5;

public enum CheckingStatus {
    IDLE("....."),
    CHECKING("Checking"),
    STOPPED("Stopped"),
    COMPLETED("Completed");

    private final String text;

    CheckingStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}