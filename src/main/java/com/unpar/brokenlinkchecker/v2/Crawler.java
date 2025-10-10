package com.unpar.brokenlinkchecker.v2;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.unpar.brokenlinkchecker.v1.BrokenLink;
import com.unpar.brokenlinkchecker.v1.CrawlStatus;
import com.unpar.brokenlinkchecker.v1.Frontier;

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
    private final Map<String, BrokenLink> brokenLinks;

    private static final String USER_AGENT = "BrokenLinkChecker/1.0 (+https://github.com/jakeschr/broken-link-checker; contact: 6182001060@student.unpar.ac.id)";
    private static final int TIMEOUT = 10000;
    private static final HttpClient httpClient = HttpClient
            .newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofMillis(TIMEOUT))
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    public Crawler(String seedUrl) {
        this.rootHost = URI.create(seedUrl).getHost().toLowerCase();
        this.repositories = new HashSet<>();
        this.frontier = new Frontier();
        this.brokenLinks = new HashMap<>();

        frontier.enqueue(seedUrl);
    }

    public void startCrawling(
            Consumer<BrokenLink> streamBrokenLink,
            Consumer<Integer> streamTotalLinks,
            Consumer<CrawlStatus> streamStatus) {

        streamStatus.accept(CrawlStatus.RUNNING);

        while (!frontier.isEmpty()) {
            String webpageLink = frontier.dequeue();

            // total links update hanya kalau ada yang baru
            if (repositories.add(webpageLink)) {
                streamTotalLinks.accept(repositories.size());
            } else {
                continue;
            }

            Document doc;
            int wlStatusCode = 0;

            try {
                Connection.Response res = Jsoup
                        .connect(webpageLink)
                        .userAgent(USER_AGENT)
                        .timeout(TIMEOUT)
                        .followRedirects(true)
                        .ignoreHttpErrors(true)
                        .execute();

                wlStatusCode = res.statusCode();

                if (wlStatusCode >= 400 || wlStatusCode == 0) {
                    markBroken(webpageLink, wlStatusCode, webpageLink, "(self)", streamBrokenLink);
                    continue;
                }

                doc = res.parse();
                if (doc.selectFirst("html") == null) {
                    continue;
                }

            } catch (Exception e) {
                markBroken(webpageLink, 0, webpageLink, "(self)", streamBrokenLink);
                continue;
            }

            // ekstrak link di halaman
            Map<String, String> linksOnWebpage = extractLinks(doc);

            for (Map.Entry<String, String> entry : linksOnWebpage.entrySet()) {
                String url = entry.getKey();
                String anchorText = entry.getValue();

                if (isPotentialWebpage(url)) {
                    if (!repositories.contains(url)) {
                        frontier.enqueue(url);
                    }
                } else {
                    if (!repositories.add(url)) {
                        // link sudah ada → kalau broken, update source
                        BrokenLink bl = brokenLinks.get(url);
                        if (bl != null) {
                            bl.addWebpage(webpageLink, anchorText);
                            streamBrokenLink.accept(bl);
                        }
                        continue;
                    }

                    streamTotalLinks.accept(repositories.size());

                    int blStatusCode = fetchUrl(url);

                    if (blStatusCode >= 400 || blStatusCode == 0) {
                        markBroken(url, blStatusCode, webpageLink, anchorText, streamBrokenLink);
                    }
                }
            }
        }

        streamStatus.accept(CrawlStatus.COMPLETED);
    }

    private void markBroken(String url, int statusCode, String sourcePage, String anchorText,
            Consumer<BrokenLink> streamBrokenLink) {
        BrokenLink bl = brokenLinks.computeIfAbsent(url,
                key -> new BrokenLink(key, statusCode, Instant.now()));
        bl.addWebpage(sourcePage, anchorText);
        streamBrokenLink.accept(bl);
    }

    private static String canonization(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty())
            return null;

        URI url;
        try {
            url = URI.create(rawUrl.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }

        String scheme = url.getScheme();
        if (scheme == null)
            return null;
        if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))
            return null;
        scheme = scheme.toLowerCase();

        String host = url.getHost();
        if (host == null || host.isEmpty())
            return null;
        try {
            host = IDN.toASCII(host).toLowerCase();
        } catch (Exception e) {
            return null;
        }

        int port = url.getPort();
        if ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)) {
            port = -1;
        }

        String path = url.getPath();
        if (path == null)
            path = "";

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
            if (cleanedUrl == null)
                continue;

            String anchorText = a.text().trim();
            results.put(cleanedUrl, anchorText);
        }
        return results;
    }

    private boolean isPotentialWebpage(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (host == null)
                return false;

            boolean isSameHost = host.equalsIgnoreCase(rootHost);

            String path = uri.getPath().toLowerCase();
            boolean isFileResource = path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")
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
            HttpRequest headReq = HttpRequest.newBuilder(URI.create(url))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .header("User-Agent", USER_AGENT)
                    .timeout(Duration.ofMillis(TIMEOUT))
                    .build();

            HttpResponse<Void> headRes = httpClient.send(headReq, HttpResponse.BodyHandlers.discarding());
            int statusCode = headRes.statusCode();

            if (fallbackStatusCode.contains(statusCode)) {
                HttpRequest getReq = HttpRequest.newBuilder(URI.create(url))
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .header("User-Agent", USER_AGENT)
                        .timeout(Duration.ofMillis(TIMEOUT))
                        .build();

                HttpResponse<Void> getRes = httpClient.send(getReq, HttpResponse.BodyHandlers.discarding());
                statusCode = getRes.statusCode();
            }
            return statusCode;
        } catch (Exception e) {
            return 0;
        }
    }
}
