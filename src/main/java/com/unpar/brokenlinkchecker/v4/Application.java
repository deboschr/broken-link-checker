package com.unpar.brokenlinkchecker.v4;

import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unpar/brokenlinkchecker/view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Hilangkan title bar default
        stage.initStyle(StageStyle.UNDECORATED);

        // Konfigurasi ukuran minimum & awal
        stage.setMinWidth(1024);
        stage.setMinHeight(600);
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setResizable(true);

        // Tambahkan listener resize custom
        addResizeListener(stage, root);

        // Center window
        stage.centerOnScreen();

        stage.show();
    }

    private static final int BORDER = 8;

    /** Menambahkan dukungan resize ke window undecorated. */
    public static void addResizeListener(Stage stage, Node root) {
        root.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Cursor cursor = Cursor.DEFAULT;

            boolean left = mouseX < BORDER;
            boolean right = mouseX > width - BORDER;
            boolean top = mouseY < BORDER;
            boolean bottom = mouseY > height - BORDER;

            if (left && top)
                cursor = Cursor.NW_RESIZE;
            else if (left && bottom)
                cursor = Cursor.SW_RESIZE;
            else if (right && top)
                cursor = Cursor.NE_RESIZE;
            else if (right && bottom)
                cursor = Cursor.SE_RESIZE;
            else if (right)
                cursor = Cursor.E_RESIZE;
            else if (left)
                cursor = Cursor.W_RESIZE;
            else if (bottom)
                cursor = Cursor.S_RESIZE;
            else if (top)
                cursor = Cursor.N_RESIZE;

            root.setCursor(cursor);
        });

        root.setOnMouseDragged(event -> {
            if (root.getCursor() == Cursor.DEFAULT)
                return;

            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();

            if (root.getCursor() == Cursor.E_RESIZE || root.getCursor() == Cursor.NE_RESIZE
                    || root.getCursor() == Cursor.SE_RESIZE)
                stage.setWidth(mouseX - stage.getX());
            if (root.getCursor() == Cursor.S_RESIZE || root.getCursor() == Cursor.SE_RESIZE
                    || root.getCursor() == Cursor.SW_RESIZE)
                stage.setHeight(mouseY - stage.getY());
            if (root.getCursor() == Cursor.W_RESIZE || root.getCursor() == Cursor.NW_RESIZE
                    || root.getCursor() == Cursor.SW_RESIZE) {
                double newX = mouseX;
                double newWidth = stage.getX() + stage.getWidth() - newX;
                if (newWidth > stage.getMinWidth()) {
                    stage.setX(newX);
                    stage.setWidth(newWidth);
                }
            }
            if (root.getCursor() == Cursor.N_RESIZE || root.getCursor() == Cursor.NE_RESIZE
                    || root.getCursor() == Cursor.NW_RESIZE) {
                double newY = mouseY;
                double newHeight = stage.getY() + stage.getHeight() - newY;
                if (newHeight > stage.getMinHeight()) {
                    stage.setY(newY);
                    stage.setHeight(newHeight);
                }
            }
        });
    }
}
