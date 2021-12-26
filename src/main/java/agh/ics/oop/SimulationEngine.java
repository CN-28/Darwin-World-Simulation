package agh.ics.oop;

import java.util.*;

import static agh.ics.oop.MoveDirection.*;

public class SimulationEngine implements IEngine, Runnable {
    private final LinkedHashSet<Animal> animals = new LinkedHashSet<>();
    private final AbstractWorldMap map;
    private IAnimalMoveObserver observer;
    protected boolean magical;
    protected int magicalCounter = 0;

    public SimulationEngine(AbstractWorldMap map){
        this.map = map;

        for (Vector2d AnimalPos : AbstractWorldMap.drawnAnimals){
            Animal animal = new Animal(map, AnimalPos);
            if(this.map.place(animal))
                this.animals.add(animal);
        }
    }

    public void run() {
        while(true) {
            while (!this.map.running) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Random rand = new Random();

            // deleting dead animals
            for (Iterator<Animal> iterator = this.animals.iterator(); iterator.hasNext(); ) {
                Animal animal = iterator.next();
                if (animal.isDead()) {
                    this.map.removeDeadAnimal(animal);
                    iterator.remove();
                    //this.notifyObserver();
                }
            }

            // moving animals at the start of the day
            int i = 0;
            for (Animal animal : animals) {
                final int move = animal.genes.genotype[rand.nextInt(32)];

                switch (move) {
                    case 0 -> moveNTimes(1, FORWARD, animal);
                    case 1 -> moveNTimes(1, RIGHT, animal);
                    case 2 -> moveNTimes(2, RIGHT, animal);
                    case 3 -> moveNTimes(3, RIGHT, animal);
                    case 4 -> moveNTimes(1, BACKWARD, animal);
                    case 5 -> moveNTimes(3, LEFT, animal);
                    case 6 -> moveNTimes(2, LEFT, animal);
                    case 7 -> moveNTimes(1, LEFT, animal);
                }
                if (i % 2 == 0)
                    this.notifyObserver();

                i++;
            }

            // eating plants by animals
            for (Iterator<Map.Entry<Vector2d, Plant>> iterator = this.map.plants.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Vector2d, Plant> entry = iterator.next();
                Vector2d plantPosition = entry.getKey();

                Object o = this.map.objectAt(plantPosition);
                if (o instanceof Animal) {
                    int maxEnergy = ((Animal) o).getEnergy();

                    // dividing energy from plant into animals with the most energy at plantPosition
                    int toDivide = 0;
                    for (Animal animal : this.map.animals.get(plantPosition)) {
                        if (animal.getEnergy() == maxEnergy) toDivide++;
                        else break;
                    }

                    // iterate through all animals with maxEnergy and increase their energy value
                    int iter = 0;
                    for (Animal animal : this.map.animals.get(plantPosition)) {
                        if (iter < toDivide) animal.increaseEnergy(Plant.getPlantEnergy() / toDivide);
                        else break;
                        iter++;
                    }
                    iterator.remove();
                }
            }

            // animals reproduction
            for (Vector2d animalsPosition : this.map.animals.keySet()) {
                if (this.map.animals.get(animalsPosition).size() > 1) {
                    Animal parent1 = null;
                    Animal parent2 = null;
                    for (Animal animal : this.map.animals.get(animalsPosition)) {

                        if (((double) animal.getEnergy()) / Animal.getStartEnergy() >= 0.5) {
                            if (parent1 == null) parent1 = animal;
                            else parent2 = animal;
                        }

                        if (parent1 != null && parent2 != null) {
                            this.animals.add(this.map.reproduceAnimals(parent1, parent2));
                            break;
                        }
                    }
                }
            }

            // placing new plants on the map
            this.map.placePlants();

            // handling magical mode
            if (magical && magicalCounter < 3 && this.animals.size() == 5) {
                Collections.shuffle(AbstractWorldMap.positionsToDrawFrom);
                int cnt = 0;
                ArrayList<Animal> toAdd = new ArrayList<>();
                Iterator<Animal> iterator = this.animals.iterator();
                for (Vector2d position : AbstractWorldMap.positionsToDrawFrom) {
                    if (!this.map.isOccupied(position)) {
                        Animal animal = iterator.next();
                        Animal newAnimal = animal.copyAnimal(this.map, position);
                        this.map.place(newAnimal);
                        toAdd.add(newAnimal);
                        cnt++;
                    }
                    if (cnt == 5)
                        break;
                }
                this.animals.addAll(toAdd);

                magicalCounter++;
                this.notifyObserver();
            }
        }
    }


    void moveNTimes(int n, MoveDirection direction, Animal animal){
        for (int i = 0; i < n; i++)
            animal.move(direction);

        this.map.animals.get(animal.getPosition()).remove(animal);
        animal.decreaseEnergy(Animal.getMoveEnergy());
        this.map.animals.get(animal.getPosition()).add(animal);
    }

    public void addObserver(IAnimalMoveObserver o){
        this.observer = o;
    }

    public void notifyObserver(){
        this.observer.animalMoved(this.map);
    }

    public void setMagical(boolean isMagical){
        this.magical = isMagical;
    }
}