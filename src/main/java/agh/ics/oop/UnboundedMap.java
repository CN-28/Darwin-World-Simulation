package agh.ics.oop;

public class UnboundedMap extends AbstractWorldMap {

    UnboundedMap(int width, int height) {
        super(width, height);
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        if (position.x < 0 || position.x >= this.width)
            position.x = (this.width + position.x) % this.width;
        if (position.y < 0 || position.y >= this.height)
            position.y = (this.height + position.y) % this.height;
        return true;
    }
}
