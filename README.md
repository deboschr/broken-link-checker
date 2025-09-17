# BrokenLink Checker

Aplikasi desktop berbasis **JavaFX** untuk mendeteksi dan melaporkan tautan rusak (*broken links*) pada sebuah situs web. Project ini dikembangkan sebagai bagian dari Tugas Akhir di Universitas Katolik Parahyangan.

## Deskripsi
BrokenLink Checker memungkinkan pengguna memasukkan sebuah URL awal (*seed URL*), kemudian aplikasi akan melakukan **crawling** terhadap seluruh halaman yang berada dalam host yang sama, serta mengumpulkan:

- **Halaman Web (Webpages)** => semua halaman valid yang berhasil dicrawl.
- **Tautan Rusak (Broken Links)** => tautan yang mengembalikan kode error (HTTP 4xx/5xx) atau gagal diakses.

Hasil crawling ditampilkan dalam dua tabel:
- **Tabel Webpages** => menampilkan URL, status code, jumlah tautan yang ditemukan, dan waktu akses.
- **Tabel Broken Links** => menampilkan URL, status code, teks anchor, serta halaman sumber.

## Fitur
- Crawling situs web mulai dari seed URL.
- Algoritma crawling **BFS** (Breadth-First Search) melalui komponen *Frontier*.
- Normalisasi URL (*canonicalization*) untuk menghindari duplikasi.
- Pemeriksaan status tautan menggunakan:
    - **Jsoup** untuk halaman web.
    - **Java HttpClient** untuk tautan non-halaman (gambar, CSS, JS, dsb.).
- Antarmuka grafis berbasis JavaFX dengan `TableView` dan dukungan CSS.

## Teknologi
- **Java 23**
- **JavaFX 17.0.6**
- **Gradle 8.10**
- **Jsoup 1.21.2**
- **Java HttpClient (java.net.http)**

## Struktur Proyek
```
broken-link-checker/
├── build.gradle                           # Konfigurasi Gradle
├── settings.gradle                        # Konfigurasi nama proyek
├── src/
│   └── main/
│       ├── java/com/unpar/brokenlinkchecker/
│       │   ├── App.java                   # Entry point utama aplikasi
│       │   ├── Controller.java            # Controller untuk JavaFX
│       │   ├── cores/                     # Logika crawling
│       │   │   ├── Crawler.java
│       │   │   └── Frontier.java
│       │   ├── models/                    # Model data untuk tabel UI
│       │   │   ├── WebpageLink.java
│       │   │   ├── BrokenLink.java
│       │   │   └── Link.java
│       │   └── utils/                     # Utility tambahan
│       │       └── HttpStatus.java
│       └── resources/com/unpar/brokenlinkchecker/
│           ├── view.fxml                   # Desain antarmuka aplikasi
│           └── style.css                   # Styling tampilan
```

## Cara Menjalankan

### Clone repository
```bash
git clone https://github.com/deboschr/broken-link-checker.git
cd broken-link-checker
```

### Jalankan aplikasi
Linux / MacOS:
```bash
./gradlew run
```

Windows (PowerShell):
```powershell
.\gradlew run
```