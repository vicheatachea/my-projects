package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class CreditsViewController {
    @FXML
    public void handleGoBack(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/menu-layout.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("World Economy Simulator");
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
