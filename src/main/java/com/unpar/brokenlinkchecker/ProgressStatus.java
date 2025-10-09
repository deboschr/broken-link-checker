package com.unpar.brokenlinkchecker;

public enum ProgressStatus {
    IDLE(""),
    RUNNING("Running"),
    STOPPED("Stopped"),
    COMPLETED("Completed");

    private final String text;

    ProgressStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
