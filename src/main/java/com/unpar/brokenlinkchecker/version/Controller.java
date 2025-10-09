package com.unpar.brokenlinkchecker.version;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.net.URI;

import com.unpar.brokenlinkchecker.temp.BrokenLink;
import com.unpar.brokenlinkchecker.temp.CrawlStatus;
import com.unpar.brokenlinkchecker.temp.HttpStatus;

/**
 * Controller untuk view.fxml
 */
public class Controller {

    @FXML
    private TextField seedUrlField;

    @FXML
    private Label totalLinksLabel;
    @FXML
    private Label brokenLinksLabel;
    @FXML
    private Label webpagesLabel;
    @FXML
    private Label progressLabel;

    @FXML
    private TableView<BrokenLink> resultsTable;
    @FXML
    private TableColumn<BrokenLink, String> statusColumn;
    @FXML
    private TableColumn<BrokenLink, String> urlColumn;

    private final ObservableList<BrokenLink> results = FXCollections.observableArrayList();
    private Task<Void> crawlTask;

    @FXML
    public void initialize() {
        statusColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.2));
        urlColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.8));

        statusColumn.setCellValueFactory(cellData -> {
            int code = cellData.getValue().getStatusCode();
            String text = HttpStatus.getStatus(code);
            return new ReadOnlyStringWrapper(text);
        });

        urlColumn.setCellValueFactory(cellData -> cellData.getValue().urlProperty());
        resultsTable.setItems(results);

        // hyperlink di tabel
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

        progressLabel.setText(CrawlStatus.IDLE.getText());
        updateStats(0, 0, 0);
    }

    @FXML
    private void onStartClick() {
        String seedUrl = seedUrlField.getText();
        if (seedUrl == null || seedUrl.isBlank()) {
            showAlert("Please enter a Seed URL first.");
            return;
        }

        results.clear();
        updateStats(0, 0, 0);

        crawlTask = new Task<>() {
            @Override
            protected Void call() {
                Crawler crawler = new Crawler(seedUrl);

                crawler.startCrawling(
                        brokenLink -> Platform.runLater(() -> {
                            if (!results.contains(brokenLink)) {
                                results.add(brokenLink);
                            }
                            updateStats();
                        }),
                        totalLinks -> Platform.runLater(() -> updateStats(totalLinks)),
                        status -> Platform.runLater(() -> progressLabel.setText(status.getText())));
                return null;
            }
        };

        Thread thread = new Thread(crawlTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void onStopClick() {
        if (crawlTask != null && crawlTask.isRunning()) {
            crawlTask.cancel();
            progressLabel.setText(CrawlStatus.STOPPED.getText());
        } else {
            showAlert("No crawling task is running.");
        }
    }

    @FXML
    private void onExportClick() {
        showAlert("Export not implemented yet.");
    }

    private void updateStats() {
        int total = Integer.parseInt(totalLinksLabel.getText());
        long broken = results.size();
        long webpages = total - broken;
        updateStats(total, broken, webpages);
    }

    private void updateStats(int total, long broken, long webpages) {
        totalLinksLabel.setText(String.valueOf(total));
        brokenLinksLabel.setText(String.valueOf(broken));
        webpagesLabel.setText(String.valueOf(webpages));
    }

    private void updateStats(int total) {
        long broken = results.size();
        long webpages = total - broken;
        updateStats(total, broken, webpages);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
