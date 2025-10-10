package com.unpar.brokenlinkchecker.v1;

public enum CrawlStatus {
    IDLE("Belum mulai"),
    RUNNING("Running"),
    STOPPED("Stopped"),
    COMPLETED("Completed");

    private final String text;

    CrawlStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
