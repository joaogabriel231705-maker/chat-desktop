package com.example.chatdesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        URL fxml = Main.class.getResource(
                "/com/example/chatdesktop/hello-view.fxml"
        );

        if (fxml == null) {
            throw new RuntimeException(
                    "ERRO: hello-view.fxml não foi encontrado!"
            );
        }

        FXMLLoader fxmlLoader =
                new FXMLLoader(fxml);

        Scene scene = new Scene(
                fxmlLoader.load(),
                800,
                600
        );

        stage.setTitle("Nexa AI");

        stage.setMinWidth(600);
        stage.setMinHeight(450);

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}