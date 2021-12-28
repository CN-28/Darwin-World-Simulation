package agh.ics.oop.Maps;

import agh.ics.oop.MapElements.Vector2d;

public class UnboundedMap extends AbstractWorldMap {

    public boolean canMoveTo(Vector2d position) {
        if (position.x < 0 || position.x >= width)
            position.x = (width + position.x) % width;
        if (position.y < 0 || position.y >= height)
            position.y = (height + position.y) % height;
        return true;
    }

    public String getNameOfCSV(){
        return "simulation1Data.csv";
    }
}
