package com.unpar.brokenlinkchecker;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unpar/brokenlinkchecker/view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        // apply CSS
        getClass().getResource("/com/unpar/brokenlinkchecker/style.css");

        stage.setTitle("BrokenLink Checker");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
