package com.unpar.brokenlinkchecker.temp;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public class Crawler {
    private final String rootHost;
    private final Frontier frontier;
    private final Set<String> repositories;
    
    private static final String USER_AGENT = "BrokenLinkChecker/1.0 (+https://github.com/jakeschr/broken-link-checker; contact: 6182001060@student.unpar.ac.id)";
    private static final int TIMEOUT = 10000;
    private static final HttpClient HTTP_CLIENT = HttpClient
            .newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)    // otomatis follow redirect (301,302,dst)
            .connectTimeout(Duration.ofMillis(TIMEOUT))     // timeout koneksi
            .version(HttpClient.Version.HTTP_1_1)           // pakai HTTP/1.1
            .build();

    public Crawler(String seedUrl) {
        this.rootHost = URI.create(seedUrl).getHost().toLowerCase();
        this.repositories = new HashSet<>();
        this.frontier = new Frontier();

        frontier.enqueue(seedUrl);
    } 

    public void startCrawling(Consumer<BrokenLink> streamBrokenLink) {

        while (!frontier.isEmpty()) {
            String webpageLink = frontier.dequeue();

            // skip kalau sudah pernah dicek
            if (!repositories.add(webpageLink)) {
                continue;
            }

            Document doc;         // hasil HTML
            int wlStatusCode = 0; // status code halaman

            try {
                // request pakai Jsoup
                Connection.Response res = Jsoup
                        .connect(webpageLink)
                        .userAgent(USER_AGENT)
                        .timeout(TIMEOUT)
                        .followRedirects(true)
                        .ignoreHttpErrors(true) // walaupun 4xx/5xx tetap kasih response
                        .execute();

                wlStatusCode = res.statusCode();

                // kalau error (>=400 atau 0), langsung dianggap broken
                if (wlStatusCode >= 400 || wlStatusCode == 0) {
                    streamBrokenLink.accept(new BrokenLink(webpageLink, wlStatusCode, Instant.now()));
                    continue;
                }

                // parse HTML
                doc = res.parse();

                // kalau gak ada elemen <html>, skip
                if (doc.selectFirst("html") == null) {
                    continue;
                }

            } catch (Exception e) {
                // kalau network error, timeout, SSL error, dll
                streamBrokenLink.accept(new BrokenLink(webpageLink, 0, Instant.now()));
                continue;
            }

            // ekstrak semua link di halaman
            Map<String, String> linksOnWebpage = extractLinks(doc);

            for (Map.Entry<String, String> entry : linksOnWebpage.entrySet()) {
                String url = entry.getKey();
                String anchorText = entry.getValue();

                // kalau link masih satu host (same domain) maka anggap ini berpotensi jadi halaman
                if (isPotentialWebpage(url)) {
                    if (!repositories.contains(url)) {
                        frontier.enqueue(url);
                    }
                }
                // kalau bukan halaman (misal gambar, css, js, dll.)
                else {
                    if (!repositories.add(url)) {
                        continue;
                    }

                    // cek status link
                    int blStatusCode = fetchUrl(url);

                    // kalau rusak, update info & kirim ke stream
                    if (blStatusCode >= 400 || blStatusCode == 0) {
                        BrokenLink bl = new BrokenLink(url, blStatusCode, Instant.now());
                        bl.addWebpage(webpageLink, anchorText);
                        streamBrokenLink.accept(bl);
                    }
                }

            }
        }
    }
    
    private String canonization(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) return null;

        URI url;
        try {
            url = URI.create(rawUrl.trim());
        } catch (IllegalArgumentException e) {
            return rawUrl;
        }

        // scheme
        String scheme = url.getScheme();
        if (scheme == null) return rawUrl;
        if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) return null;
        scheme = scheme.toLowerCase();

        // host
        String host = url.getHost();
        if (host == null || host.isEmpty()) return rawUrl;
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
        if (path == null) path = "";

        // query
        String query = url.getRawQuery();

        try {
            URI cleanedUrl = new URI(scheme, null, host, port, path, query, null);
            return cleanedUrl.toASCIIString();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private Map<String, String> extractLinks(Document doc) {
        Map<String, String> results = new HashMap<>();

        for (Element a : doc.select("a[href]")) {
            String absoluteUrl = a.attr("abs:href");
            String cleanedUrl = canonization(absoluteUrl);

            if (cleanedUrl == null) continue;

            String anchorText = a.text().trim();
            results.put(cleanedUrl, anchorText); // URL jadi key, anchor jadi value
        }

        return results;
    }

    private boolean isPotentialWebpage(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (host == null) return false;

            boolean isSameHost = host.equalsIgnoreCase(rootHost);

            String path = uri.getPath().toLowerCase();
            boolean isFileResource =
                    path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")
                            || path.endsWith(".gif") || path.endsWith(".webp") || path.endsWith(".svg")
                            || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".pdf")
                            || path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".7z")
                            || path.endsWith(".mjs") || path.endsWith(".map") || path.endsWith(".json");

            return !isFileResource && isSameHost;
        } catch (Exception e) {
            return false;
        }
    }

    private int fetchUrl(String url) {
        List<Integer> fallbackStatusCode = Arrays.asList(405, 501, 999);

        try {
            HttpRequest headReq = HttpRequest
                    .newBuilder(URI.create(url))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .header("User-Agent", USER_AGENT)
                    .timeout(Duration.ofMillis(TIMEOUT))
                    .build();

            HttpResponse<Void> headRes = HTTP_CLIENT.send(headReq, HttpResponse.BodyHandlers.discarding());
            int statusCode = headRes.statusCode();

            // fallback kalau HEAD nggak didukung
            if (fallbackStatusCode.contains(statusCode)) {
                HttpRequest getReq = HttpRequest
                        .newBuilder(URI.create(url))
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .header("User-Agent", USER_AGENT)
                        .timeout(Duration.ofMillis(TIMEOUT))
                        .build();

                HttpResponse<Void> getRes = HTTP_CLIENT.send(getReq, HttpResponse.BodyHandlers.discarding());
                statusCode = getRes.statusCode();
            }

            return statusCode;
        } catch (Exception e) {
            return 0;
        }
    }
}
    