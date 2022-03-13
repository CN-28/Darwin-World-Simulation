package agh.ics.oop.MapElements;

import agh.ics.oop.Gui.App;
import agh.ics.oop.Gui.GuiElementBox;
import agh.ics.oop.Maps.AbstractWorldMap;
import agh.ics.oop.Maps.IWorldMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import static agh.ics.oop.MapElements.MapDirection.*;

public class Animal implements IMapElement {
    private Vector2d position;
    private MapDirection orientation;
    public Genes genes;
    private int energy;
    private final IWorldMap map;
    private final static List<MapDirection> orientationsList = Arrays.asList(NORTH, SOUTH, WEST, EAST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST);
    private final LinkedHashSet<IPositionChangeObserver> observers = new LinkedHashSet<>();
    private static int moveEnergy;
    private static int startEnergy;
    private final ImageView[] imageViews = new ImageView [12];
    public int birthDay;
    public int numberOfChildren = 0;
    public boolean isDescendant = false;
    public static int size = 30;
    public GuiElementBox box;
    private static final Image[] images = new Image[12];

    static {
        try {
            images[0] = new Image(new FileInputStream("src/main/resources/up.png"), size, size, false, false);
            images[1] = new Image(new FileInputStream("src/main/resources/up_right.png"), size, size, false, false);
            images[2] = new Image(new FileInputStream("src/main/resources/right.png"), size, size, false, false);
            images[3] = new Image(new FileInputStream("src/main/resources/down_right.png"), size, size, false, false);
            images[4] = new Image(new FileInputStream("src/main/resources/down.png"), size, size, false, false);
            images[5] = new Image(new FileInputStream("src/main/resources/down_left.png"), size, size, false, false);
            images[6] = new Image(new FileInputStream("src/main/resources/left.png"), size, size, false, false);
            images[7] = new Image(new FileInputStream("src/main/resources/up_left.png"), size, size, false, false);
            images[8] = new Image(new FileInputStream("src/main/resources/25.png"), size, ((double) size) / 5, false, false);
            images[9] = new Image(new FileInputStream("src/main/resources/50.png"), size, ((double) size) / 5, false, false);
            images[10] = new Image(new FileInputStream("src/main/resources/75.png"), size, ((double) size) / 5, false, false);
            images[11] = new Image(new FileInputStream("src/main/resources/100.png"), size, ((double) size) / 5, false, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Animal(AbstractWorldMap map, Vector2d position, int birthDay) {
        this.birthDay = birthDay;
        for (int i = 0; i < 12; i++)
            imageViews[i] = new ImageView(images[i]);

        this.map = map;
        this.energy = startEnergy;
        this.position = position;
        this.genes = new Genes();
        Random rand = new Random();
        this.orientation = orientationsList.get(rand.nextInt(orientationsList.size()));
        this.box = new GuiElementBox(this, map, App.width);
    }

    public Animal(AbstractWorldMap map, Animal parent1, Animal parent2, int birthDay) {
        this.birthDay = birthDay;
        for (int i = 0; i < 12; i++)
            imageViews[i] = new ImageView(images[i]);

        this.map = map;
        this.energy = (int) (((double) parent1.getEnergy()) / 4) + (int) (((double) parent2.getEnergy()) / 4);
        this.position = parent1.position;
        this.genes = new Genes(parent1, parent2);
        Random rand = new Random();
        this.orientation = orientationsList.get(rand.nextInt(orientationsList.size()));
        this.box = new GuiElementBox(this, map, App.width);
    }

    public Vector2d getPosition() {
        return this.position;
    }


    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Object a){
        for (IPositionChangeObserver observer : observers)
            observer.positionChanged(oldPosition, newPosition, a);
    }

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    public void move(MoveDirection direction){
        Vector2d newPos = this.position;
        switch (direction) {
            case RIGHT -> this.orientation = this.orientation.next();
            case LEFT -> this.orientation = this.orientation.previous();
            case FORWARD -> newPos = this.position.add(this.orientation.toUnitVector());
            case BACKWARD -> newPos = this.position.subtract(this.orientation.toUnitVector());
        }

        if (!(this.position.equals(newPos)) && this.map.canMoveTo(newPos)){
            this.positionChanged(this.position, newPos, this);
            this.position = newPos;
        }
    }

    public Animal copyAnimal(AbstractWorldMap map, Vector2d position) {
        Animal animal = new Animal(map, position, this.birthDay);
        animal.genes = this.genes;
        return animal;
    }

    public boolean isDead(){
        return this.energy < moveEnergy;
    }

    public static void setMoveEnergy(int moveEnergyValue){
        moveEnergy = moveEnergyValue;
    }

    public static void setStartEnergy(int startEnergyValue){
        startEnergy = startEnergyValue;
    }

    public static int getStartEnergy(){
        return startEnergy;
    }

    public static int getMoveEnergy() {
        return moveEnergy;
    }

    public void decreaseEnergy(int energyValue){
        this.energy -= energyValue;
    }

    public void increaseEnergy(int increaseNumber){
        this.energy += increaseNumber;
    }

    public int getEnergy(){
        return this.energy;
    }

    public ImageView getPicture(){
        return switch(this.orientation) {
            case NORTH -> imageViews[0];
            case NORTH_EAST -> imageViews[1];
            case EAST -> imageViews[2];
            case SOUTH_EAST -> imageViews[3];
            case SOUTH -> imageViews[4];
            case SOUTH_WEST -> imageViews[5];
            case WEST -> imageViews[6];
            case NORTH_WEST -> imageViews[7];
        };
    }

    public ImageView getEnergyPicture(int energy){
        double energyRatio = ((double) energy) / Animal.startEnergy;
        if (energyRatio <= 0.25) return imageViews[8];
        else if (energyRatio <= 0.5) return imageViews[9];
        else if (energyRatio <= 0.75) return imageViews[10];
        return imageViews[11];
    }
}
