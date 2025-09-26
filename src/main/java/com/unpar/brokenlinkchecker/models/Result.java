package com.unpar.brokenlinkchecker.models;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Result {
    private final String url;
    private final int statusCode;
    private final Instant accessTime;

    // key = URL halaman sumber, value = anchor text di halaman tersebut (boleh kosong/null)
    private final Map<String, String> sourcePages;

    public Result(String url, int statusCode, Instant accessTime) {
        this.url = url;
        this.statusCode = statusCode;
        this.accessTime = accessTime;
        this.sourcePages = new HashMap<>();
    }

    // Getter
    public String getUrl() {
        return url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Instant getAccessTime() {
        return accessTime;
    }

    public Map<String, String> getSourcePages() {
        return sourcePages;
    }

    public void addSourcePage(String pageUrl, String anchorText) {
        this.sourcePages.put(pageUrl, anchorText);
    }
}
