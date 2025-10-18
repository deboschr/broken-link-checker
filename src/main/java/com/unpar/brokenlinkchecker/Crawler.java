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
import java.util.function.Consumer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.unpar.brokenlinkchecker.model.Link;
import com.unpar.brokenlinkchecker.model.FetchResult;

import javafx.application.Platform;

public class Crawler {
    private static final String USER_AGENT = "BrokenLinkChecker/1.0 (+https://github.com/jakeschr/broken-link-checker; contact: 6182001060@student.unpar.ac.id)";
    private static final int TIMEOUT = 10000;

    // callback ke controller buat kirim hasil link (streaming real-time)
    private final Consumer<Link> brokenLinkConsumer;

    // internal state
    private String rootHost;
    private final Queue<Link> frontier = new ArrayDeque<>();
    private final Set<String> repositories = new HashSet<>();

    // untuk kontrol proses
    private volatile boolean running = false;

    public Crawler(Consumer<Link> brokenLinkConsumer) {
        this.brokenLinkConsumer = brokenLinkConsumer;
    }

    public void start(String seedUrl) {
        running = true;

        this.rootHost = getHostUrl(seedUrl);
        repositories.clear();
        frontier.clear();

        // masukkan seed ke frontier
        frontier.offer(new Link(seedUrl, null, 0, null, null, Instant.now()));

        while (running && !frontier.isEmpty()) {

            Link link = frontier.poll();

            // Skip kalau sudah pernah dikunjungi
            if (!repositories.add(link.getUrl())) {
                continue;
            }

            FetchResult result = fetchUrl(link.getUrl(), true);

            Link webpageLink = result.link();
            Document doc = result.document();

            // Skip dan kirim broken link kalau error
            if (webpageLink.getStatusCode() >= 400 || webpageLink.getError() != null) {
                sendBrokenLink(webpageLink);
                continue;
            }

            // Skip kalau beda host
            String finalUrlHost = getHostUrl(webpageLink.getFinalUrl());
            if (finalUrlHost == null || !finalUrlHost.equals(rootHost)) {
                continue;
            }

            // Skip kalau dokumen kosong atau bukan HTML
            if (doc == null || doc.selectFirst("html") == null) {
                continue;
            }

            Map<String, String> linksOnWebpage = extractUrl(doc);

            // Hapus koneksi WebpageLink dengan parentnya
            webpageLink.clearConnection();

            for (Map.Entry<String, String> entry : linksOnWebpage.entrySet()) {
                String entryUrl = entry.getKey();
                String entryAnchorText = entry.getValue();

                String entryHost = getHostUrl(entryUrl);

                if (entryHost != null && entryHost.equals(rootHost)) {
                    Link entryLink = new Link(entryUrl, null, 0, null, null, Instant.now());

                    entryLink.setConnection(webpageLink, entryAnchorText);

                    frontier.offer(entryLink);
                } else {
                    if (!repositories.add(entryUrl)) {
                        continue;
                    }

                    FetchResult entryRes = fetchUrl(entryUrl, false);

                    Link entryLink = entryRes.link();

                    if (entryLink.getStatusCode() >= 400 || entryLink.getError() != null) {
                        entryLink.setConnection(entryLink, entryAnchorText);
                        sendBrokenLink(entryLink);
                    }

                }

            }

        }

    }

    public void stop() {
        running = false;
    }

    /**
     * kirim hasil link ke controller lewat JavaFX thread
     * 
     * @param link
     */
    private void sendBrokenLink(Link link) {
        if (brokenLinkConsumer != null) {
            Platform.runLater(() -> brokenLinkConsumer.accept(link));
        }
    }

    private FetchResult fetchUrl(String url, Boolean isParseDoc) {
        try {
            Connection.Response res = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .execute();

            Link link = new Link(url, res.url().toString(), res.statusCode(), res.contentType(), null, Instant.now());

            Document doc = null;

            if (isParseDoc && res.statusCode() == 200) {
                doc = res.parse();
            }

            return new FetchResult(link, doc);

        } catch (IOException e) {
            // Gagal koneksi / timeout / DNS / SSL, dll.

            Link link = new Link(url, null, 0, null, e.getClass().getSimpleName(), Instant.now());

            return new FetchResult(link, null);
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

            if (!repositories.contains(cleanedUrl)) {
                results.put(cleanedUrl, anchorText);
            }
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