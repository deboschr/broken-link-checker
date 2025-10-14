package com.unpar.brokenlinkchecker.v4;

import javafx.beans.property.*;
import java.time.Instant;

public class Link {

   private final StringProperty url;
   private final StringProperty status;
   private final ObjectProperty<Instant> accessTime;
   private final int statusCode;

   public Link(String url, int statusCode, Instant accessTime) {
      this.url = new SimpleStringProperty(url);
      this.status = new SimpleStringProperty(HttpStatus.getStatus(statusCode));
      this.accessTime = new SimpleObjectProperty<>(accessTime);

      this.statusCode = statusCode;
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
   // Status
   public String getStatus() {
      return status.get();
   }

   // ini dipakai kalau status code yang dikirim di contractor tidak valid misalnya
   // 0 atau 999 maka artinya errornya perperti koneksi error dll
   public void setStatus(String value) {
      status.set(value);
   }

   public StringProperty statusProperty() {
      return status;
   }

   public int getStatusCode() {
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
}
