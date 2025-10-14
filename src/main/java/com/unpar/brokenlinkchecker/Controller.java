package com.unpar.brokenlinkchecker;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.time.Instant;

/**
 * Controller utama untuk UI Broken Link Checker
 */
public class Controller {

   @FXML
   private BorderPane root;

   // Title bar
   @FXML
   private HBox titleBar;
   @FXML
   private Button minimizeBtn, maximizeBtn, closeBtn;

   // Input + Control
   @FXML
   private TextField seedUrlField;
   @FXML
   private Button startBtn, stopBtn;

   // Summary
   @FXML
   private Label checkingStatusLabel;
   @FXML
   private Label totalLinksLabel;
   @FXML
   private Label webpageLinksLabel;
   @FXML
   private Label brokenLinksLabel;

   // Result Table
   @FXML
   private TableView<BrokenLink> resultTable;
   @FXML
   private TableColumn<BrokenLink, String> statusColumn;
   @FXML
   private TableColumn<BrokenLink, String> urlColumn;
   @FXML
   private Button exportBtn;

   // Pagination
   @FXML
   private Button prevPageBtn, nextPageBtn;

   // Fields
   private double xOffset = 0;
   private double yOffset = 0;
   private CheckingStatus currentCheckingStatus = CheckingStatus.IDLE;
   private final ObservableList<BrokenLink> brokenLinks = FXCollections.observableArrayList();

   @FXML
   public void initialize() {
      Platform.runLater(() -> {
         initTitleBar();
         initResultTable();
      });
   }

   @FXML
   private void onStartClick() {
      showAlert("Start not implemented yet.");
   }

   @FXML
   private void onStopClick() {
      showAlert("Stop not implemented yet.");
   }

   @FXML
   private void onExportClick() {
      showAlert("Export not implemented yet.");
   }

   private void initTitleBar() {
      Stage stage = (Stage) titleBar.getScene().getWindow();

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

   private void initResultTable() {
      // atur lebar kolom
      statusColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.2));
      urlColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.8));

      // Set binding data
      statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());
      urlColumn.setCellValueFactory(cell -> cell.getValue().urlProperty());
      resultTable.setItems(brokenLinks);

      // Matikan resize kolom
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
               BrokenLink link = getTableView().getItems().get(getIndex());
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

      // URL COLUMN — bisa di klik hyperlink
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

      // Data dummy
      brokenLinks.addAll(
            new BrokenLink("https://informatika.unpar.ac.id/laboratorium-komputasi/", 500, Instant.now()),
            new BrokenLink("https://www.agatestudio.com/", 404, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/penambangan-data/", 0, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/data-science-untuk-domain-spesifik/", 404, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/basisdata-dan-pemrograman-sql-untuk-big-data/", 0,
                  Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/laboratorium-komputasi/", 500, Instant.now()),
            new BrokenLink("https://www.agatestudio.com/", 404, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/penambangan-data/", 0, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/data-science-untuk-domain-spesifik/", 404, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/basisdata-dan-pemrograman-sql-untuk-big-data/", 0,
                  Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/laboratorium-komputasi/", 500, Instant.now()),
            new BrokenLink("https://www.agatestudio.com/", 404, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/penambangan-data/", 0, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/data-science-untuk-domain-spesifik/", 404, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/basisdata-dan-pemrograman-sql-untuk-big-data/", 0,
                  Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/laboratorium-komputasi/", 500, Instant.now()),
            new BrokenLink("https://www.agatestudio.com/", 404, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/penambangan-data/", 0, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/data-science-untuk-domain-spesifik/", 404, Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/basisdata-dan-pemrograman-sql-untuk-big-data/", 0,
                  Instant.now()),
            new BrokenLink("https://informatika.unpar.ac.id/statistika-dengan-r/", 999, Instant.now()));
   }

   private void showAlert(String message) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
   }
}
