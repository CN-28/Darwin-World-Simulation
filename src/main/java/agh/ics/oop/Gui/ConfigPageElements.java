package agh.ics.oop.Gui;

import agh.ics.oop.Engine.SimulationEngine;
import agh.ics.oop.MapElements.Animal;
import agh.ics.oop.MapElements.Plant;
import agh.ics.oop.Maps.AbstractWorldMap;
import agh.ics.oop.Maps.BoundedMap;
import agh.ics.oop.Maps.UnboundedMap;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ConfigPageElements {
    InputConfigBox mapWidthBox, mapHeightBox, animalsAtStartBox, startEnergyBox, moveEnergyBox, plantEnergyBox, jungleRatioBox, moveDelayBox;
    private final RadioButton magical1;
    private final RadioButton magical2;
    protected HBox hBox;

    public ConfigPageElements(App app){
        this.mapWidthBox = new InputConfigBox(5, 50, 14, "Map width");
        this.mapHeightBox = new InputConfigBox(5, 50, 14, "Map height");
        this.animalsAtStartBox = new InputConfigBox(1, 100, 10, "Number of animals at start");
        this.startEnergyBox = new InputConfigBox(1, 200, 50, "Animal starting energy");
        this.moveEnergyBox = new InputConfigBox(1, 200, 1, "Animal moving energy");
        this.plantEnergyBox = new InputConfigBox(1, 200, 20, "Plant energy value");
        this.jungleRatioBox = new InputConfigBox(0.1, 0.8, 0.6, "Jungle ratio");
        this.moveDelayBox = new InputConfigBox(5, 1000, 30, "Move delay");
        ToggleGroup group1 = new ToggleGroup();
        this.magical1 = new RadioButton("Magical");
        RadioButton standard1 = new RadioButton("Standard");
        ToggleGroup group2 = new ToggleGroup();
        this.magical2 = new RadioButton("Magical");
        RadioButton standard2 = new RadioButton("Standard");
        this.magical1.setToggleGroup(group1);
        standard1.setToggleGroup(group1);
        this.magical2.setToggleGroup(group2);
        standard2.setToggleGroup(group2);

        HBox map1ModeBox = new HBox();
        HBox map2ModeBox = new HBox();
        map1ModeBox.setSpacing(10);
        map2ModeBox.setSpacing(10);

        Label map1ModeLabel = new Label("Unbounded map mode: ");
        Label map2ModeLabel = new Label("Bounded map mode: ");
        map1ModeBox.getChildren().addAll(map1ModeLabel, this.magical1, standard1);
        map2ModeBox.getChildren().addAll(map2ModeLabel, this.magical2, standard2);

        Button applyConfigButton = new Button("Apply config");
        VBox configBox = new VBox();
        configBox.getChildren().addAll(mapWidthBox.hBox, mapHeightBox.hBox, animalsAtStartBox.hBox, startEnergyBox.hBox, moveEnergyBox.hBox, plantEnergyBox.hBox, jungleRatioBox.hBox, moveDelayBox.hBox, map1ModeBox, map2ModeBox, applyConfigButton);
        configBox.setAlignment(Pos.CENTER);
        configBox.setSpacing(10);

        applyConfigButton.setOnAction(e1 -> {
            // settings
            AbstractWorldMap.setWidthHeight(mapWidthBox.getIntValue(), mapHeightBox.getIntValue());
            AbstractWorldMap.setNumberOfAnimalsAtStart(animalsAtStartBox.getIntValue());
            AbstractWorldMap.setJungleRatio(jungleRatioBox.getDoubleValue());
            AbstractWorldMap.setMoveDelay(moveDelayBox.getIntValue());
            AbstractWorldMap.setInitialValues();
            Animal.setMoveEnergy(moveEnergyBox.getIntValue());
            Animal.setStartEnergy(startEnergyBox.getIntValue());
            Plant.setPlantEnergy(plantEnergyBox.getIntValue());
            App.width = Math.min(504 / AbstractWorldMap.width, 504 / AbstractWorldMap.height);
            App.height = App.width;

            app.map1 = new UnboundedMap();
            app.map2 = new BoundedMap();
            app.map1.guiElements = new MapGuiElements(app.map1, app);
            app.map2.guiElements = new MapGuiElements(app.map2, app);
            app.engine1 = new SimulationEngine(app.map1);
            app.engine1.addObserver(app);

            app.engine2 = new SimulationEngine(app.map2);
            app.engine2.addObserver(app);

            if (magical1.isSelected())
                app.engine1.setMagical(true);
            if (magical2.isSelected())
                app.engine2.setMagical(true);

            app.thread1 = new Thread(app.engine1);
            app.thread2 = new Thread(app.engine2);

            VBox setUpBox = new VBox();
            setUpBox.setAlignment(Pos.CENTER);
            setUpBox.getChildren().addAll(app.map1.guiElements.statsBox, app.map2.guiElements.statsBox);

            HBox gridsBox = new HBox();
            VBox grid1 = new VBox();
            VBox grid2 = new VBox();
            grid1.getChildren().add(app.map1.guiElements.grid);
            grid2.getChildren().add(app.map2.guiElements.grid);
            grid1.setAlignment(Pos.CENTER);
            grid2.setAlignment(Pos.CENTER);

            gridsBox.setSpacing(13);
            gridsBox.getChildren().addAll(grid1, grid2);
            gridsBox.setAlignment(Pos.CENTER);

            this.hBox.getChildren().clear();
            this.hBox.getChildren().addAll(setUpBox, gridsBox);
            this.hBox.setSpacing(30);
            app.renderMapsAtStart(app.map2, app.map2.guiElements.gridNodes);
            app.renderMapsAtStart(app.map1, app.map1.guiElements.gridNodes);

            app.thread1.start();
            app.thread2.start();
        });

        this.hBox = new HBox();
        this.hBox.getChildren().add(configBox);
        this.hBox.setAlignment(Pos.CENTER);
    }


}
