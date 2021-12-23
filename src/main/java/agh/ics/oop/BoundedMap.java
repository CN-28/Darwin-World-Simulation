package agh.ics.oop;

public class BoundedMap extends AbstractWorldMap {

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.precedes(this.upperRight) && position.follows(this.lowerLeft);
    }
}
