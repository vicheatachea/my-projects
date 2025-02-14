package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class SimulationView extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/menu-layout.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("World Economy Simulator");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setWidth(900);
        stage.setHeight(600);
        stage.show();
    }
}
