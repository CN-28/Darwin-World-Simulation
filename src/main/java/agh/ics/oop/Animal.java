package agh.ics.oop;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import static agh.ics.oop.MapDirection.*;

public class Animal {
    private Vector2d position;
    private MapDirection orientation;
    private Genes genes;
    protected int energy;
    private final IWorldMap map;
    private final static List<MapDirection> orientationsList = Arrays.asList(NORTH, SOUTH, WEST, EAST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST);
    private final LinkedHashSet<IPositionChangeObserver> observers = new LinkedHashSet<>();

    public Animal(IWorldMap map, Vector2d position, int energy) {
        this.map = map;
        this.energy = energy;
        this.position = position;
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

        if (this.map.canMoveTo(newPos)){
            this.positionChanged(this.position, newPos, this);
            this.position = newPos;
        }

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
