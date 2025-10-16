package com.unpar.brokenlinkchecker.model;

import javafx.beans.property.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class BrokenLink extends Link {

   /**
    * key : objek WebpageLink dimana tautan rusak ini ditemukan
    * value : anchor text dari tautan pada webpage tersebut
    */
   private final Map<WebpageLink, String> webpageLinks;

   public BrokenLink(String url, int statusCode, Instant accessTime) {
      super(url, statusCode, accessTime);
      this.webpageLinks = new HashMap<>();
   }

   public void addWebpageLinks(WebpageLink webpageLink, String anchorText) {
      if (!webpageLinks.containsKey(webpageLink)) {
         webpageLinks.put(webpageLink, anchorText);
         webpageLink.addBrokenLink(this, anchorText);
      }
   }

   public Map<WebpageLink, String> getWebpageLinks() {
      return webpageLinks;
   }

}
