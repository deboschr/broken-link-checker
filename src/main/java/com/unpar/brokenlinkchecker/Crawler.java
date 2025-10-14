package com.unpar.brokenlinkchecker;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.unpar.brokenlinkchecker.v1.CrawlStatus;

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
            .followRedirects(HttpClient.Redirect.NORMAL) // otomatis follow redirect (301,302,dll)
            .connectTimeout(Duration.ofMillis(TIMEOUT)) // set timeout koneksi
            .version(HttpClient.Version.HTTP_1_1) // set versi HTTP ke 1.1
            .build();

    public Crawler(String seedUrl) {
        this.rootHost = URI.create(seedUrl).getHost().toLowerCase();
        this.repositories = new HashSet<>();
        this.frontier = new Frontier();

        frontier.enqueue(seedUrl);
    }

    // public void startCrawling(
    // Consumer<BrokenLink> streamBrokenLink,
    // Consumer<Integer> streamTotalLinks,
    // Consumer<CrawlStatus> streamStatus) {

    // streamStatus.accept(CrawlStatus.RUNNING);

    // while (!frontier.isEmpty()) {
    // String webpageLink = frontier.dequeue();

    // // total links update hanya kalau ada yang baru
    // if (repositories.add(webpageLink)) {
    // streamTotalLinks.accept(repositories.size());
    // } else {
    // continue;
    // }

    // Document doc;
    // int wlStatusCode = 0;

    // try {
    // Connection.Response res = Jsoup
    // .connect(webpageLink)
    // .userAgent(USER_AGENT)
    // .timeout(TIMEOUT)
    // .followRedirects(true)
    // .ignoreHttpErrors(true)
    // .execute();

    // wlStatusCode = res.statusCode();

    // if (wlStatusCode >= 400 || wlStatusCode == 0) {
    // markBroken(webpageLink, wlStatusCode, webpageLink, "(self)",
    // streamBrokenLink);
    // continue;
    // }

    // doc = res.parse();
    // if (doc.selectFirst("html") == null) {
    // continue;
    // }

    // } catch (Exception e) {
    // markBroken(webpageLink, 0, webpageLink, "(self)", streamBrokenLink);
    // continue;
    // }

    // // ekstrak link di halaman
    // Map<String, String> linksOnWebpage = extractLinks(doc);

    // for (Map.Entry<String, String> entry : linksOnWebpage.entrySet()) {
    // String url = entry.getKey();
    // String anchorText = entry.getValue();

    // if (isPotentialWebpage(url)) {
    // if (!repositories.contains(url)) {
    // frontier.enqueue(url);
    // }
    // } else {
    // if (!repositories.add(url)) {
    // // link sudah ada → kalau broken, update source
    // BrokenLink bl = brokenLinks.get(url);
    // if (bl != null) {
    // bl.addWebpage(webpageLink, anchorText);
    // streamBrokenLink.accept(bl);
    // }
    // continue;
    // }

    // streamTotalLinks.accept(repositories.size());

    // int blStatusCode = fetchUrl(url);

    // if (blStatusCode >= 400 || blStatusCode == 0) {
    // markBroken(url, blStatusCode, webpageLink, anchorText, streamBrokenLink);
    // }
    // }
    // }
    // }

    // streamStatus.accept(CrawlStatus.COMPLETED);
    // }

    public static String normalizeUrl(String rawUrl) {
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

    private Map<String, String> extractLinks(Document doc) {
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

}
