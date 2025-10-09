package com.unpar.brokenlinkchecker;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

/**
 * Controller utama untuk mengelola interaksi UI dan logika crawling.
 */
public class Controller {

   // ============================================================
   // FXML ELEMENTS
   // ============================================================

   @FXML
   private TextField seedUrlField;

   @FXML
   private Label checkingStatusLabel;
   @FXML
   private Label totalLinksLabel;
   @FXML
   private Label webpageLinksLabel;
   @FXML
   private Label brokenLinksLabel;

   @FXML
   private TableView<BrokenLink> resultsTable;
   @FXML
   private TableColumn<BrokenLink, String> statusColumn;
   @FXML
   private TableColumn<BrokenLink, String> urlColumn;

   // ============================================================
   // DATA & STATE
   // ============================================================

   private final ObservableList<BrokenLink> results = FXCollections.observableArrayList();
   private final SummaryCard summaryCard = new SummaryCard();
   private Task<Void> crawlTask;

   // ============================================================
   // INITIALIZATION
   // ============================================================

   @FXML
   public void initialize() {
      bindSummaryLabels();
      setupResultsTable();
   }

   /**
    * Binding antara SummaryCard dan label di UI
    */
   private void bindSummaryLabels() {
      // Checking Status
      summaryCard.checkingStatusProperty().addListener((obs, oldVal, newVal) -> {
         checkingStatusLabel.setText(newVal.getText());
      });

      // Total Links
      totalLinksLabel.textProperty().bind(summaryCard.totalLinksProperty().asString());

      // Webpage Links
      webpageLinksLabel.textProperty().bind(summaryCard.webpagesProperty().asString());

      // Broken Links
      brokenLinksLabel.textProperty().bind(summaryCard.brokenLinksProperty().asString());
   }

   /**
    * Inisialisasi kolom tabel dan perilakunya.
    */
   private void setupResultsTable() {
      resultsTable.setItems(results);

      // Kolom Status → langsung pakai property dari BrokenLink
      statusColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getStatus()));

      // Kolom URL → langsung pakai property dari BrokenLink
      urlColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getUrl()));

      // Klik dua kali → buka URL di browser
      resultsTable.setRowFactory(tv -> {
         TableRow<BrokenLink> row = new TableRow<>();
         row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !row.isEmpty()) {
               BrokenLink link = row.getItem();
               openUrl(link.getUrl());
            }
         });
         return row;
      });
   }

   // ============================================================
   // EVENT HANDLERS
   // ============================================================

   /**
    * Tombol Start diklik → mulai proses crawling.
    */
   @FXML
   private void onStartClick() {
      String rawUrl = seedUrlField.getText().trim();

      if (rawUrl.isEmpty()) {
         showAlert("Please enter a valid seed URL.");
         return;
      }

      // Normalisasi URL (gunakan utilitas dari Crawler)
      String normalizedUrl = Crawler.normalizeUrl(rawUrl);
      if (normalizedUrl == null) {
         showAlert("Invalid URL. Make sure it includes a host and uses http or https.");
         return;
      }

      // Reset summary
      summaryCard.setCheckingStatus(CheckingStatus.RUNNING);
      summaryCard.setTotalLinks(0);
      summaryCard.setWebpages(0);
      summaryCard.setBrokenLinks(0);
      results.clear();

      // Jalankan proses crawling di background thread
      crawlTask = new Task<>() {
         @Override
         protected Void call() {
            try {
               // =====================================================
               // >>> SEMENTARA: SIMULASI proses crawling dummy
               // =====================================================
               Platform.runLater(() -> summaryCard.setCheckingStatus(CheckingStatus.RUNNING));

               // Simulasi total 3 link rusak
               for (int i = 1; i <= 3; i++) {
                  Thread.sleep(1000); // delay untuk simulasi
                  String url = "https://example.com/broken-" + i;
                  int code = (i == 3) ? 500 : 404;

                  BrokenLink brokenLink = new BrokenLink(url, code, Instant.now());
                  brokenLink.addWebpage("https://example.com/source-" + i, "anchor text " + i);

                  Platform.runLater(() -> {
                     results.add(brokenLink);
                     summaryCard.setBrokenLinks(summaryCard.getBrokenLinks() + 1);
                     summaryCard.setTotalLinks(summaryCard.getTotalLinks() + 1);
                     summaryCard.setWebpages(summaryCard.getWebpages() + 1);
                  });
               }

               Platform.runLater(() -> summaryCard.setCheckingStatus(CheckingStatus.COMPLETED));

            } catch (InterruptedException e) {
               Platform.runLater(() -> summaryCard.setCheckingStatus(CheckingStatus.STOPPED));
            }

            return null;
         }
      };

      Thread thread = new Thread(crawlTask);
      thread.setDaemon(true);
      thread.start();
   }

   /**
    * Tombol Stop diklik → hentikan proses crawling.
    */
   @FXML
   private void onStopClick() {
      if (crawlTask != null && crawlTask.isRunning()) {
         crawlTask.cancel();
         summaryCard.setCheckingStatus(CheckingStatus.STOPPED);
      } else {
         showAlert("No active crawling process.");
      }
   }

   /**
    * Tombol Export diklik → ekspor hasil.
    */
   @FXML
   private void onExportClick() {
      showAlert("Export feature not implemented yet.");
   }

   // ============================================================
   // UTILITIES
   // ============================================================

   /**
    * Buka URL di browser default sistem.
    */
   private void openUrl(String url) {
      try {
         if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(url));
         }
      } catch (IOException | URISyntaxException e) {
         showAlert("Failed to open URL: " + url);
      }
   }

   /**
    * Tampilkan pesan alert sederhana.
    */
   private void showAlert(String message) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
   }
}
