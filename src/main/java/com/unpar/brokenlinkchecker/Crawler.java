package com.unpar.brokenlinkchecker;

import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Crawler {
    private final String rootHost;
    private final Set<String> repositories;
    private final Queue<String> frontier;

    private static final String USER_AGENT = "BrokenLinkChecker/1.0 (+https://github.com/jakeschr/broken-link-checker; contact: 6182001060@student.unpar.ac.id)";
    private static final int TIMEOUT = 10000;
    private static final HttpClient HTTP_CLIENT = HttpClient
            .newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofMillis(TIMEOUT))
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    public Crawler(String seedUrl) {
        this.rootHost = URI.create(seedUrl).getHost().toLowerCase();
        this.repositories = new HashSet<>();
        this.frontier = new ArrayDeque<>();

        frontier.offer(seedUrl);
    }

    public void start() {
        while (!frontier.isEmpty()) {

            // Ambil URL paling depan
            String webpageUrl = frontier.poll();

            // Skip kalau URL sudah di cek
            if (!repositories.add(webpageUrl)) {
                continue;
            }


        }
    }

    public void stop() {

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
        if (scheme == null)
            return rawUrl;

        if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))
            return null;

        scheme = scheme.toLowerCase();

        // host
        String host = url.getHost();
        if (host == null || host.isEmpty())
            return null;

        try {
            host = IDN.toASCII(host).toLowerCase();
        } catch (Exception e) {
            return null;
        }

        // port
        int port = url.getPort();
        if ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)) {
            port = -1;
        }

        // path
        String path = url.getPath();
        if (path == null)
            path = "";

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

    private Map<String, String> extractUrl(Document doc) {
        Map<String, String> results = new HashMap<>();
        for (Element a : doc.select("a[href]")) {
            String absoluteUrl = a.attr("abs:href");
            String cleanedUrl = normalizeUrl(absoluteUrl);
            if (cleanedUrl == null)
                continue;

            String anchorText = a.text().trim();
            results.put(cleanedUrl, anchorText);
        }
        return results;
    }

    private int fetchUrl(String url) {
        return 0;
    }
}
