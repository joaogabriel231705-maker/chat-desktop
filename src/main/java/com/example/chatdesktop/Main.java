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
                "/com/example/chatdesktop/tela-inicial.fxml"
        );

        if (fxml == null) {

            throw new RuntimeException(
                    "ERRO: tela-inicial.fxml não foi encontrada!"
            );
        }

        FXMLLoader loader =
                new FXMLLoader(fxml);


        Scene scene = new Scene(
                loader.load(),
                1000,
                650
        );

        stage.setTitle(
                "NexaSoft"
        );

        stage.setMinWidth(800);

        stage.setMinHeight(550);

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {

        launch();
    }
}