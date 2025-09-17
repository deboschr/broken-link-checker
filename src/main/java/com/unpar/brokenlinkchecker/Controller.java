package com.unpar.brokenlinkchecker;

import com.unpar.brokenlinkchecker.cores.Crawler;
import com.unpar.brokenlinkchecker.models.WebpageLink;
import com.unpar.brokenlinkchecker.models.BrokenLink;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller {

    @FXML private TextField urlField;
    @FXML private Button checkButton;

    // Tabel untuk WebpageLink
    @FXML private TableView<WebpageLink> webpageTable;
    @FXML private TableColumn<WebpageLink, String> wpUrlColumn;
    @FXML private TableColumn<WebpageLink, Number> wpStatusColumn;
    @FXML private TableColumn<WebpageLink, Number> wpCountColumn;
    @FXML private TableColumn<WebpageLink, String> wpAccessTimeColumn;

    // Tabel untuk BrokenLink
    @FXML private TableView<BrokenLink> brokenTable;
    @FXML private TableColumn<BrokenLink, String> blUrlColumn;
    @FXML private TableColumn<BrokenLink, Number> blStatusColumn;
    @FXML private TableColumn<BrokenLink, String> blAnchorColumn;
    @FXML private TableColumn<BrokenLink, String> blSourceColumn;

    // List data yang dipakai tabel halaman
    private final ObservableList<WebpageLink> webpageData = FXCollections.observableArrayList();
    // List data yang dipakai tabel broken link
    private final ObservableList<BrokenLink> brokenData   = FXCollections.observableArrayList();

    /**
     * Method ini otomatis dijalankan setelah file FXML diload.
     * Tugasnya: inisialisasi tabel supaya tiap kolom tahu harus ngambil data dari field mana.
     * - Untuk tabel WebpageLink: ngikat URL, status code, jumlah link, dan waktu akses.
     * - Untuk tabel BrokenLink: ngikat URL, status code, anchor text, dan halaman sumber.
     * - Terakhir, pasang list data (webpageData, brokenData) ke tabel supaya isinya bisa ditampilkan secara stream dan otomatis berubah (auto update)
     */
    @FXML
    public void initialize() {
        // === Tabel untuk WebpageLink ===
        wpUrlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));          // kolom URL
        wpStatusColumn.setCellValueFactory(new PropertyValueFactory<>("statusCode"));// kolom status
        wpCountColumn.setCellValueFactory(new PropertyValueFactory<>("linkCount"));  // kolom jumlah link
        // kolom access time butuh convert dari Instant ke StringProperty biar bisa ditampilkan
        wpAccessTimeColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getAccessTime().toString())
        );

        // === Tabel untuk BrokenLink ===
        blUrlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));          // kolom URL
        blStatusColumn.setCellValueFactory(new PropertyValueFactory<>("statusCode"));// kolom status
        blAnchorColumn.setCellValueFactory(new PropertyValueFactory<>("anchorText"));// kolom anchor
        blSourceColumn.setCellValueFactory(new PropertyValueFactory<>("webpageUrl"));// kolom halaman sumber

        // Pasang list data ke tabel biar tabel bisa nampilin isinya dan auto update
        webpageTable.setItems(webpageData);
        brokenTable.setItems(brokenData);
    }


    /**
     * Method ini dipanggil waktu user klik tombol "Check".
     * Fungsinya: mulai proses crawling berdasarkan URL yang diinput user.
     * - Ambil seed URL dari TextField.
     * - Kalau kosong, munculin Alert warning.
     * - Kalau ada, bersihin data lama dari tabel, lalu bikin objek Crawler baru.
     * - Jalankan crawl di thread terpisah supaya UI nggak nge-freeze.
     * - Hasil crawling (WebpageLink & BrokenLink) dikirim balik lewat Consumer,
     *   terus dimasukin ke ObservableList pakai Platform.runLater() biar aman update UI.
     */
    @FXML
    protected void onCheckClick() {
        // Ambil URL input dari TextField
        String seedUrl = urlField.getText().trim();

        // Validasi: kalau kosong, kasih alert dan stop
        if (seedUrl.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Masukkan Seed URL terlebih dahulu!");
            alert.showAndWait();
            return;
        }

        // Reset data tabel biar hasil baru nggak campur sama yang lama
        webpageData.clear();
        brokenData.clear();

        // Bikin objek crawler dengan seed URL
        Crawler crawler = new Crawler(seedUrl);

        // Jalankan crawling di thread terpisah
        new Thread(() -> {
            crawler.crawl(
                    // tiap kali nemu WebpageLink, masukin ke list halaman
                    wp -> Platform.runLater(() -> webpageData.add(wp)),
                    // tiap kali nemu BrokenLink, masukin ke list brokenlink
                    bl -> Platform.runLater(() -> brokenData.add(bl))
            );
        }).start();
    }
}
