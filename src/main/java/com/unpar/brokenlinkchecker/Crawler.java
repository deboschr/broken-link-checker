package com.unpar.brokenlinkchecker;

import java.io.IOException;
import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.unpar.brokenlinkchecker.model.FetchResult;
import com.unpar.brokenlinkchecker.model.Link;

public class Crawler {
    private final String rootHost;
    private final Set<Link> links;
    private final Queue<String> frontier;
    private final Set<String> repositories;

    private static final String USER_AGENT = "BrokenLinkChecker/1.0 (+https://github.com/jakeschr/broken-link-checker; contact: 6182001060@student.unpar.ac.id)";
    private static final int TIMEOUT = 10000;

    public Crawler(String seedUrl) {
        this.rootHost = getHostUrl(seedUrl);
        this.repositories = new HashSet<>();
        this.frontier = new ArrayDeque<>();
        this.links = new HashSet<>();

        frontier.offer(seedUrl);
    }

    public void start() {
        while (!frontier.isEmpty()) {

            String wpUrl = frontier.poll();

            if (!repositories.add(wpUrl)) {
                continue;
            }

            FetchResult result = fetchUrl(wpUrl);

            if (result.getStatusCode() >= 400 || result.getError() != null) {
                Link brokenLink = new Link(wpUrl, result.getStatusCode(), Instant.now(), result.getError());
                links.add(brokenLink);
                // harusnya di sini kita tambahkan relasi ke parentnya dan strem ke UI
                continue;
            }

            // Skip kalau beda host dengan seed url atau bukan HTML
            String finalHost = getHostUrl(result.getFinalUrl());
            if (finalHost == null
                    || !finalHost.equals(rootHost)
                    || result.getContentType() == null
                    || !result.getContentType().startsWith("text/html")) {
                continue;
            }

            Document doc = result.getDocument();
            if (doc == null) {
                continue;
            }

            Link webpageLink = new Link(wpUrl, result.getStatusCode(), Instant.now(), result.getError());
            Map<String, String> linksOnWebpage = extractUrl(doc);

            for (Map.Entry<String, String> entry : linksOnWebpage.entrySet()) {
                String entryUrl = entry.getKey();
                String entryAnchorText = entry.getValue();

                String entryHost = getHostUrl(result.getFinalUrl());

                if (entryHost.equalsIgnoreCase(rootHost)) {
                    frontier.offer(entryUrl);
                } else {
                    if (!repositories.add(entryUrl)) {
                        continue;
                    }

                    FetchResult entryResult = fetchUrl(entryUrl);

                    if (entryResult.getStatusCode() >= 400 || entryResult.getError() != null) {
                        Link brokenLink = new Link(entryUrl, entryResult.getStatusCode(), Instant.now(),
                                entryResult.getError());
                        links.add(brokenLink);
                        webpageLink.setConnection(brokenLink, entryAnchorText);
                        // harusnya abis ini stream ke UI
                    }

                }

            }

        }

    }

    public void stop() {

    }

    private FetchResult fetchUrl(String url) {
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .execute();

            int status = response.statusCode();
            String type = response.contentType();
            String finalUrl = response.url().toString();

            Document doc = null;

            // hanya parse HTML kalau kontennya HTML
            if (type != null && type.startsWith("text/html") && status == 200) {
                doc = response.parse();
            }

            return new FetchResult(status, type, finalUrl, doc, null);

        } catch (IOException e) {
            // Gagal koneksi / timeout / DNS / SSL, dsb.
            return new FetchResult(0, null, url, null, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private Map<String, String> extractUrl(Document doc) {

        Map<String, String> results = new HashMap<>();

        for (Element a : doc.select("a[href]")) {

            String absoluteUrl = a.attr("abs:href");

            String cleanedUrl = normalizeUrl(absoluteUrl);

            if (cleanedUrl == null) {
                continue;
            }

            String anchorText = a.text().trim();

            results.put(cleanedUrl, anchorText);
        }

        return results;
    }

    private String normalizeUrl(String rawUrl) {

        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            return null;
        }

        URI url;
        try {
            url = URI.create(rawUrl.trim());
        } catch (IllegalArgumentException e) {
            return rawUrl;
        }

        // scheme
        String scheme = url.getScheme();
        if (scheme == null || scheme.isEmpty()) {
            return rawUrl;
        }
        if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
            return null;
        }
        scheme = scheme.toLowerCase();

        // host
        String host = url.getHost();
        if (host == null || host.isEmpty()) {
            return null;
        }

        // port
        int port = url.getPort();
        if ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)) {
            port = -1;
        }

        // path
        String path = url.getPath();
        if (path == null) {
            path = "";
        }

        // query
        String query = url.getRawQuery();

        // Bangun ulang URL dengan objel URI tanpa menyertakan fragment
        try {
            URI cleanedUrl = new URI(scheme, null, host, port, path, query, null);
            return cleanedUrl.toASCIIString();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private String getHostUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        try {
            URI uri = URI.create(url.trim());
            String host = uri.getHost();

            if (host == null || host.isEmpty()) {
                return null;
            }

            // konversi ke format ASCII untuk domain internasional
            return IDN.toASCII(host.toLowerCase());
        } catch (IllegalArgumentException e) {
            // URL tidak valid secara sintaks
            return null;
        }
    }

}
