package com.unpar.brokenlinkchecker;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    /**
     * Method main() jadi titik masuk program.
     */
    public static void main(String[] args) {
        launch(); // launch() dipanggil untuk memulai lifecycle JavaFX.
    }

    /**
     * Method start() otomatis dijalankan setelah method launch() dijalankan.
     * Tugasnya: menyiapkan UI utama aplikasi.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Loader buat baca file FXML (layout utama aplikasi)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unpar/brokenlinkchecker/view.fxml"));
        Parent root = loader.load();

        // Bungkus root node ke dalam Scene
        Scene scene = new Scene(root);

        // apply CSS
        getClass().getResource("/com/unpar/brokenlinkchecker/style.css");

        // Set konfigurasi stage (window utama)
        stage.setTitle("BrokenLink Checker"); // judul window
        stage.setScene(scene);                // kasih scene ke stage
        stage.centerOnScreen();               // biar muncul di tengah layar
        stage.show();                         // tampilkan window
    }
}
