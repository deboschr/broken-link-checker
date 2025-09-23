package com.unpar.brokenlinkchecker.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BrokenLink extends Link {
    private final StringProperty anchorText;
    private final StringProperty webpageUrl;

    public BrokenLink(String url, int statusCode, String anchorText, String webpageUrl) {
        super(url, statusCode);
        this.anchorText = new SimpleStringProperty(anchorText);
        this.webpageUrl = new SimpleStringProperty(webpageUrl);
    }

    // Properties
//    public StringProperty anchorTextProperty() { return anchorText; }
//    public StringProperty webpageUrlProperty() { return webpageUrl; }

    // Getters & Setters
    public String getAnchorText() { return anchorText.get(); }
    public void setAnchorText(String value) { anchorText.set(value); }

    public String getWebpageUrl() { return webpageUrl.get(); }
    public void setWebpageUrl(String value) { webpageUrl.set(value); }
}
