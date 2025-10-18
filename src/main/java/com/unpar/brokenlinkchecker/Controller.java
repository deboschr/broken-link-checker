package com.unpar.brokenlinkchecker;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;

import com.unpar.brokenlinkchecker.model.*;

public class Controller {

   // ============================= FXML BINDINGS =============================
   @FXML
   private BorderPane root;

   // Title bar
   @FXML
   private HBox titleBar;
   @FXML
   private Button minimizeBtn, maximizeBtn, closeBtn;

   // Input + Control Button
   @FXML
   private TextField seedUrlField;
   @FXML
   private Button startBtn, stopBtn, exportButton;

   // Summary
   @FXML
   private Label checkingStatusLabel, totalLinksLabel, webpageLinksLabel, brokenLinksLabel;

   // Result Table
   @FXML
   private TableView<Link> resultTable;
   @FXML
   private TableColumn<Link, String> statusColumn, urlColumn;

   // ============================= FIELDS =============================
   private double xOffset = 0;
   private double yOffset = 0;

   // ObservableList berisi semua link rusak (akan tampil di tabel)
   private final ObservableList<Link> brokenLinks = FXCollections.observableArrayList();

   // Model summary card yang akan di-bind ke label
   private final SummaryCard summaryCard = new SummaryCard();

   @FXML
   public void initialize() {
      Platform.runLater(() -> {
         initTitleBar();
         initResultTable();
         initSummaryCard();
      });
   }

   // ============================= EVENT HANDLERS =============================
   @FXML
   private void onStartClick() {
      String seedUrl = seedUrlField.getText().trim();

      // kosongkan data lama
      brokenLinks.clear();

      // buat crawler dan kirim consumer
      Crawler crawler = new Crawler(
            seedUrl,
            link -> brokenLinks.add(link), // update tabel real-time
            summary -> { // update summary real-time
               checkingStatusLabel.setText(summary.getCheckingStatus().getText());
               totalLinksLabel.setText(String.valueOf(summary.getTotalLinks()));
               webpageLinksLabel.setText(String.valueOf(summary.getWebpages()));
               brokenLinksLabel.setText(String.valueOf(summary.getBrokenLinks()));
            },
            summaryCard);

      // jalankan di thread terpisah
      new Thread(crawler::start).start();
   }

   @FXML
   private void onStopClick() {
      summaryCard.setCheckingStatus(CheckingStatus.STOPPED);
   }

   @FXML
   private void onExportClick() {
      showAlert("Export not implemented yet.");
   }

   // ============================= TITLE BAR =============================
   private void initTitleBar() {
      Stage stage = (Stage) titleBar.getScene().getWindow();

      // biar bisa digeser manual
      titleBar.setOnMousePressed((MouseEvent e) -> {
         xOffset = e.getSceneX();
         yOffset = e.getSceneY();
      });

      titleBar.setOnMouseDragged((MouseEvent e) -> {
         stage.setX(e.getScreenX() - xOffset);
         stage.setY(e.getScreenY() - yOffset);
      });

      minimizeBtn.setOnAction(e -> stage.setIconified(true));
      maximizeBtn.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
      closeBtn.setOnAction(e -> stage.close());
   }

   // ============================= SUMMARY CARD =============================
   private void initSummaryCard() {
      // Bind label text dengan property di SummaryCard
      checkingStatusLabel.textProperty().bind(summaryCard.checkingStatusProperty().asString());
      totalLinksLabel.textProperty().bind(summaryCard.totalLinksProperty().asString());
      webpageLinksLabel.textProperty().bind(summaryCard.webpagesProperty().asString());
      brokenLinksLabel.textProperty().bind(summaryCard.brokenLinksProperty().asString());
   }

   // ============================= RESULT TABLE =============================
   private void initResultTable() {
      // atur lebar kolom
      statusColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.2));
      urlColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.8));

      // set sumber data tabel
      resultTable.setItems(brokenLinks);

      // statusColumn ambil data errorProperty (teks status HTTP)
      statusColumn.setCellValueFactory(cell -> cell.getValue().errorProperty());
      urlColumn.setCellValueFactory(cell -> cell.getValue().urlProperty());

      // nonaktifkan resize manual kolom
      resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

      // STATUS COLUMN — teks berwarna
      statusColumn.setCellFactory(col -> new TableCell<>() {
         @Override
         protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);
            if (empty || status == null) {
               setText(null);
               setStyle("");
            } else {
               Link link = getTableView().getItems().get(getIndex());
               int code = link.getStatusCode();
               setText(status);

               // warna merah untuk error
               if (code >= 400 && code < 600)
                  setStyle("-fx-text-fill: #ef4444;");
               else
                  setStyle("-fx-text-fill: #f9fafb;");
            }
         }
      });

      // URL COLUMN — hyperlink klik-buka di browser
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
               link.setStyle("-fx-text-fill: #60a5fa; -fx-underline: true;");
               setGraphic(link);
            }
         }
      });
   }

   // ============================= UTIL =============================
   private void showAlert(String message) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
   }
}
