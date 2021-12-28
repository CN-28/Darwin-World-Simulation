package agh.ics.oop.MapElements;

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

    static {
        try {
            image = new Image (new FileInputStream("src/main/resources/plant.png"), size, size, false, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Plant (Vector2d plantPosition){
        this.position = plantPosition;
        this.imageView = new ImageView(image);
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
