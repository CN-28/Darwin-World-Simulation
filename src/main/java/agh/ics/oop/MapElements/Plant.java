package agh.ics.oop.MapElements;

import agh.ics.oop.Gui.App;
import agh.ics.oop.Gui.GuiElementBox;
import agh.ics.oop.Maps.AbstractWorldMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Plant implements IMapElement {
    private final Vector2d position;
    private static int plantEnergy;
    private final ImageView imageView;
    private static final int size = 20;
    private static Image image;
    public GuiElementBox box;

    static {
        try {
            image = new Image (new FileInputStream("src/main/resources/plant.png"), size, size, false, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Plant (Vector2d plantPosition, AbstractWorldMap map){
        this.position = plantPosition;
        this.imageView = new ImageView(image);
        this.box = new GuiElementBox(this, map, App.width);
    }

    public ImageView getPicture() {
        return this.imageView;
    }

    public Vector2d getPosition(){
        return this.position;
    }

    public static void setPlantEnergy(int plantEnergyValue){
        plantEnergy = plantEnergyValue;
    }

    public static int getPlantEnergy(){
        return plantEnergy;
    }

    public String toString(){
        return "*";
    }
}
