package com.unpar.brokenlinkchecker;

import com.unpar.brokenlinkchecker.models.LinkResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.Desktop;
import java.net.URI;

/**
 * Controller untuk view-v2.fxml
 */
public class ControllerV2 {

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
    private TableView<LinkResult> resultsTable;

    @FXML
    private TableColumn<LinkResult, String> statusColumn;
    @FXML
    private TableColumn<LinkResult, String> urlColumn;

    // Data storage
    private final ObservableList<LinkResult> results = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Binding lebar kolom ke persentase
        statusColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.2)); // 30%
        urlColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.8));    // 70%

        // Set data ke kolom
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
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

        // Dummy data
        results.add(new LinkResult("200 OK", "https://example.com/page1"));
        results.add(new LinkResult("404 Not Found", "https://example.com/broken"));
        results.add(new LinkResult("500 Internal Server Error", "https://example.com/error"));

        updateStats();
    }



    @FXML
    private void onStartClick() {
        String seedUrl = seedUrlField.getText();
        if (seedUrl == null || seedUrl.isBlank()) {
            showAlert("Please enter a Seed URL first.");
            return;
        }

        // TODO: panggil service crawling
        System.out.println("Start crawling: " + seedUrl);

        // contoh update dummy data
        results.clear();
        results.add(new LinkResult("200 OK", seedUrl + "/home"));
        results.add(new LinkResult("404 Not Found", seedUrl + "/missing"));
        updateStats();
    }

    @FXML
    private void onStopClick() {
        // TODO: implementasi stop crawling
        System.out.println("Crawling stopped.");
    }

    @FXML
    private void onExportClick() {
        // TODO: implementasi export ke Excel/CSV
        System.out.println("Export results...");
    }

    private void updateStats() {
        int total = results.size();
        long broken = results.stream()
                .filter(r -> r.getStatus().startsWith("4") || r.getStatus().startsWith("5"))
                .count();
        long webpages = results.stream()
                .filter(r -> r.getUrl().contains("example.com")) // dummy rule
                .count();

        totalLinksLabel.setText(String.valueOf(total));
        brokenLinksLabel.setText(String.valueOf(broken));
        webpagesLabel.setText(String.valueOf(webpages));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
