package agh.ics.oop;

public class Plant {
    private final Vector2d position;

    public Plant (Vector2d plantPosition){
        this.position = plantPosition;
    }

    public Vector2d getPosition(){
        return this.position;
    }

    public String toString(){
        return "*";
    }
}
