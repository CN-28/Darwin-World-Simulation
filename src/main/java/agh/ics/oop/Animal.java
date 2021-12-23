package agh.ics.oop;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import static agh.ics.oop.MapDirection.*;

public class Animal implements IMapElement {
    private Vector2d position;
    private MapDirection orientation;
    protected final Genes genes;
    private int energy;
    private final IWorldMap map;
    private final static List<MapDirection> orientationsList = Arrays.asList(NORTH, SOUTH, WEST, EAST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST);
    private final LinkedHashSet<IPositionChangeObserver> observers = new LinkedHashSet<>();
    private static int moveEnergy;
    private static int startEnergy;
    private final ArrayList<ImageView> imageViews = new ArrayList<>();

    public Animal(IWorldMap map, Vector2d position) {
        this.map = map;
        this.energy = startEnergy;
        this.position = position;
        this.genes = new Genes();
        Random rand = new Random();
        this.orientation = orientationsList.get(rand.nextInt(orientationsList.size()));

        try {
            imageViews.add(new ImageView(new Image(new FileInputStream("src/main/resources/up.png"))));
            imageViews.add(new ImageView(new Image(new FileInputStream("src/main/resources/up_right.png"))));
            imageViews.add(new ImageView(new Image(new FileInputStream("src/main/resources/right.png"))));
            imageViews.add(new ImageView(new Image(new FileInputStream("src/main/resources/down_right.png"))));
            imageViews.add(new ImageView(new Image(new FileInputStream("src/main/resources/down.png"))));
            imageViews.add(new ImageView(new Image(new FileInputStream("src/main/resources/down_left.png"))));
            imageViews.add(new ImageView(new Image(new FileInputStream("src/main/resources/left.png"))));
            imageViews.add(new ImageView(new Image(new FileInputStream("src/main/resources/up_left.png"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Animal(IWorldMap map, Animal parent1, Animal parent2) {
        this.map = map;
        this.energy = (int) (((double) parent1.getEnergy()) / 4) + (int) (((double) parent2.getEnergy()) / 4);
        this.position = parent1.position;
        this.genes = new Genes(parent1, parent2);
        Random rand = new Random();
        this.orientation = orientationsList.get(rand.nextInt(orientationsList.size()));
    }

    public MapDirection getOrientation(){
        return this.orientation;
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public boolean isAt(Vector2d position){
        return this.position.equals(position);
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
            case NORTH -> imageViews.get(0);
            case NORTH_EAST -> imageViews.get(1);
            case EAST -> imageViews.get(2);
            case SOUTH_EAST -> imageViews.get(3);
            case SOUTH -> imageViews.get(4);
            case SOUTH_WEST -> imageViews.get(5);
            case WEST -> imageViews.get(6);
            case NORTH_WEST -> imageViews.get(7);
        };
    }

    public String toString(){
        return switch(this.orientation){
            case NORTH -> "N";
            case NORTH_EAST -> "NE";
            case NORTH_WEST -> "NW";
            case SOUTH_EAST -> "SE";
            case SOUTH_WEST -> "SW";
            case EAST -> "E";
            case WEST -> "W";
            case SOUTH -> "S";
        };
    }
}
