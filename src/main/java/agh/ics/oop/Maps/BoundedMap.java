package agh.ics.oop.Maps;

import agh.ics.oop.MapElements.Vector2d;

public class BoundedMap extends AbstractWorldMap {

    public boolean canMoveTo(Vector2d position) {
        return position.precedes(this.upperRight) && position.follows(this.lowerLeft);
    }

    public String getNameOfCSV(){
        return "simulation2Data.csv";
    }
}
