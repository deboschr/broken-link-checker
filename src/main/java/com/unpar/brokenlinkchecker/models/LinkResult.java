package com.unpar.brokenlinkchecker.models;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LinkResult {

    private final StringProperty status;
    private final StringProperty url;

    public LinkResult(String status, String url) {
        this.status = new SimpleStringProperty(status);
        this.url = new SimpleStringProperty(url);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty urlProperty() {
        return url;
    }

    public String getStatus() {
        return status.get();
    }

    public String getUrl() {
        return url.get();
    }
}
