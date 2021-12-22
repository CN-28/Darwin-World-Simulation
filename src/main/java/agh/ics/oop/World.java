package agh.ics.oop;

import java.util.ArrayList;
import java.util.Arrays;

import static agh.ics.oop.MoveDirection.*;

public class World {
    public static void main(String[] args){
        ArrayList<MoveDirection> directions = new ArrayList<>(Arrays.asList(RIGHT, FORWARD, FORWARD));
        IWorldMap map = new UnboundedMap(9, 9);
        Vector2d[] positions = {new Vector2d(0, 0), new Vector2d(2, 1)};
        IEngine engine = new SimulationEngine(directions, map, positions);
        System.out.println(map);
        engine.run();
        System.out.println(map);
    }
}
