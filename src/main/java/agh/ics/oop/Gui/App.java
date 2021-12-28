package agh.ics.oop.Gui;

import agh.ics.oop.Engine.SimulationEngine;
import agh.ics.oop.MapElements.Animal;
import agh.ics.oop.MapElements.Plant;
import agh.ics.oop.MapElements.Vector2d;
import agh.ics.oop.Maps.AbstractWorldMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application implements IGuiUpdateObserver {
    protected AbstractWorldMap map1, map2;
    protected SimulationEngine engine1, engine2;
    protected Thread thread1, thread2;
    private Scene scene;
    protected static int width;
    protected static int height;


    public void init(){
        ConfigPageElements configPageElements = new ConfigPageElements(this);
        this.scene = new Scene(configPageElements.hBox, 1550, 790);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Darwin World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void markDominant(AbstractWorldMap map, GridPane grid){
        grid.getChildren().clear();

        for (int i = 0; i <= map.upperRight.x; i++){
            for (int j = 0; j <= map.upperRight.y; j++)
                fillCell(i, j, grid, map);
        }

        for (Vector2d animalPos : map.animals.keySet()){
            Animal animalOnMap = map.animals.get(animalPos).first();
            for (Animal animal : map.animals.get(animalPos)){
                String genotypeStr = "";
                StringBuilder genotypeResBuilder = new StringBuilder(genotypeStr);
                for (int num : animal.genes.genotype)
                    genotypeResBuilder.append(num).append(" ");
                genotypeStr = genotypeResBuilder.toString();

                if (map.dominantGenotype.equals(genotypeStr)){
                    Pane cell = new Pane();
                    cell.setPrefSize(width, height);
                    cell.setMaxSize(width, height);
                    cell.setMinSize(width, height);

                    if (map.upperRight.y - animalPos.y == 0 && animalPos.x == map.upperRight.x)
                        cell.setStyle("-fx-background-color: black, blue; -fx-background-insets: 0, 1 1 1 1;");
                    else if (map.upperRight.y - animalPos.y == 0)
                        cell.setStyle("-fx-background-color: black, blue; -fx-background-insets: 0, 1 0 1 1;");
                    else if (animalPos.x == map.upperRight.x)
                        cell.setStyle("-fx-background-color: black, blue; -fx-background-insets: 0, 0 1 1 1;");
                    else
                        cell.setStyle("-fx-background-color: black, blue; -fx-background-insets: 0, 0 0 1 1;");

                    grid.add(cell, animalPos.x, map.upperRight.y - animalPos.y);
                }
            }
            grid.add(new GuiElementBox(animalOnMap, map, width).vBox, animalPos.x, map.upperRight.y - animalPos.y);
        }

        // add grass labels
        for (Vector2d plantPos : map.plants.keySet()){
            Plant plant = map.plants.get(plantPos);
            if (map.objectAt(plantPos) instanceof Plant)
                grid.add(new GuiElementBox(plant, map, width).vBox, plantPos.x, map.upperRight.y - plantPos.y);
        }
    }

    public void updateGui(AbstractWorldMap map, GridPane grid){
        Platform.runLater(() -> {
            grid.getChildren().clear();

            for (int i = 0; i <= map.upperRight.x; i++){
                for (int j = 0; j <= map.upperRight.y; j++)
                    fillCell(i, j, grid, map);
            }
        });

        // add animals labels
        for (Vector2d animalPos : map.animals.keySet()){
            Animal animal = map.animals.get(animalPos).first();
            Platform.runLater(() -> grid.add(new GuiElementBox(animal, map, width).vBox, animalPos.x, map.upperRight.y - animalPos.y));
        }

        // add grass labels
        for (Vector2d plantPos : map.plants.keySet()){
            Plant plant = map.plants.get(plantPos);
            if (map.objectAt(plantPos) instanceof Plant)
                Platform.runLater(() -> grid.add(new GuiElementBox(plant, map, width).vBox, plantPos.x, map.upperRight.y - plantPos.y));
        }
    }

    public void updateStats(AbstractWorldMap map){
        Platform.runLater(() -> {
            if (map.numberOfAnimals == 0)
                map.guiElements.averageEnergyLabel.setText("Average energy of animals: 0");
            else
                map.guiElements.averageEnergyLabel.setText("Average energy of animals: " + String.format("%.2f", map.averageEnergy));
            if (map.averageLifeSpan != 0)
                map.guiElements.averageLifeSpanLabel.setText("Average life span of animals: " + String.format("%.2f", map.averageLifeSpan));
            map.guiElements.averageNumberOfChildrenLabel.setText("Average number of children: " + String.format("%.2f", map.averageNumberOfChildren));
            map.guiElements.dominantLabel.setText("Dominant genotype: " + map.dominantGenotype);
            if (map.trackedAnimal != null){
                map.guiElements.trackedAnimalChildrenLabel.setText("Number of children of tracked animal: " + map.numberOfChildren);
                map.guiElements.trackedAnimalDescendantsLabel.setText("Number of descendants of tracked animal: " + map.numberOfDescendants);
            }
            if (map.trackedAnimalDeath != -1){
                map.guiElements.trackedAnimalDeathDayLabel.setText("Tracked animal died at: " + map.trackedAnimalDeath);
                map.trackedAnimalDeath = -1;
            }
        });
    }

    public void fillCell(int i, int j, GridPane grid, AbstractWorldMap map) {
        Pane cell = new Pane();
        cell.setPrefSize(width, height);
        cell.setMaxSize(width, height);
        cell.setMinSize(width, height);
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

        grid.add(cell, i, map.upperRight.y - j);
    }

    public void updateNeeded(AbstractWorldMap map) {
        updateGui(map, map.guiElements.grid);
    }
}