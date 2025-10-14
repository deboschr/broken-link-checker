package com.unpar.brokenlinkchecker.v4;

import javafx.beans.property.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class WebpageLink extends Link {

   private final StringProperty url;
   private final StringProperty status;
   private final ObjectProperty<Instant> accessTime;
   private final int statusCode;

   /**
    * Daftar webpage dan anchor text dari BrokenLink
    * key : url webpage
    * value : anchor text
    */
   private final Map<String, String> webpages;

   public WebpageLink(String url, int statusCode, Instant accessTime) {
      this.url = new SimpleStringProperty(url);
      this.status = new SimpleStringProperty(HttpStatus.getStatus(statusCode));
      this.accessTime = new SimpleObjectProperty<>(accessTime);
      this.webpages = new HashMap<>();

      this.statusCode = statusCode;
      super(url, statusCode, accessTime);
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

   // ===============================================================================
   // Webpages
   public Map<String, String> getWebpages() {
      return webpages;
   }

   public void addWebpage(String webpageUrl, String anchorText) {
      this.webpages.put(webpageUrl, anchorText);
   }
}
