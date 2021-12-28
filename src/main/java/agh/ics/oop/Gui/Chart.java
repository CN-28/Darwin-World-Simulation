package agh.ics.oop.Gui;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


public class Chart {
    protected final NumberAxis xAxis = new NumberAxis();
    protected final NumberAxis yAxis = new NumberAxis();
    protected final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    protected final XYChart.Series animalsSeries = new XYChart.Series();
    protected final XYChart.Series plantsSeries = new XYChart.Series();

    public Chart(){
        lineChart.setPrefWidth(400);
        lineChart.setPrefHeight(250);
        lineChart.setMaxHeight(250);
        lineChart.setMaxWidth(400);
        xAxis.setLabel("Epoch");
        animalsSeries.setName("Number of animals alive");
        plantsSeries.setName("Number of plants on the map");
        lineChart.setCreateSymbols(false);
        this.lineChart.getData().addAll(animalsSeries, plantsSeries);
    }

    public void updateChart(int epoch, int numberOfAnimals, int numberOfPlants){
        Platform.runLater(() -> {
            animalsSeries.getData().add(new XYChart.Data(epoch, numberOfAnimals));
            plantsSeries.getData().add(new XYChart.Data(epoch, numberOfPlants));
        });
    }
}
