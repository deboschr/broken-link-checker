package com.unpar.brokenlinkchecker.v3;

public enum CheckingStatus {
    IDLE("....."),
    RUNNING("Running"),
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