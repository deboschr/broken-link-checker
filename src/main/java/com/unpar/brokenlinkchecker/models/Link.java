package com.unpar.brokenlinkchecker.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Link {
    private final StringProperty url;
    private final IntegerProperty statusCode;

    public Link(String url, int statusCode) {
        this.url = new SimpleStringProperty(url);
        this.statusCode = new SimpleIntegerProperty(statusCode);
    }

    // property getters untuk TableView
    public StringProperty urlProperty() { return url; }
    public IntegerProperty statusCodeProperty() { return statusCode; }

    // getter/setter biasa
    public String getUrl() { return url.get(); }
    public void setUrl(String value) { url.set(value); }

    public int getStatusCode() { return statusCode.get(); }
    public void setStatusCode(int value) { statusCode.set(value); }
}
