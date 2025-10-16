package com.unpar.brokenlinkchecker.model;

import org.jsoup.nodes.Document;

/**
 * Pembungkus hasil fetch, berisi metadata (FetchResult) dan dokumen HTML
 * (optional).
 */
public record FetchResponse(Link link, Document document) {
}
