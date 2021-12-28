package agh.ics.oop.Maps;

import agh.ics.oop.MapElements.Animal;
import agh.ics.oop.MapElements.Vector2d;

public interface IWorldMap {

    boolean canMoveTo(Vector2d position);

    boolean place(Animal animal);

    boolean isOccupied(Vector2d position);

    Object objectAt(Vector2d position);

    void placePlants();

    String getNameOfCSV();
}
