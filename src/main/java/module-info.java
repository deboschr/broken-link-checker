module com.unpar.brokenlinkchecker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.jsoup;

    requires java.net.http;
    requires java.desktop;

    opens com.unpar.brokenlinkchecker to javafx.fxml;

    exports com.unpar.brokenlinkchecker;
}
