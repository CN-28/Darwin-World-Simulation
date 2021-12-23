package agh.ics.oop;

public class UnboundedMap extends AbstractWorldMap {

    @Override
    public boolean canMoveTo(Vector2d position) {
        if (position.x < 0 || position.x >= width)
            position.x = (width + position.x) % width;
        if (position.y < 0 || position.y >= height)
            position.y = (height + position.y) % height;
        return true;
    }
}
