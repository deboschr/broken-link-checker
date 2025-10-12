package com.unpar.brokenlinkchecker;

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
   private Button exportButton;

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
   private TableView<?> resultTable;
   @FXML
   private TableColumn<?, ?> statusCol;
   @FXML
   private TableColumn<?, ?> urlCol;

   @FXML
   public void initialize() {
      // Inisialisasi awal TableView atau ComboBox bisa ditambahkan di sini
   }

   @FXML
   private void onStartClick() {
      // TODO: mulai pemeriksaan tautan
   }

   @FXML
   private void onStopClick() {
      // TODO: hentikan pemeriksaan
   }

   @FXML
   private void onExportClick() {
      // TODO: ekspor hasil
   }
}
