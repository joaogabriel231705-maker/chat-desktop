module com.example.chatdesktop {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    opens com.example.chatdesktop.Controller
            to javafx.fxml;

    exports com.example.chatdesktop;
    exports com.example.chatdesktop.Controller;
    exports com.example.chatdesktop.model;
    exports com.example.chatdesktop.service;
    exports com.example.chatdesktop.config;
}