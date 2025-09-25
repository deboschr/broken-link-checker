module com.unpar.brokenlinkchecker {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires java.net.http;
    requires java.desktop;

    opens com.unpar.brokenlinkchecker to javafx.fxml;
    opens com.unpar.brokenlinkchecker.models to javafx.base;

    exports com.unpar.brokenlinkchecker;
    exports com.unpar.brokenlinkchecker.models;
}
