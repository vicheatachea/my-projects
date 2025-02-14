package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import datasource.MariaDbConnection;

public class MenuViewController {
	@FXML
	public void handleStartSimulation(ActionEvent event) {
		try (Connection ignored = MariaDbConnection.getConnection()
		) {
			MariaDbConnection.executeSqlFile("scripts/simulationDb.sql");
			MariaDbConnection.executeSqlFile("scripts/simulationMetrics.sql");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/settings-layout.fxml"));
			Parent root = fxmlLoader.load();

			Scene scene = new Scene(root);

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.setTitle("Settings");
			stage.setMaximized(true);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleSimulationCredits(ActionEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/credits-layout.fxml"));
			Parent root = fxmlLoader.load();

			Scene scene = new Scene(root);

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.setTitle("Credits");
			stage.setFullScreen(true);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleQuitSimulation() {
		Platform.exit();
	}
}
