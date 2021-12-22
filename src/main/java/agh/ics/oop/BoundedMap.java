package agh.ics.oop;

public class BoundedMap extends AbstractWorldMap {

    BoundedMap(int width, int height) {
        super(width, height);
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.precedes(this.upperRight) && position.follows(this.lowerLeft);
    }
}
