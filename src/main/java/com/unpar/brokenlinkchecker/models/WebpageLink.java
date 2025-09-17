package com.unpar.brokenlinkchecker.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WebpageLink extends Link {
    private final StringProperty title;
    private final IntegerProperty depth;

    public WebpageLink(String url, int statusCode, String title, int depth) {
        super(url, statusCode);
        this.title = new SimpleStringProperty(title);
        this.depth = new SimpleIntegerProperty(depth);
    }

    // property getter untuk TableView
    public StringProperty titleProperty() { return title; }
    public IntegerProperty depthProperty() { return depth; }

    // getter/setter biasa
    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }

    public int getDepth() { return depth.get(); }
    public void setDepth(int value) { depth.set(value); }
}
