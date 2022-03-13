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
    public static int width;
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

    public void markDominant(AbstractWorldMap map){
        for (Vector2d animalPos : map.animals.keySet()){
            Animal animalOnMap = map.animals.get(animalPos).first();
            for (Animal animal : map.animals.get(animalPos)){
                String genotypeStr = "";
                StringBuilder genotypeResBuilder = new StringBuilder(genotypeStr);
                for (int num : animal.genes.genotype)
                    genotypeResBuilder.append(num).append(" ");
                genotypeStr = genotypeResBuilder.toString();

                if (map.dominantGenotype.equals(genotypeStr)){
                    map.guiElements.gridNodes[map.upperRight.y - animalPos.y][animalPos.x].getChildren().clear();
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

                    map.guiElements.gridNodes[map.upperRight.y - animalPos.y][animalPos.x].getChildren().add(cell);
                    map.guiElements.gridNodes[map.upperRight.y - animalPos.y][animalPos.x].getChildren().add(animalOnMap.box.vBox);
                }
            }
        }
    }

    public void renderMapsAtStart(AbstractWorldMap map, StackPane[][] gridNodes){
        // add animals labels
        for (Vector2d animalPos : map.animals.keySet()){
            Platform.runLater(() ->{
                Animal animal = map.animals.get(animalPos).first();
                Vector2d pos = new Vector2d(animalPos.x, map.upperRight.y - animalPos.y);
                map.guiElements.boxNodes.put(pos, animal.box.vBox);
                gridNodes[map.upperRight.y - animalPos.y][animalPos.x].getChildren().add(animal.box.vBox);
            });
        }

        // add grass labels
        for (Vector2d plantPos : map.plants.keySet()){
            Platform.runLater(() -> {
                Plant plant = map.plants.get(plantPos);
                if (map.objectAt(plantPos) instanceof Plant){
                    Vector2d pos = new Vector2d(plantPos.x, map.upperRight.y - plantPos.y);
                    map.guiElements.boxNodes.put(pos, plant.box.vBox);
                    gridNodes[map.upperRight.y - plantPos.y][plantPos.x].getChildren().add(plant.box.vBox);
                }
            });
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

}