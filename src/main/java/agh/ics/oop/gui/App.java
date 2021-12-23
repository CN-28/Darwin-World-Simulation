package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application implements IAnimalMoveObserver {
    private GridPane grid;
    private UnboundedMap map;
    private SimulationEngine engine;
    private Scene scene;
    private Thread thread;
    private final static int width = 36;
    private final static int height = 36;
    public void init(){
        try {
            AbstractWorldMap.setWidthHeight(20, 20);
            AbstractWorldMap.setNumberOfAnimalsAtStart(20);
            AbstractWorldMap.setInitialValues();
            this.map = new UnboundedMap();
            this.engine = new SimulationEngine(map);
            this.engine.addObserver(this);
            this.grid = new GridPane();
            this.grid.setLayoutX(5);
            this.grid.setLayoutY(5);
            this.scene = new Scene(this.grid, 760, 760);
            this.thread = new Thread(this.engine);
            updateGui(this.map, this.grid);
        }
        catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Darwin World");
        primaryStage.setScene(scene);
        primaryStage.show();
        this.thread.start();
    }


    public void updateGui(AbstractWorldMap map, GridPane grid){
        GuiElementBox guiElementBox;
        Vector2d lowerLeft = this.map.lowerLeft;
        Vector2d upperRight = this.map.upperRight;

        grid.setGridLinesVisible(false);
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();
        grid.setGridLinesVisible(true);

        for (int i = lowerLeft.x; i <= upperRight.x; i++)
            grid.getColumnConstraints().add(new ColumnConstraints(width));


        for (int i = upperRight.y; i >= lowerLeft.y; i--)
            grid.getRowConstraints().add(new RowConstraints(height));


        // add animals labels
        for (Vector2d animalPos : this.map.animals.keySet()){
            Animal animal = this.map.animals.get(animalPos).first();
            guiElementBox = new GuiElementBox(animal);

            grid.add(guiElementBox.vBox, animalPos.x - lowerLeft.x, upperRight.y - animalPos.y);
        }

        // add grass labels
        for (Vector2d plantPos : this.map.plants.keySet()){
            Plant plant = this.map.plants.get(plantPos);
            if (map.objectAt(plantPos) instanceof Plant){
                guiElementBox = new GuiElementBox(plant);
                grid.add(guiElementBox.vBox, plantPos.x - lowerLeft.x, upperRight.y - plantPos.y);
            }
        }
    }

    public void animalMoved() {
        updateGui(this.map, this.grid);
    }
}
