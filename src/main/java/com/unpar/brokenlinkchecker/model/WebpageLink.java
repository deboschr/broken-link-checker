package com.unpar.brokenlinkchecker.model;

import javafx.beans.property.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class WebpageLink extends Link {

   /**
    * key : objek BrokenLink yang ditemukan di halaman ini
    * value : anchor text dari tautan rusak tersebut
    */
   private final Map<BrokenLink, String> brokenLinks;

   public WebpageLink(String url, int statusCode, Instant accessTime) {
      super(url, statusCode, accessTime);
      this.brokenLinks = new HashMap<>();
   }

   public void addBrokenLink(BrokenLink brokenLink, String anchorText) {
      if (!brokenLinks.containsKey(brokenLink)) {
         brokenLinks.put(brokenLink, anchorText);
         brokenLink.addWebpageLinks(this, anchorText);
      }
   }

   public Map<BrokenLink, String> getBrokenLinks() {
      return brokenLinks;
   }

}
