package com.unpar.brokenlinkchecker;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.net.URI;
import java.time.Instant;

/**
 * Controller untuk view.fxml
 */
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
    // Result table column
    @FXML
    private TableColumn<BrokenLink, String> statusColumn;
    @FXML
    private TableColumn<BrokenLink, String> urlColumn;

    // Data storage
    private final ObservableList<BrokenLink> results = FXCollections.observableArrayList();

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

        // Tambahkan data dummy
        results.add(new BrokenLink("https://example.com/404", 404, Instant.now()));
        results.add(new BrokenLink("https://example.com/500", 500, Instant.now()));
        results.add(new BrokenLink("https://other.com/image.png", 0, Instant.now())); // 0 = connection error
        results.add(new BrokenLink("https://example.com/home", 200, Instant.now()));

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

        // Reset data dummy
        results.clear();
        results.add(new BrokenLink(seedUrl + "/404", 404, Instant.now()));
        results.add(new BrokenLink(seedUrl + "/contact", 200, Instant.now()));

        updateStats();
    }

    @FXML
    private void onStopClick() {
        showAlert("Stop crawling not implemented yet.");
    }

    @FXML
    private void onExportClick() {
        showAlert("Export not implemented yet.");
    }

    private void updateStats() {
        int total = results.size();
        long broken = results.stream()
                .filter(r -> r.getStatusCode() >= 400)
                .count();
        long webpages = results.stream()
                .filter(r -> r.getUrl().contains("example.com")) // dummy rule
                .count();

        totalLinksLabel.setText(String.valueOf(total));
        brokenLinksLabel.setText(String.valueOf(broken));
        webpagesLabel.setText(String.valueOf(webpages));

        // progress dummy
        progressLabel.setText(total == 0 ? "0%" : "100%");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
