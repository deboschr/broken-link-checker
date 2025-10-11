package com.unpar.brokenlinkchecker.v4;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {

   @FXML
   private TextField seedUrlField;
   @FXML
   private Button startButton;
   @FXML
   private Button stopButton;

   @FXML
   private Label statusLabel;
   @FXML
   private Label totalLinksLabel;
   @FXML
   private Label webpageLinksLabel;
   @FXML
   private Label brokenLinksLabel;

   @FXML
   private ComboBox<String> urlSelectBox;
   @FXML
   private TextField urlFilterField;
   @FXML
   private ComboBox<String> statusSelectBox;
   @FXML
   private TextField statusFilterField;

   @FXML
   private Button exportButton;

   @FXML
   private TableView<?> resultTable;
   @FXML
   private TableColumn<?, ?> resultCol;
   @FXML
   private TableColumn<?, ?> urlCol;

   @FXML
   private void initialize() {
      // Setup awal TableView atau listener tombol bisa kamu tambahkan di sini
   }

   @FXML
   private void onStartClick() {
      // TODO: jalankan proses checking
   }

   @FXML
   private void onStopClick() {
      // TODO: hentikan proses checking
   }

   @FXML
   private void onExportClick() {
      // TODO: ekspor hasil ke Excel
   }
}
