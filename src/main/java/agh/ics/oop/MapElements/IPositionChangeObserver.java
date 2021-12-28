package agh.ics.oop.MapElements;

public interface IPositionChangeObserver {
    void positionChanged(Vector2d oldPosition, Vector2d newPosition, Object o);
}
