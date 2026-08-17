module com.example.chatdesktop {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    opens com.example.chatdesktop to javafx.fxml;

    exports com.example.chatdesktop;
}