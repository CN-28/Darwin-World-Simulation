package agh.ics.oop.Gui;

import agh.ics.oop.MapElements.Vector2d;
import agh.ics.oop.Maps.AbstractWorldMap;
import agh.ics.oop.MapElements.Animal;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.HashMap;

public class MapGuiElements {
    protected GridPane grid;
    public StackPane[][] gridNodes = new StackPane[AbstractWorldMap.height][AbstractWorldMap.width];
    public HashMap<Vector2d, VBox> boxNodes = new HashMap<>();
    protected VBox statsBox;
    protected HBox buttonsBox;
    protected Button startButton, stopButton, showDominantsButton, saveToCSVButton;
    protected Label averageEnergyLabel, averageLifeSpanLabel, averageNumberOfChildrenLabel, dominantLabel, trackedAnimalLabel, trackedAnimalChildrenLabel, trackedAnimalDescendantsLabel, trackedAnimalDeathDayLabel;
    protected AbstractWorldMap map;
    public Chart chart;

    public MapGuiElements(AbstractWorldMap map, App app) {
        this.grid = new GridPane();
        this.map = map;
        for (int i = 0; i < AbstractWorldMap.height; i++){
            for (int j = 0; j < AbstractWorldMap.width; j++){
                StackPane pane = new StackPane();
                this.grid.add(pane, j, i);
                gridNodes[i][j] = pane;
            }
        }

        for (int i = 0; i < AbstractWorldMap.height; i++){
            for (int j = 0; j < AbstractWorldMap.width; j++)
                fillCell(j, i);
        }

        this.statsBox = new VBox();
        this.statsBox.setAlignment(Pos.CENTER);
        this.startButton = new Button("START");
        this.startButton.setOnAction(e -> this.map.running = true);
        this.stopButton = new Button("STOP");
        this.stopButton.setOnAction(e -> this.map.running = false);
        this.showDominantsButton = new Button("SHOW ALL DOMINANTS");
        this.showDominantsButton.setOnAction(e -> {
            if (!this.map.running)
                app.markDominant(this.map);
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

    public void fillCell(int i, int j) {
        HBox cell = new HBox();
        cell.setPrefSize(App.width, App.height);
        cell.setMaxSize(App.width, App.height);
        cell.setMinSize(App.width, App.height);
        Vector2d pos = new Vector2d(i, j);
        if (pos.precedes(AbstractWorldMap.jungleUpperRight) && pos.follows(AbstractWorldMap.jungleLowerLeft))
            cell.setStyle("-fx-background-color: black, green; -fx-background-insets: 0, 0 0 1 1;");
        else{
            if (map.upperRight.y - j == 0 && i == map.upperRight.x)
                cell.setStyle("-fx-background-color: black, lightgreen; -fx-background-insets: 0, 1 1 1 1;");
            else if (map.upperRight.y - j == 0)
                cell.setStyle("-fx-background-color: black, lightgreen; -fx-background-insets: 0, 1 0 1 1;");
            else if (i == map.upperRight.x)
                cell.setStyle("-fx-background-color: black, lightgreen; -fx-background-insets: 0, 0 1 1 1;");
            else
                cell.setStyle("-fx-background-color: black, lightgreen; -fx-background-insets: 0, 0 0 1 1;");
        }

        gridNodes[map.upperRight.y - j][i].getChildren().add(cell);
    }
}
