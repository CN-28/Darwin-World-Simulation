package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class App extends Application implements IAnimalMoveObserver {
    private GridPane grid, grid2;
    private AbstractWorldMap map, map2;
    private SimulationEngine engine, engine2;
    private Scene scene;
    private Thread thread, thread2;
    private HBox hBox;
    private Button button, button2;
    private final static int width = 36;
    private final static int height = 36;

    public void init(){
        try {
            // settings
            AbstractWorldMap.setWidthHeight(15, 15);
            AbstractWorldMap.setNumberOfAnimalsAtStart(5);
            AbstractWorldMap.setInitialValues();
            Animal.setMoveEnergy(1);
            Animal.setStartEnergy(80);
            Plant.setPlantEnergy(20);

            this.map = new UnboundedMap();
            this.map2 = new BoundedMap();
            this.engine = new SimulationEngine(map);
            this.engine.addObserver(this);
            this.engine.setMagical(true);
            this.engine2 = new SimulationEngine(map2);
            this.engine2.addObserver(this);

            this.grid = new GridPane();
            this.grid2 = new GridPane();

            this.hBox = new HBox();
            this.grid.setLayoutX(10);
            this.grid.setLayoutY(10);
            this.button = new Button("START/STOP");
            this.button2 = new Button("START/STOP");
            this.thread = new Thread(this.engine);
            this.thread2 = new Thread(this.engine2);
            this.button.setOnAction(e -> this.map.running = !this.map.running);
            this.button2.setOnAction(e -> this.map2.running = !this.map2.running);
            this.hBox.getChildren().addAll(button, this.grid, this.button2, this.grid2);
            this.hBox.setLayoutY(15);
            this.hBox.setSpacing(15);
            updateGui(this.map2, this.grid2);
            updateGui(this.map, this.grid);
            this.scene = new Scene(this.hBox, 1400, 760);


        }
        catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void start(Stage primaryStage) {
        this.thread.start();
        this.thread2.start();
        primaryStage.setTitle("Darwin World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void updateGui(AbstractWorldMap map, GridPane grid){
        final ArrayList<VBox> toAdd = new ArrayList<>();
        final ArrayList<Vector2d> toAddPos = new ArrayList<>();
        Vector2d lowerLeft = map.lowerLeft;
        Vector2d upperRight = map.upperRight;

        // add animals labels
        for (Vector2d animalPos : map.animals.keySet()){
            Animal animal = map.animals.get(animalPos).first();
            Platform.runLater(() -> {
                toAdd.add(new GuiElementBox(animal).vBox);
                toAddPos.add(animalPos);
            });
        }

        // add grass labels
        for (Vector2d plantPos : map.plants.keySet()){
            Plant plant = map.plants.get(plantPos);
            if (map.objectAt(plantPos) instanceof Plant){
                Platform.runLater(() ->{
                    toAdd.add(new GuiElementBox(plant).vBox);
                    toAddPos.add(plantPos);
                });
            }
        }

        Platform.runLater(() -> {
            grid.setGridLinesVisible(false);
            grid.getChildren().clear();
            grid.getColumnConstraints().clear();
            grid.getRowConstraints().clear();

            for (int i = lowerLeft.x; i <= upperRight.x; i++)
                grid.getColumnConstraints().add(new ColumnConstraints(width));

            for (int i = upperRight.y; i >= lowerLeft.y; i--)
                grid.getRowConstraints().add(new RowConstraints(height));


            for (int i = 0; i < toAdd.size(); i++)
                grid.add(toAdd.get(i), toAddPos.get(i).x - lowerLeft.x, upperRight.y -  toAddPos.get(i).y);

            grid.setGridLinesVisible(true);
        });


    }

    public void animalMoved(AbstractWorldMap map) {
        if (map == this.map2)
            updateGui(map, this.grid2);
        else
            updateGui(map, this.grid);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}