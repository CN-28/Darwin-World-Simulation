package agh.ics.oop.Gui;

import agh.ics.oop.Maps.AbstractWorldMap;
import agh.ics.oop.MapElements.Animal;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class MapGuiElements {
    protected GridPane grid;
    protected VBox statsBox;
    protected HBox buttonsBox;
    protected Button startButton, stopButton, showDominantsButton, saveToCSVButton;
    protected Label averageEnergyLabel, averageLifeSpanLabel, averageNumberOfChildrenLabel, dominantLabel, trackedAnimalLabel, trackedAnimalChildrenLabel, trackedAnimalDescendantsLabel, trackedAnimalDeathDayLabel;
    protected AbstractWorldMap map;
    public Chart chart;

    public MapGuiElements(AbstractWorldMap map, App app) {
        this.grid = new GridPane();
        this.map = map;
        this.statsBox = new VBox();
        this.statsBox.setAlignment(Pos.CENTER);
        this.startButton = new Button("START");
        this.startButton.setOnAction(e -> this.map.running = true);
        this.stopButton = new Button("STOP");
        this.stopButton.setOnAction(e -> this.map.running = false);
        this.showDominantsButton = new Button("SHOW ALL DOMINANTS");
        this.showDominantsButton.setOnAction(e -> {
            if (!this.map.running)
                app.markDominant(this.map, this.grid);
        });

        this.saveToCSVButton = new Button("SAVE TO CSV");
        this.saveToCSVButton.setOnAction(e -> {
            if (!this.map.running)
                this.map.csvWriter.writeToCSV();
        });

        this.averageEnergyLabel = new Label("Average energy of animals: " + Animal.getStartEnergy());
        this.averageLifeSpanLabel = new Label("Average life span of animals: 0");
        this.averageNumberOfChildrenLabel = new Label("Average number of children: 0");
        this.dominantLabel = new Label("Dominant genotype: undefined");
        this.trackedAnimalLabel = new Label();
        this.trackedAnimalChildrenLabel = new Label();
        this.trackedAnimalDescendantsLabel = new Label();
        this.trackedAnimalDeathDayLabel = new Label();
        this.buttonsBox = new HBox();
        this.buttonsBox.getChildren().addAll(this.startButton, this.stopButton, this.showDominantsButton, this.saveToCSVButton);
        this.buttonsBox.setSpacing(10);
        this.buttonsBox.setAlignment(Pos.CENTER);
        this.chart = new Chart();
        this.statsBox.getChildren().addAll(this.buttonsBox, this.chart.lineChart, this.averageEnergyLabel, this.averageLifeSpanLabel, this.averageNumberOfChildrenLabel, this.dominantLabel, this.trackedAnimalLabel, this.trackedAnimalChildrenLabel, this.trackedAnimalDescendantsLabel, this.trackedAnimalDeathDayLabel);
        this.statsBox.setStyle("-fx-border-color: lightblue");
        this.statsBox.setPadding(new Insets(10, 0, 0, 0));

    }
}
