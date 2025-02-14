package controller;

import dao.*;
import entity.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.core.Country;
import model.core.Resource;
import model.simulation.Clock;
import model.simulation.SimulationConfig;
import model.simulation.Simulator;

import java.util.ArrayList;
import java.util.List;

public class SimulationController {
	private Simulator simulator;

	private CountryEntity selectedCountry;
	private ResourceEntity selectedResource;
	private ResourceEntity selectedResourceNode;

	private String currentSeriesName;
	private int currentDay = 0;

	private final CountryDao countryDao = new CountryDao();
	private final ResourceDao resourceDao = new ResourceDao();
	private final ResourceNodeDao resourceNodeDao = new ResourceNodeDao();
	private final CountryMetricsDao countryMetricsDao = new CountryMetricsDao();
	private final ResourceMetricsDao resourceMetricsDao = new ResourceMetricsDao();
	private final ResourceNodeMetricsDao resourceNodeMetricsDao = new ResourceNodeMetricsDao();

	@FXML
	private Button toggleSimulationButton;
	@FXML
	private TextField daysTextField;
	@FXML
	private Label delayWarningLabel;
	@FXML
	private TextField delayTextField;
	@FXML
	private ComboBox<String> countryComboBox;
	@FXML
	private ComboBox<String> resourceComboBox;
	@FXML
	private ComboBox<String> resourceNodeComboBox;
	@FXML
	private LineChart<String, Number> lineChart;
	@FXML
	private Label informationLabel;

	@FXML
	private void initialize() {
		delayWarningLabel.setVisible(false);
		delayWarningLabel.setManaged(false);
	}

	public void initialize(List<Resource> resources, List<Country> countries) {
		countryComboBox.getItems().addAll(countries.stream().map(Country::getName).toList());
		resourceComboBox.getItems().addAll(resources.stream().map(Resource::name).toList());


		this.simulator = new Simulator(this, resources, countries);
	}

	public void beginSimulation() {
		Thread simulationThread = new Thread(() -> {
			this.simulator.runSimulation();
		});
		simulationThread.setDaemon(true);
		simulationThread.start();
	}

	public void endSimulation() {
		Platform.runLater(() -> informationLabel.setText("Simulation has ended."));
	}

	public synchronized void updateData() {
		Platform.runLater(() -> {
			int currentDay = Clock.getInstance().getTime();
			this.currentDay = currentDay;
			daysTextField.setText(String.valueOf(currentDay));
		});

		if (currentSeriesName == null) {
			return;
		}

		switch (currentSeriesName) {
			case "Population/Day":
				updatePopulationGraph();
				break;
			case "Money/Day":
				updateMoneyGraph();
				break;
			case "Average Happiness/Day":
				updateAverageHappinessGraph();
				break;
			case "Individual Budget/Day":
				updateIndividualBudgetGraph();
				break;
			case "Quantity/Day":
				updateQuantityGraph();
				break;
			case "Value/Day":
				updateValueGraph();
				break;
			case "Production Cost/Day":
				updateProductionCostGraph();
				break;
			case "Max Capacity/Day":
				updateMaxCapacityGraph();
				break;
			case "Tier/Day":
				updateTierGraph();
				break;
		}
	}

	private void drawLineGraph(String seriesName, List<Integer> xData, List<? extends Number> yData) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName(seriesName);
		currentSeriesName = seriesName;

		for (int i = 0; i < xData.size(); i++) {
			series.getData().add(new XYChart.Data<>(String.valueOf(xData.get(i)), yData.get(i)));
		}

		lineChart.getData().clear();
		lineChart.getData().add(series);
	}

	private void updateLineGraph(String seriesName, int xData, Number yData) {
		Platform.runLater(() -> {
			XYChart.Series<String, Number> series = null;

			// Check if the series already exists
			for (XYChart.Series<String, Number> existingSeries : lineChart.getData()) {
				if (existingSeries.getName().equals(seriesName)) {
					series = existingSeries;
					break;
				}
			}

			// If the series doesn't exist, create a new one
			if (series == null) {
				series = new XYChart.Series<>();
				series.setName(seriesName);
				lineChart.getData().add(series);
			}

			// Update the series with new data
			series.getData().add(new XYChart.Data<>(String.valueOf(xData), yData));
		});
	}

	@FXML
	public void toggleSimulation() {
		if (Clock.getInstance().isPaused()) {
			Clock.getInstance().resume();
			toggleSimulationButton.setText("Pause");
		} else {
			Clock.getInstance().pause();
			toggleSimulationButton.setText("Resume");
		}
	}

	@FXML
	public void updateDelay() {
		try {
			int newDelay = Integer.parseInt(delayTextField.getText());
			SimulationConfig.setSimulationDelay(newDelay);
			delayWarningLabel.setVisible(false);
			delayWarningLabel.setManaged(false);
		} catch (IllegalArgumentException e) {
			delayWarningLabel.setVisible(true);
			delayWarningLabel.setManaged(true);
			delayTextField.setText(String.valueOf(SimulationConfig.getSimulationDelay()));
		}
	}

	@FXML
	private void handleCountrySelection() {
		String selectedCountryName = countryComboBox.getValue();
		if (selectedCountryName != null) {
			selectedCountry = countryDao.findByName(selectedCountryName);
		}
		populateResourceNodeComboBox();
		redrawGraph();
	}

	@FXML
	private void handleResourceSelection() {
		String selectedResourceName = resourceComboBox.getValue();
		if (selectedResourceName != null) {
			selectedResource = resourceDao.findByName(selectedResourceName);
		}
		redrawGraph();
	}

	private void populateResourceNodeComboBox() {

		resourceNodeComboBox.getItems().clear();
		List<ResourceNodeEntity> resourceNodeEntities = resourceNodeDao.findByCountry(selectedCountry);
		List<ResourceEntity> resourceEntities = new ArrayList<>();
		resourceNodeEntities.forEach(resourceNodeEntity -> {
			resourceEntities.add(resourceNodeEntity.getResource());
		});
		resourceEntities.forEach(resourceEntity -> {
			resourceNodeComboBox.getItems().add(resourceEntity.getName());
		});
	}

	@FXML
	private void handleResourceNodeSelection() {
		String selectedResourceNodeName = resourceNodeComboBox.getValue();
		System.out.println(selectedResourceNodeName);
		if (selectedResourceNodeName != null) {
			selectedResourceNode = resourceDao.findByName(selectedResourceNodeName);
		}
		redrawGraph();
//		System.out.println(selectedResourceNode);
	}

	private void redrawGraph() {
		if (currentSeriesName == null) {
			return;
		}

		switch (currentSeriesName) {
			case "Population/Day":
				handlePopulationButton();
				break;
			case "Money/Day":
				handleMoneyButton();
				break;
			case "Average Happiness/Day":
				handleAverageHappinessButton();
				break;
			case "Individual Budget/Day":
				handleIndividualBudgetButton();
				break;
			case "Quantity/Day":
				handleQuantityButton();
				break;
			case "Value/Day":
				handleValueButton();
				break;
			case "Production Cost/Day":
				handleProductionCostButton();
				break;
			case "Max Capacity/Day":
				handleMaxCapacityButton();
				break;
			case "Tier/Day":
				handleTierButton();
				break;
		}
	}

	@FXML
	private void handlePopulationButton() {

		if (selectedCountry == null) {
			return;
		}

		List<CountryMetricsEntity> countryMetricEntities = countryMetricsDao.findByCountry(selectedCountry);

		List<Integer> xData = countryMetricEntities.stream().map(CountryMetricsEntity::getDay).toList();
		List<Long> yData = countryMetricEntities.stream().map(CountryMetricsEntity::getPopulation).toList();

		drawLineGraph("Population/Day", xData, yData);
	}

	private void updatePopulationGraph() {

		if (selectedCountry == null) {
			return;
		}
		CountryMetricsEntity countryMetricsEntity = countryMetricsDao.findByCountryAndDay(selectedCountry, currentDay);

		int xData = countryMetricsEntity.getDay();
		long yData = countryMetricsEntity.getPopulation();

		updateLineGraph("Population/Day", xData, yData);
	}

	@FXML
	private void handleMoneyButton() {

		if (selectedCountry == null) {
			return;
		}
		List<CountryMetricsEntity> countryMetricsEntities = countryMetricsDao.findByCountry(selectedCountry);

		List<Integer> xData = countryMetricsEntities.stream().map(CountryMetricsEntity::getDay).toList();
		List<Double> yData = countryMetricsEntities.stream().map(CountryMetricsEntity::getMoney).toList();

		drawLineGraph("Money/Day", xData, yData);
	}

	private void updateMoneyGraph() {

		if (selectedCountry == null) {
			return;
		}
		CountryMetricsEntity countryMetricsEntity = countryMetricsDao.findByCountryAndDay(selectedCountry, currentDay);

		int xData = countryMetricsEntity.getDay();
		double yData = countryMetricsEntity.getMoney();

		updateLineGraph("Money/Day", xData, yData);
	}

	@FXML
	private void handleAverageHappinessButton() {

		if (selectedCountry == null) {
			return;
		}
		List<CountryMetricsEntity> countryMetricsEntities = countryMetricsDao.findByCountry(selectedCountry);

		List<Integer> xData = countryMetricsEntities.stream().map(CountryMetricsEntity::getDay).toList();
		List<Double> yData = countryMetricsEntities.stream().map(CountryMetricsEntity::getAverageHappiness).toList();

		drawLineGraph("Average Happiness/Day", xData, yData);
	}

	private void updateAverageHappinessGraph() {

		if (selectedCountry == null) {
			return;
		}
		CountryMetricsEntity countryMetricsEntity = countryMetricsDao.findByCountryAndDay(selectedCountry, currentDay);

		int xData = countryMetricsEntity.getDay();
		double yData = countryMetricsEntity.getAverageHappiness();

		updateLineGraph("Average Happiness/Day", xData, yData);
	}

	@FXML
	private void handleIndividualBudgetButton() {

		if (selectedCountry == null) {
			return;
		}
		List<CountryMetricsEntity> countryMetricsEntities = countryMetricsDao.findByCountry(selectedCountry);

		List<Integer> xData = countryMetricsEntities.stream().map(CountryMetricsEntity::getDay).toList();
		List<Double> yData = countryMetricsEntities.stream().map(CountryMetricsEntity::getIndividualBudget).toList();

		drawLineGraph("Individual Budget/Day", xData, yData);
	}

	private void updateIndividualBudgetGraph() {

		if (selectedCountry == null) {
			return;
		}
		CountryMetricsEntity countryMetricsEntity = countryMetricsDao.findByCountryAndDay(selectedCountry, currentDay);

		int xData = countryMetricsEntity.getDay();
		double yData = countryMetricsEntity.getIndividualBudget();

		updateLineGraph("Individual Budget/Day", xData, yData);
	}

	@FXML
	private void handleQuantityButton() {

		if (selectedResource == null) {
			return;
		}
		List<ResourceMetricsEntity> resourceMetricsEntities =
				resourceMetricsDao.findByResource(selectedCountry, selectedResource);

		List<Integer> xData = resourceMetricsEntities.stream().map(ResourceMetricsEntity::getDay).toList();
		List<Integer> yData = resourceMetricsEntities.stream().map(ResourceMetricsEntity::getQuantity).toList();

		drawLineGraph("Quantity/Day", xData, yData);
	}

	private void updateQuantityGraph() {

		if (selectedResource == null) {
			return;
		}
		ResourceMetricsEntity resourceMetricsEntity =
				resourceMetricsDao.findByResourceAndDay(selectedCountry, selectedResource, currentDay);

		int xData = resourceMetricsEntity.getDay();
		int yData = resourceMetricsEntity.getQuantity();

		updateLineGraph("Quantity/Day", xData, yData);
	}

	@FXML
	private void handleValueButton() {

		if (selectedResource == null) {
			return;
		}
		List<ResourceMetricsEntity> resourceMetricsEntities =
				resourceMetricsDao.findByResource(selectedCountry, selectedResource);

		List<Integer> xData = resourceMetricsEntities.stream().map(ResourceMetricsEntity::getDay).toList();
		List<Double> yData = resourceMetricsEntities.stream().map(ResourceMetricsEntity::getValue).toList();

		drawLineGraph("Value/Day", xData, yData);

	}

	private void updateValueGraph() {

		if (selectedResource == null) {
			return;
		}
		ResourceMetricsEntity resourceMetricsEntity =
				resourceMetricsDao.findByResourceAndDay(selectedCountry, selectedResource, currentDay);

		int xData = resourceMetricsEntity.getDay();
		double yData = resourceMetricsEntity.getValue();

		updateLineGraph("Value/Day", xData, yData);
	}

	@FXML
	private void handleProductionCostButton() {

		if (selectedResourceNode == null) {
			return;
		}

		List<ResourceNodeMetricsEntity> resourceNodeMetricsEntities =
				resourceNodeMetricsDao.findByResourceNode(selectedCountry, selectedResourceNode);

		List<Integer> xData = resourceNodeMetricsEntities.stream().map(ResourceNodeMetricsEntity::getDay).toList();
		List<Double> yData =
				resourceNodeMetricsEntities.stream().map(ResourceNodeMetricsEntity::getProductionCost).toList();

		drawLineGraph("Production Cost/Day", xData, yData);
	}

	private void updateProductionCostGraph() {

		if (selectedResourceNode == null) {
			return;
		}
		ResourceNodeMetricsEntity resourceNodeMetricsEntity =
				resourceNodeMetricsDao.findByResourceNodeAndDay(selectedCountry, selectedResourceNode, currentDay);

		int xData = resourceNodeMetricsEntity.getDay();
		double yData = resourceNodeMetricsEntity.getProductionCost();

		updateLineGraph("Production Cost/Day", xData, yData);
	}

	@FXML
	private void handleMaxCapacityButton() {

		if (selectedResourceNode == null) {
			return;
		}
		List<ResourceNodeMetricsEntity> resourceNodeMetricsEntities =
				resourceNodeMetricsDao.findByResourceNode(selectedCountry, selectedResourceNode);

		List<Integer> xData = resourceNodeMetricsEntities.stream().map(ResourceNodeMetricsEntity::getDay).toList();
		List<Integer> yData =
				resourceNodeMetricsEntities.stream().map(ResourceNodeMetricsEntity::getMaxCapacity).toList();

		drawLineGraph("Max Capacity/Day", xData, yData);
	}

	private void updateMaxCapacityGraph() {

		if (selectedResourceNode == null) {
			return;
		}
		ResourceNodeMetricsEntity resourceNodeMetricsEntity =
				resourceNodeMetricsDao.findByResourceNodeAndDay(selectedCountry, selectedResourceNode, currentDay);

		int xData = resourceNodeMetricsEntity.getDay();
		int yData = resourceNodeMetricsEntity.getMaxCapacity();

		updateLineGraph("Max Capacity/Day", xData, yData);
	}

	@FXML
	private void handleTierButton() {

		if (selectedResourceNode == null) {
			return;
		}
		List<ResourceNodeMetricsEntity> resourceNodeMetricsEntities =
				resourceNodeMetricsDao.findByResourceNode(selectedCountry, selectedResourceNode);

		List<Integer> xData = resourceNodeMetricsEntities.stream().map(ResourceNodeMetricsEntity::getDay).toList();
		List<Integer> yData = resourceNodeMetricsEntities.stream().map(ResourceNodeMetricsEntity::getTier).toList();

		drawLineGraph("Tier/Day", xData, yData);
	}

	private void updateTierGraph() {

		if (selectedResourceNode == null) {
			return;
		}
		ResourceNodeMetricsEntity resourceNodeMetricsEntity =
				resourceNodeMetricsDao.findByResourceNodeAndDay(selectedCountry, selectedResourceNode, currentDay);

		int xData = resourceNodeMetricsEntity.getDay();
		int yData = resourceNodeMetricsEntity.getTier();

		updateLineGraph("Tier/Day", xData, yData);
	}
}