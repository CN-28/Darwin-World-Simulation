package agh.ics.oop;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Plant implements IMapElement {
    private final Vector2d position;
    private static int plantEnergy;
    private ImageView imageView;

    public Plant (Vector2d plantPosition){
        this.position = plantPosition;

        try {
            this.imageView = new ImageView(new Image(new FileInputStream("src/main/resources/plant.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
