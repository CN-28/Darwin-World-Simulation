package agh.ics.oop.Engine;

import agh.ics.oop.Gui.IGuiUpdateObserver;
import agh.ics.oop.MapElements.Animal;
import agh.ics.oop.MapElements.Plant;
import agh.ics.oop.Maps.AbstractWorldMap;
import agh.ics.oop.MapElements.MoveDirection;
import agh.ics.oop.MapElements.Vector2d;

import java.util.*;

import static agh.ics.oop.MapElements.MoveDirection.*;

public class SimulationEngine implements IEngine, Runnable {
    private final LinkedHashSet<Animal> animals = new LinkedHashSet<>();
    private final AbstractWorldMap map;
    private IGuiUpdateObserver observer;
    protected boolean magical;
    protected int magicalCounter = 0;
    protected int epoch = 0;
    private final HashMap<String, Integer> dominantGenotypes = new HashMap<>();

    public SimulationEngine(AbstractWorldMap map){
        this.map = map;

        for (Vector2d AnimalPos : AbstractWorldMap.drawnAnimals){
            Animal animal = new Animal(map, AnimalPos, epoch);
            if(this.map.place(animal))
                this.animals.add(animal);
        }
    }

    public void run() {
        while(true) {
            while (!this.map.running) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Random rand = new Random();
            this.map.tempSumAverageEnergy = 0;
            this.map.tempSumChildrenAverage = 0;
            this.dominantGenotypes.clear();
            // deleting dead animals, counting the averageEnergy at the start of every day in simulation
            for (Iterator<Animal> iterator = this.animals.iterator(); iterator.hasNext(); ) {
                Animal animal = iterator.next();
                if (animal.isDead()) {
                    this.map.removeDeadAnimal(animal, epoch);
                    iterator.remove();
                    this.notifyObserver();
                }
                else{
                    String genotypeStr = "";
                    StringBuilder genotypeResBuilder = new StringBuilder(genotypeStr);
                    for (int num : animal.genes.genotype)
                        genotypeResBuilder.append(num).append(" ");
                    genotypeStr = genotypeResBuilder.toString();

                    if (dominantGenotypes.containsKey(genotypeStr)){
                        int cnt = dominantGenotypes.get(genotypeStr);
                        dominantGenotypes.remove(genotypeStr);
                        dominantGenotypes.put(genotypeStr, cnt + 1);
                    }
                    else
                        dominantGenotypes.put(genotypeStr, 1);

                    this.map.tempSumAverageEnergy += animal.getEnergy();
                    this.map.tempSumChildrenAverage += animal.numberOfChildren;
                }
            }
            if (this.map.numberOfDeadAnimals != 0)
                this.map.averageLifeSpan = (double) (this.map.tempSumLifeSpan) / this.map.numberOfDeadAnimals;
            if (this.map.numberOfAnimals != 0){
                this.map.averageEnergy = (double) (this.map.tempSumAverageEnergy)/ this.map.numberOfAnimals;
                this.map.averageNumberOfChildren = (double) (this.map.tempSumChildrenAverage) / this.map.numberOfAnimals;
            }
            int maxi = 0;
            String dominantGenotypeRes = "";
            for (String genotype : dominantGenotypes.keySet()){
                if (dominantGenotypes.get(genotype) > maxi){
                    maxi = dominantGenotypes.get(genotype);
                    dominantGenotypeRes = genotype;
                }
            }
            this.map.dominantGenotype = dominantGenotypeRes;



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
                if (i % 2 == 0){
                    this.notifyObserver();
                    try {
                        Thread.sleep(AbstractWorldMap.moveDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                i++;
            }

            // eating plants by animals
            for (Iterator<Map.Entry<Vector2d, Plant>> iterator = this.map.plants.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Vector2d, Plant> entry = iterator.next();
                Vector2d plantPosition = entry.getKey();

                Object o = this.map.objectAt(plantPosition);
                if (o instanceof Animal) {
                    this.map.numberOfPlants--;
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
                            this.animals.add(this.map.reproduceAnimals(parent1, parent2, epoch));
                            break;
                        }
                    }
                }
            }

            // placing new plants on the map
            this.map.placePlants();
            this.notifyObserver();

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

            epoch++;
            this.map.guiElements.chart.updateChart(epoch, this.map.numberOfAnimals, this.map.numberOfPlants);
            this.observer.updateStats(this.map);
            this.map.toWriteToCSV.add((double) this.map.numberOfAnimals);
            this.map.toWriteToCSV.add((double) this.map.numberOfPlants);
            this.map.toWriteToCSV.add(this.map.averageEnergy);
            this.map.toWriteToCSV.add(this.map.averageLifeSpan);
            this.map.toWriteToCSV.add(this.map.averageNumberOfChildren);
        }
    }


    void moveNTimes(int n, MoveDirection direction, Animal animal){
        for (int i = 0; i < n; i++)
            animal.move(direction);

        this.map.animals.get(animal.getPosition()).remove(animal);
        animal.decreaseEnergy(Animal.getMoveEnergy());
        this.map.animals.get(animal.getPosition()).add(animal);
    }

    public void addObserver(IGuiUpdateObserver o){
        this.observer = o;
    }

    public void notifyObserver(){
        this.observer.updateNeeded(this.map);
    }

    public void setMagical(boolean isMagical){
        this.magical = isMagical;
    }
}