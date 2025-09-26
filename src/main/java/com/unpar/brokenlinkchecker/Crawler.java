// package com.unpar.brokenlinkchecker;

// import com.unpar.brokenlinkchecker.BrokenLink;
// import com.unpar.brokenlinkchecker.WebpageLink;
// import org.jsoup.Connection;
// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;

// import java.net.*;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.time.Duration;
// import java.time.Instant;
// import java.util.*;
// import java.util.function.Consumer;

// public class Crawler {

//     private final String rootHost;      // host dari seed URL
//     private final Frontier frontier;    // antrian URL yang akan dicrawl
//     private final Set<String> repositories; // kumpulan URL supaya nggak ada yang dicek 2x

//     private static final String USER_AGENT = "BrokenLinkChecker/1.0 (+https://github.com/jakeschr/BrokenLinkChecker; contact: 6182001060@student.unpar.ac.id)";
//     private static final int TIMEOUT = 10000;

//     // HttpClient bawaan Java buat ngecek link non-webpage (pakai HEAD/GET)
//     private static final HttpClient httpClient = HttpClient
//             .newBuilder()
//             .followRedirects(HttpClient.Redirect.NORMAL)    // otomatis follow redirect (301,302,dst)
//             .connectTimeout(Duration.ofMillis(TIMEOUT))     // timeout koneksi
//             .version(HttpClient.Version.HTTP_1_1)           // pakai HTTP/1.1
//             .build();

//     /**
//      * Constructor: bikin crawler baru dari seed URL.
//      * - Ambil host root dari seed.
//      * - Siapin set repository biar URL unik.
//      * - Masukin seed URL ke frontier supaya jadi titik awal crawl.
//      */
//     public Crawler(String seedUrl) {
//         this.rootHost = URI.create(seedUrl).getHost().toLowerCase();
//         this.repositories = new HashSet<>();
//         this.frontier = new Frontier();

//         frontier.enqueue(seedUrl);
//     }

//     /**
//      * Fungsi utama buat ngecrawl.
//      * - Ambil URL dari frontier satu per satu.
//      * - Kalau URL baru maka request ke server terus ambil status + HTML.
//      * - Simpan hasil sebagai WebpageLink atau BrokenLink.
//      * - Ekstrak semua link dari halaman, lalu tentukan apakah link itu halaman lagi (lanjut crawl) atau link resource (cukup dicek status).
//      * - Setiap kali dapat hasil, dikirim lewat Consumer ke UI.
//      */
//     public void startCrawling(Consumer<WebpageLink> streamWebpageLink,
//                               Consumer<BrokenLink> streamBrokenLink) {

//         while (!frontier.isEmpty()) {
//             String webpageLink = frontier.dequeue();

//             // skip kalau sudah pernah dicek
//             if (!repositories.add(webpageLink)) {
//                 continue;
//             }

//             Document doc;         // hasil HTML
//             int wlStatusCode = 0; // status code halaman

//             try {
//                 // request pakai Jsoup
//                 Connection.Response res = Jsoup
//                         .connect(webpageLink)
//                         .userAgent(USER_AGENT)
//                         .timeout(TIMEOUT)
//                         .followRedirects(true)
//                         .ignoreHttpErrors(true) // walaupun 4xx/5xx tetap kasih response
//                         .execute();

//                 wlStatusCode = res.statusCode();

//                 // kalau error (>=400 atau 0), langsung dianggap broken
//                 if (wlStatusCode >= 400 || wlStatusCode == 0) {
//                     streamBrokenLink.accept(new BrokenLink(webpageLink, wlStatusCode, "", ""));
//                     continue;
//                 }

//                 // parse HTML
//                 doc = res.parse();

//                 // kalau gak ada elemen <html>, skip
//                 if (doc.selectFirst("html") == null) {
//                     continue;
//                 }

//             } catch (Exception e) {
//                 // kalau network error, timeout, SSL error, dll
//                 streamBrokenLink.accept(new BrokenLink(webpageLink, 0, e.getClass().getSimpleName(), ""));
//                 continue;
//             }

//             // ekstrak semua link di halaman
//             List<BrokenLink> linksOnWebpage = extractLinks(doc);

//             // kirim data halaman yang berhasil dicrawl
//             streamWebpageLink.accept(new WebpageLink(webpageLink, wlStatusCode, linksOnWebpage.size(), Instant.now()));

//             // proses tiap link yang ditemukan
//             for (BrokenLink bl : linksOnWebpage) {

//                 // kalau link masih satu host (same domain) maka anggap ini berpotensi jadi halaman
//                 if (isPotentialWebpage(bl.getUrl())) {
//                     if (!repositories.contains(bl.getUrl())) {
//                         frontier.enqueue(bl.getUrl());
//                     }
//                 }
//                 // kalau bukan halaman (misal gambar, css, js, dll.)
//                 else {
//                     if (!repositories.add(bl.getUrl())) {
//                         continue;
//                     }

//                     // cek status link
//                     int blStatusCode = fetchUrl(bl.getUrl());

//                     // kalau rusak, update info & kirim ke stream
//                     if (blStatusCode >= 400 || blStatusCode == 0) {
//                         bl.setStatusCode(blStatusCode);
//                         bl.setWebpageUrl(webpageLink);
//                         streamBrokenLink.accept(bl);
//                     }
//                 }
//             }
//         }
//     }

//     /**
//      * Ekstrak semua <a href> dari dokumen HTML.
//      * - Ambil absolute URL.
//      * - Bersihin URL (canonicalization).
//      * - Simpan sebagai BrokenLink dengan status 0 (belum dicek).
//      */
//     private List<BrokenLink> extractLinks(Document doc) {
//         List<BrokenLink> results = new ArrayList<>();

//         for (Element a : doc.select("a[href]")) {
//             String absoluteUrl = a.attr("abs:href");              // ambil absolute URL
//             String cleanedUrl = canonical(absoluteUrl); // normalisasi

//             if (cleanedUrl == null) continue;

//             String anchorText = a.text().trim();                  // ambil teks anchor
//             results.add(new BrokenLink(cleanedUrl, 0, anchorText, ""));
//         }
//         return results;
//     }

//     /**
//      * Cek apakah URL potensial jadi halaman (bisa dicrawl lagi).
//      * Syarat:
//      * - host sama dengan seed
//      * - bukan file resource (jpg, png, css, js, pdf, dll.)
//      */
//     private boolean isPotentialWebpage(String url) {
//         try {
//             URI uri = URI.create(url);
//             String host = uri.getHost();
//             if (host == null) return false;

//             boolean isSameHost = host.equalsIgnoreCase(rootHost);

//             String path = uri.getPath().toLowerCase();
//             boolean isFileResource =
//                     path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")
//                             || path.endsWith(".gif") || path.endsWith(".webp") || path.endsWith(".svg")
//                             || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".pdf")
//                             || path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".7z")
//                             || path.endsWith(".mjs") || path.endsWith(".map") || path.endsWith(".json");

//             return !isFileResource && isSameHost;
//         } catch (Exception e) {
//             return false;
//         }
//     }

//     /**
//      * Membersihkan URL biar konsisten.
//      * - scheme ke lowercase, hanya http/https
//      * - host ke lowercase (support IDN)
//      * - hapus port default (80, 443)
//      * - hapus fragment (#...) karena gak dikirim ke server
//      */
//     private static String canonical(String rawUrl) {
//         if (rawUrl == null || rawUrl.trim().isEmpty()) return null;

//         URI url;
//         try {
//             url = URI.create(rawUrl.trim());
//         } catch (IllegalArgumentException e) {
//             return rawUrl;
//         }

//         // scheme
//         String scheme = url.getScheme();
//         if (scheme == null) return null;
//         if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) return null;
//         scheme = scheme.toLowerCase();

//         // host
//         String host = url.getHost();
//         if (host == null || host.isEmpty()) return null;
//         try {
//             host = IDN.toASCII(host).toLowerCase();
//         } catch (Exception e) {
//             return null;
//         }

//         // port
//         int port = url.getPort();
//         if ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)) {
//             port = -1;
//         }

//         // path
//         String path = url.getPath();
//         if (path == null) path = "";

//         // query
//         String query = url.getRawQuery();

//         try {
//             URI cleanedUrl = new URI(scheme, null, host, port, path, query, null);
//             return cleanedUrl.toASCIIString();
//         } catch (URISyntaxException e) {
//             return null;
//         }
//     }

//     /**
//      * Cek status code sebuah URL non-webpage.
//      * - Pertama coba pakai HEAD request (lebih ringan).
//      * - Kalau gagal (status 405, 501, 999), fallback pakai GET.
//      * - Kalau error (exception), return 0.
//      */
//     private int fetchUrl(String url) {
//         List<Integer> fallbackStatusCode = Arrays.asList(405, 501, 999);

//         try {
//             HttpRequest headReq = HttpRequest
//                     .newBuilder(URI.create(url))
//                     .method("HEAD", HttpRequest.BodyPublishers.noBody())
//                     .header("User-Agent", USER_AGENT)
//                     .timeout(Duration.ofMillis(TIMEOUT))
//                     .build();

//             HttpResponse<Void> headRes = httpClient.send(headReq, HttpResponse.BodyHandlers.discarding());
//             int statusCode = headRes.statusCode();

//             // fallback kalau HEAD nggak didukung
//             if (fallbackStatusCode.contains(statusCode)) {
//                 HttpRequest getReq = HttpRequest
//                         .newBuilder(URI.create(url))
//                         .method("GET", HttpRequest.BodyPublishers.noBody())
//                         .header("User-Agent", USER_AGENT)
//                         .timeout(Duration.ofMillis(TIMEOUT))
//                         .build();

//                 HttpResponse<Void> getRes = httpClient.send(getReq, HttpResponse.BodyHandlers.discarding());
//                 statusCode = getRes.statusCode();
//             }

//             return statusCode;
//         } catch (Exception e) {
//             return 0;
//         }
//     }
// }
