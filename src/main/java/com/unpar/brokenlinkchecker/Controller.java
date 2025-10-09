package com.unpar.brokenlinkchecker;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.net.URI;

import com.unpar.brokenlinkchecker.BrokenLink;
import com.unpar.brokenlinkchecker.ProgressStatus;
import com.unpar.brokenlinkchecker.HttpStatus;

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

      statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
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

      progressLabel.setText(ProgressStatus.IDLE.getText());
      // updateSummaryCard(0, 0, 0);
   }

   @FXML
   private void onStartClick() {
      String seedUrl = seedUrlField.getText();

      String cleanedUrl = Crawler.normalizeUrl(seedUrl);

      if (seedUrl == null || seedUrl.isBlank()) {
         showAlert("Please enter a Seed URL first.");
         return;
      }

      results.clear();
      // updateSummaryCard(0, 0, 0);

      crawlTask = new Task<>() {
         @Override
         protected Void call() {
            Crawler crawler = new Crawler(seedUrl);

            crawler.startCrawling(
                  brokenLink -> Platform.runLater(() -> {
                     if (!results.contains(brokenLink)) {
                        results.add(brokenLink);
                     }
                     updateSummaryCard();
                  }),
                  totalLinks -> Platform.runLater(() -> updateSummaryCard(totalLinks)),
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
         progressLabel.setText(ProgressStatus.STOPPED.getText());
      } else {
         showAlert("No crawling task is running.");
      }
   }

   @FXML
   private void onExportClick() {
      showAlert("Export not implemented yet.");
   }

   private void showAlert(String message) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
   }

}
