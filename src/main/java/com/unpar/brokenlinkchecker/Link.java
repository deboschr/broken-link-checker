package com.unpar.brokenlinkchecker;

import javafx.beans.property.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Link {

   private final StringProperty url;
   private final IntegerProperty statusCode;
   private final ObjectProperty<Instant> accessTime;

   private final Set<Link> connections;

   public Link(String url, int statusCode, Instant accessTime) {
      this.url = new SimpleStringProperty(url);
      this.statusCode = new SimpleIntegerProperty(statusCode);
      this.accessTime = new SimpleObjectProperty<>(accessTime);

      this.connections = new HashSet<>();
   }

   // ===============================================================================
   // URL
   public String getUrl() {
      return url.get();
   }

   public void setUrl(String value) {
      url.set(value);
   }

   public StringProperty urlProperty() {
      return url;
   }

   // ===============================================================================
   // Status Cod
   public int getStatusCode() {
      return statusCode.get();
   }

   public void setStatusCode(int value) {
      statusCode.set(value);
   }

   public IntegerProperty statusProperty() {
      return statusCode;
   }

   // ===============================================================================
   // Access Time
   public Instant getAccessTime() {
      return accessTime.get();
   }

   public void setAccessTime(Instant value) {
      accessTime.set(value);
   }

   public ObjectProperty<Instant> accessTimeProperty() {
      return accessTime;
   }

   // ===============================================================================
   // Utils
   public void connect(Link other) {
      if (other == null || other == this) {
         return;
      }

      this.connections.add(other);
      other.connections.add(this);
   }

   public Set<Link> getConnections() {
      return connections;
   }
}
