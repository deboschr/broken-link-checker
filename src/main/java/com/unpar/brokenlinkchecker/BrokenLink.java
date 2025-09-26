package com.unpar.brokenlinkchecker;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class BrokenLink {
    private final String url;
    private final int statusCode;
    private final Instant accessTime;

    /**
     * Untuk menyimpan daftar halaman sumber dimana tautan ditemukan.
     * key : url webpage
     * value : anchor text
     */
    private final Map<String, String> webpages;

    public BrokenLink(String url, int statusCode, Instant accessTime) {
        this.url = url;
        this.statusCode = statusCode;
        this.accessTime = accessTime;
        this.webpages = new HashMap<>();
    }

    public String getUrl() {
        return url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Instant getAccessTime() {
        return accessTime;
    }

    public Map<String, String> getWebpages() {
        return webpages;
    }

    public void addWebpage(String webpageUrl, String anchorText) {
        this.webpages.put(webpageUrl, anchorText);
    }
}
