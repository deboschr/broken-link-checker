package com.unpar.brokenlinkchecker.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BrokenLink extends Link {
    private final StringProperty source;

    public BrokenLink(String url, int statusCode, String source) {
        super(url, statusCode);
        this.source = new SimpleStringProperty(source);
    }

    // property getter untuk TableView
    public StringProperty sourceProperty() { return source; }

    // getter/setter biasa
    public String getSource() { return source.get(); }
    public void setSource(String value) { source.set(value); }
}
