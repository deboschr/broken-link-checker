package com.unpar.brokenlinkchecker.model;

import org.jsoup.nodes.Document;

/**
 * Hasil dari proses pengambilan (fetch) suatu URL.
 * Berisi informasi status HTTP, tipe konten, URL akhir,
 * dokumen HTML, serta tipe error jika fetch gagal total.
 */
public class FetchResult {

   private final int statusCode;
   private final String contentType;
   private final String finalUrl;
   private final Document document;
   private final String error;

   public FetchResult(int statusCode, String contentType, String finalUrl, Document document, String error) {
      this.statusCode = statusCode;
      this.contentType = contentType;
      this.finalUrl = finalUrl;
      this.document = document;
      this.error = error;
   }

   public int getStatusCode() {
      return statusCode;
   }

   public String getContentType() {
      return contentType;
   }

   public String getFinalUrl() {
      return finalUrl;
   }

   public Document getDocument() {
      return document;
   }

   public String getError() {
      return error;
   }
}
