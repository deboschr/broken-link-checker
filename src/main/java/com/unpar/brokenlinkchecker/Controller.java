package com.unpar.brokenlinkchecker;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Controller {

   // =======================================
   // =========== FXML Components ===========

   // TITLE BAR
   @FXML
   private HBox titleBar;
   @FXML
   private Button minimizeBtn, maximizeBtn, closeBtn;

   // INPUT URL & CONTROLL PROSSES
   @FXML
   private TextField seedUrlField;
   @FXML
   private Button startBtn, stopBtn;

   // SUMMARY
   @FXML
   private Label checkingStatusLabel;
   @FXML
   private Label totalLinksLabel;
   @FXML
   private Label webpageLinksLabel;
   @FXML
   private Label brokenLinksLabel;

   // =======================================
   // =========== Private Fields ============

   private double xOffset = 0;
   private double yOffset = 0;
   private CheckingStatus currentCheckingStatus = CheckingStatus.IDLE;

   @FXML
   public void initialize() {
      Platform.runLater(this::initTitleBar);

      startBtn.setOnAction(e -> setActiveButton("start"));
      stopBtn.setOnAction(e -> setActiveButton("stop"));

      updateStatusLabel();
   }

   private void initTitleBar() {
      Stage stage = (Stage) titleBar.getScene().getWindow();

      // --- Window Dragging ---
      titleBar.setOnMousePressed((MouseEvent event) -> {
         xOffset = event.getSceneX();
         yOffset = event.getSceneY();
      });

      titleBar.setOnMouseDragged((MouseEvent event) -> {
         stage.setX(event.getScreenX() - xOffset);
         stage.setY(event.getScreenY() - yOffset);
      });

      // --- Control Buttons ---
      closeBtn.setOnAction(e -> stage.close());

      minimizeBtn.setOnAction(e -> stage.setIconified(true));

      maximizeBtn.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
   }

   private void setActiveButton(String active) {
      startBtn.getStyleClass().removeAll("btn-start-active");
      stopBtn.getStyleClass().removeAll("btn-stop-active");

      if ("start".equals(active)) {
         startBtn.getStyleClass().add("btn-start-active");
      } else if ("stop".equals(active)) {
         stopBtn.getStyleClass().add("btn-stop-active");
      }
   }

   private void updateStatusLabel() {
      checkingStatusLabel.setText(currentCheckingStatus.getText());
      checkingStatusLabel.getStyleClass().removeAll("status-idle", "status-checking", "status-stopped",
            "status-completed");

      switch (currentCheckingStatus) {
         case IDLE -> checkingStatusLabel.getStyleClass().add("status-idle");
         case CHECKING -> checkingStatusLabel.getStyleClass().add("status-checking");
         case STOPPED -> checkingStatusLabel.getStyleClass().add("status-stopped");
         case COMPLETED -> checkingStatusLabel.getStyleClass().add("status-completed");
      }
   }

   public void onStartClick() {
      currentCheckingStatus = CheckingStatus.CHECKING;
      updateStatusLabel();
   }

   public void onStopClick() {
      currentCheckingStatus = CheckingStatus.STOPPED;
      updateStatusLabel();
   }

   // @FXML
   // private void onExportClick() {
   // // TODO: ekspor hasil
   // }
}
