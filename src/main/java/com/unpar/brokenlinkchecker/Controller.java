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

    // Data Observable
    private final ObservableList<WebpageLink> webpageData = FXCollections.observableArrayList();
    private final ObservableList<BrokenLink> brokenData   = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind kolom WebpageLink
        wpUrlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        wpStatusColumn.setCellValueFactory(new PropertyValueFactory<>("statusCode"));
        wpCountColumn.setCellValueFactory(new PropertyValueFactory<>("linkCount"));
        wpAccessTimeColumn.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getAccessTime().toString()   // ubah Instant jadi String
                )
        );

        // Bind kolom BrokenLink
        blUrlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        blStatusColumn.setCellValueFactory(new PropertyValueFactory<>("statusCode"));
        blAnchorColumn.setCellValueFactory(new PropertyValueFactory<>("anchorText"));
        blSourceColumn.setCellValueFactory(new PropertyValueFactory<>("webpageUrl"));

        // Pasang data ke tabel
        webpageTable.setItems(webpageData);
        brokenTable.setItems(brokenData);
    }


    @FXML
    protected void onCheckClick() {
        String seedUrl = urlField.getText().trim();
        if (seedUrl.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Masukkan Seed URL terlebih dahulu!");
            alert.showAndWait();
            return;
        }

        webpageData.clear();
        brokenData.clear();

        Crawler crawler = new Crawler(seedUrl);

        // jalankan di thread terpisah biar UI tidak nge-freeze
        new Thread(() -> {
            crawler.crawl(
                    wp -> Platform.runLater(() -> webpageData.add(wp)),
                    bl -> Platform.runLater(() -> brokenData.add(bl))
            );
        }).start();
    }
}
