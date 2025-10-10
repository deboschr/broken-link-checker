package com.unpar.brokenlinkchecker.v1;

import javafx.beans.property.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Model BrokenLink untuk TableView
 */
public class BrokenLink {

    private final StringProperty url;
    private final IntegerProperty statusCode;
    private final ObjectProperty<Instant> accessTime;

    /**
     * key : url webpage
     * value : anchor text
     */
    private final Map<String, String> webpages;

    public BrokenLink(String url, int statusCode, Instant accessTime) {
        this.url = new SimpleStringProperty(url);
        this.statusCode = new SimpleIntegerProperty(statusCode);
        this.accessTime = new SimpleObjectProperty<>(accessTime);
        this.webpages = new HashMap<>();
    }

    // ===== URL =====
    public String getUrl() {
        return url.get();
    }

    public void setUrl(String value) {
        url.set(value);
    }

    public StringProperty urlProperty() {
        return url;
    }

    // ===== Status Code =====
    public int getStatusCode() {
        return statusCode.get();
    }

    public void setStatusCode(int value) {
        statusCode.set(value);
    }

    public IntegerProperty statusCodeProperty() {
        return statusCode;
    }

    // ===== Access Time =====
    public Instant getAccessTime() {
        return accessTime.get();
    }

    public void setAccessTime(Instant value) {
        accessTime.set(value);
    }

    public ObjectProperty<Instant> accessTimeProperty() {
        return accessTime;
    }

    // ===== Webpages (sumber tautan) =====
    public Map<String, String> getWebpages() {
        return webpages;
    }

    public void addWebpage(String webpageUrl, String anchorText) {
        this.webpages.put(webpageUrl, anchorText);
    }
}
