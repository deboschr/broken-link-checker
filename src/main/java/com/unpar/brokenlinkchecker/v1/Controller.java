package com.unpar.brokenlinkchecker.v1;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.net.URI;
import java.time.Instant;

public class Controller {

    // Input seed URL
    @FXML
    private TextField seedUrlField;

    // Stats
    @FXML
    private Label totalLinksLabel;
    @FXML
    private Label brokenLinksLabel;
    @FXML
    private Label webpagesLabel;
    @FXML
    private Label progressLabel;

    // Results table
    @FXML
    private TableView<BrokenLink> resultsTable;
    @FXML
    private TableColumn<BrokenLink, String> statusColumn;
    @FXML
    private TableColumn<BrokenLink, String> urlColumn;

    // Data storage
    private final ObservableList<BrokenLink> results = FXCollections.observableArrayList();

    // Task crawler (biar bisa dihentikan)
    private Task<Void> crawlTask;

    @FXML
    public void initialize() {
        // Binding lebar kolom ke persentase
        statusColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.2));
        urlColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.8));

        // Kolom status → pakai HttpStatus
        statusColumn.setCellValueFactory(cellData -> {
            int code = cellData.getValue().getStatusCode();
            String text = HttpStatus.getStatus(code);
            return new ReadOnlyStringWrapper(text);
        });

        // Kolom URL → langsung dari property
        urlColumn.setCellValueFactory(cellData -> cellData.getValue().urlProperty());

        resultsTable.setItems(results);

        // URL clickable hyperlink
        urlColumn.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();

            {
                link.setOnAction(e -> {
                    String url = link.getText();
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(new URI(url));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    link.setText(item);
                    setGraphic(link);
                }
            }
        });

        updateStats();
    }

    @FXML
    private void onStartClick() {
        String seedUrl = seedUrlField.getText();
        if (seedUrl == null || seedUrl.isBlank()) {
            showAlert("Please enter a Seed URL first.");
            return;
        }

        System.out.println("Start crawling: " + seedUrl);

        // Reset data lama
        results.clear();
        updateStats();

        // Jalankan crawler di background task
        crawlTask = new Task<>() {
            @Override
            protected Void call() {
                Crawler crawler = new Crawler(seedUrl);

                crawler.startCrawling(brokenLink -> {
                    if (isCancelled()) return; // stop kalau user tekan "Stop"

                    Platform.runLater(() -> {
                        results.add(brokenLink);
                        updateStats();
                    });
                });

                return null;
            }
        };

        Thread thread = new Thread(crawlTask);
        thread.setDaemon(true); // mati otomatis saat app ditutup
        thread.start();
    }

    @FXML
    private void onStopClick() {
        if (crawlTask != null && crawlTask.isRunning()) {
            crawlTask.cancel();
            showAlert("Crawling stopped.");
        } else {
            showAlert("No crawling task is running.");
        }
    }

    @FXML
    private void onExportClick() {
        showAlert("Export not implemented yet.");
    }

    private void updateStats() {
        int total = results.size();
        long broken = results.stream()
                .filter(r -> r.getStatusCode() >= 400 || r.getStatusCode() == 0)
                .count();
        long webpages = results.stream()
                .filter(r -> r.getStatusCode() < 400 && r.getStatusCode() != 0)
                .count();

        totalLinksLabel.setText(String.valueOf(total));
        brokenLinksLabel.setText(String.valueOf(broken));
        webpagesLabel.setText(String.valueOf(webpages));

        // Progress sementara (belum real)
        progressLabel.setText(total == 0 ? "0%" : "…");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}