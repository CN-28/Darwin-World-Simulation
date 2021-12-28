package agh.ics.oop.MapElements;

public enum MapDirection {
    NORTH,
    NORTH_EAST,
    NORTH_WEST,
    WEST,
    EAST,
    SOUTH,
    SOUTH_EAST,
    SOUTH_WEST;

    public String toString(){
        return switch (this) {
            case NORTH -> "North";
            case NORTH_EAST -> "North-east";
            case NORTH_WEST -> "North-west";
            case SOUTH_EAST -> "South_east";
            case SOUTH_WEST -> "South-west";
            case SOUTH -> "South";
            case WEST -> "West";
            case EAST -> "East";
        };
    }

    public MapDirection next(){
        return switch (this) {
            case NORTH -> NORTH_EAST;
            case NORTH_EAST -> EAST;
            case EAST -> SOUTH_EAST;
            case SOUTH_EAST -> SOUTH;
            case SOUTH -> SOUTH_WEST;
            case SOUTH_WEST -> WEST;
            case WEST -> NORTH_WEST;
            case NORTH_WEST -> NORTH;
        };
    }

    public MapDirection previous(){
        return switch (this) {
            case NORTH -> NORTH_WEST;
            case NORTH_WEST -> WEST;
            case WEST -> SOUTH_WEST;
            case SOUTH_WEST -> SOUTH;
            case SOUTH -> SOUTH_EAST;
            case SOUTH_EAST -> EAST;
            case EAST -> NORTH_EAST;
            case NORTH_EAST -> NORTH;
        };
    }

    public Vector2d toUnitVector(){
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTH_EAST -> new Vector2d(1, 1);
            case NORTH_WEST -> new Vector2d(-1, 1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTH_EAST -> new Vector2d(1, -1);
            case SOUTH_WEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case EAST -> new Vector2d(1, 0);
        };
    }
}
