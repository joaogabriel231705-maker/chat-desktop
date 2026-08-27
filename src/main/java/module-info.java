module com.example.chatdesktop {

    // ==========================================
    // JAVAFX
    // ==========================================

    requires javafx.controls;
    requires javafx.fxml;


    // ==========================================
    // JAVA HTTP
    // ==========================================

    requires java.net.http;


    // ==========================================
    // PDFBOX
    // ==========================================

    requires org.apache.pdfbox;


    // ==========================================
    // APACHE POI
    // ==========================================

    requires org.apache.poi.ooxml;


    // ==========================================
    // FXML
    // ==========================================

    opens com.example.chatdesktop.Controller
            to javafx.fxml;


    // ==========================================
    // EXPORTS
    // ==========================================

    exports com.example.chatdesktop;

    exports com.example.chatdesktop.Controller;

    exports com.example.chatdesktop.model;

    exports com.example.chatdesktop.service;

    exports com.example.chatdesktop.config;
}