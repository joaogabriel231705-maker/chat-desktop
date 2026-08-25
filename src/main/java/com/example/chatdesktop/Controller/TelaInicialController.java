
package com.example.chatdesktop.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

public class TelaInicialController {

    @FXML
    private void entrarNaIA(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/chatdesktop/hello-view.fxml"
                    )
            );

            Scene scene = new Scene(
                    loader.load(),
                    800,
                    600
            );

            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setTitle("Nexa AI");

            stage.setScene(scene);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
