package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

public class SimulationEngine implements IEngine{
    private final ArrayList<MoveDirection> directions;
    private final List<Animal> animals = new ArrayList<>();
    private final IWorldMap map;
    private final static int startEnergy = 7;

    public SimulationEngine(ArrayList<MoveDirection> directions, IWorldMap map, Vector2d[] positions){
        this.directions = directions;
        this.map = map;

        for (Vector2d AnimalPos : positions){
            Animal animal = new Animal(map, AnimalPos, startEnergy);
            if(this.map.place(animal))
                this.animals.add(animal);
        }
    }

    public void run() {
        int numberOfAnimals = this.animals.size();
        for (int i = 0; i < this.directions.size(); i++){
            Animal animal = animals.get(i % numberOfAnimals);
            animal.move(this.directions.get(i));
        }
    }
}