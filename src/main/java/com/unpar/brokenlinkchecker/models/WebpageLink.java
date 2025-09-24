package com.unpar.brokenlinkchecker.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class WebpageLink extends Link {
    private final IntegerProperty linkCount;
    private final ObjectProperty<Instant> accessTime;
    private final Set<BrokenLink> brokenLinks;

    public WebpageLink(String url, int statusCode, int linkCount, Instant accessTime) {
        super(url, statusCode);
        this.linkCount = new SimpleIntegerProperty(linkCount);
        this.accessTime = new SimpleObjectProperty<>(accessTime);
        this.brokenLinks = new HashSet<>();
    }

    // Property
    public IntegerProperty linkCountProperty() {
        return linkCount;
    }

    public ObjectProperty<Instant> accessTimeProperty() {
        return accessTime;
    }

    // Getter & Setter
    public int getLinkCount() {
        return linkCount.get();
    }

    public void setLinkCount(int value) {
        linkCount.set(value);
    }

    public Instant getAccessTime() {
        return accessTime.get();
    }

    public void setAccessTime(Instant value) {
        accessTime.set(value);
    }

    public Set<BrokenLink> getBrokenLinks() {
        return brokenLinks;
    }

    public void addBrokenLink(BrokenLink link) {
        brokenLinks.add(link);
        link.addSourceWebpage(this); // sinkronisasi dua arah
    }
}
