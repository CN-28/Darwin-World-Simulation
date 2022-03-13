package agh.ics.oop.Engine;

import agh.ics.oop.Gui.IGuiUpdateObserver;
import agh.ics.oop.MapElements.Animal;
import agh.ics.oop.MapElements.Plant;
import agh.ics.oop.Maps.AbstractWorldMap;
import agh.ics.oop.MapElements.MoveDirection;
import agh.ics.oop.MapElements.Vector2d;
import javafx.application.Platform;

import java.util.*;

import static agh.ics.oop.MapElements.MoveDirection.*;

public class SimulationEngine implements IEngine, Runnable {
    private final LinkedHashSet<Animal> animals = new LinkedHashSet<>();
    private final AbstractWorldMap map;
    private IGuiUpdateObserver observer;
    protected boolean magical, deleted;
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
                sleepABit();
            }

            deleteDeadAnimalsAndCountStats();
            moveAnimals();
            animalsEatPlants();
            reproduceAnimals();
            map.placePlants();
            handleMagicalMode();
            updateStats();

            if (this.animals.size() == 0){
                clearGrid();
                sleepABit();
            }
        }
    }

    private void sleepABit(){
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clearGrid(){
        if (!deleted){
            for (int i = 0; i < AbstractWorldMap.height; i++){
                for (int j = 0; j < AbstractWorldMap.width; j++){
                    int finalI = i;
                    int finalJ = j;
                    Platform.runLater(() -> {
                        map.guiElements.gridNodes[map.upperRight.y - finalI][finalJ].getChildren().clear();
                        map.guiElements.fillCell(finalJ, finalI);
                    });
                }
            }
            deleted = true;
        }
    }

    private void moveAnimals(){
        Random rand = new Random();
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
        }
    }

    void moveNTimes(int n, MoveDirection direction, Animal animal){
        Vector2d animalPos = animal.getPosition();

        for (int i = 0; i < n; i++)
            animal.move(direction);

        this.map.animals.get(animal.getPosition()).remove(animal);
        animal.decreaseEnergy(Animal.getMoveEnergy());
        this.map.animals.get(animal.getPosition()).add(animal);


        Vector2d pos = new Vector2d(animalPos.x, map.upperRight.y - animalPos.y);
        Platform.runLater(() -> {
            // delete animal rendering if it is on gui
            boolean temp = false;
            if (map.guiElements.boxNodes.containsKey(pos) && map.guiElements.boxNodes.get(pos).equals(animal.box.vBox)){
                temp = true;
                map.guiElements.gridNodes[pos.y][pos.x].getChildren().clear();
                map.guiElements.fillCell(animalPos.x, animalPos.y);
                map.guiElements.boxNodes.remove(pos);
            }

            animal.box.updateBox();
            if (temp){
                if (animal.getPosition().equals(animalPos)){
                    map.guiElements.gridNodes[pos.y][pos.x].getChildren().add(animal.box.vBox);
                    map.guiElements.boxNodes.put(pos, animal.box.vBox);
                }
                else{
                    if (map.animals.containsKey(animalPos) && map.animals.get(animalPos).size() > 0){
                        map.guiElements.gridNodes[pos.y][pos.x].getChildren().add(map.animals.get(animalPos).first().box.vBox);
                        map.guiElements.boxNodes.put(pos, map.animals.get(animalPos).first().box.vBox);
                    }
                    else if (map.plants.containsKey(animalPos)){
                        map.guiElements.gridNodes[pos.y][pos.x].getChildren().add(map.plants.get(animalPos).box.vBox);
                        map.guiElements.boxNodes.put(pos, map.plants.get(animalPos).box.vBox);
                    }
                }
            }

            if (!animal.getPosition().equals(animalPos) && map.animals.containsKey(animal.getPosition()) && map.animals.get(animal.getPosition()).size() > 0){
                if (map.animals.get(animal.getPosition()).first().equals(animal)){
                    Vector2d newPos = new Vector2d(animal.getPosition().x, map.upperRight.y - animal.getPosition().y);
                    map.guiElements.gridNodes[newPos.y][newPos.x].getChildren().clear();
                    map.guiElements.fillCell(animal.getPosition().x, animal.getPosition().y);
                    if (map.plants.containsKey(animal.getPosition()))
                        map.guiElements.gridNodes[newPos.y][newPos.x].getChildren().remove(map.plants.get(animal.getPosition()).box.vBox);
                    map.guiElements.boxNodes.remove(newPos);

                    map.guiElements.gridNodes[newPos.y][newPos.x].getChildren().add(animal.box.vBox);
                    map.guiElements.boxNodes.put(newPos, animal.box.vBox);
                }
            }
        });

        try {
            Thread.sleep(AbstractWorldMap.moveDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void deleteDeadAnimalsAndCountStats(){
        this.map.tempSumAverageEnergy = 0;
        this.map.tempSumChildrenAverage = 0;
        this.dominantGenotypes.clear();

        for (Iterator<Animal> iterator = this.animals.iterator(); iterator.hasNext(); ) {
            Animal animal = iterator.next();

            if (animal.isDead()) {
                iterator.remove();
                this.map.removeDeadAnimal(animal, epoch);
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
    }


    private void animalsEatPlants(){
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
                // maybe remove and add again to treeset - maybe bug?
                int iter = 0;
                for (Animal animal : this.map.animals.get(plantPosition)) {
                    if (iter < toDivide) animal.increaseEnergy(Plant.getPlantEnergy() / toDivide);
                    else break;
                    iter++;
                }
                iterator.remove();
            }
        }
    }

    private void reproduceAnimals(){
        for (Vector2d animalsPosition : this.map.animals.keySet()){
            if (this.map.animals.get(animalsPosition).size() > 1){
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
    }


    private void handleMagicalMode(){
        // handling magical mode
        if (magical && magicalCounter < 3 && this.animals.size() == 5) {
            System.out.println("MAGIC");
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
        }
    }

    private void updateStats(){
        epoch++;
        this.map.guiElements.chart.updateChart(epoch, this.map.numberOfAnimals, this.map.numberOfPlants);
        this.observer.updateStats(this.map);
        this.map.toWriteToCSV.add((double) this.map.numberOfAnimals);
        this.map.toWriteToCSV.add((double) this.map.numberOfPlants);
        this.map.toWriteToCSV.add(this.map.averageEnergy);
        this.map.toWriteToCSV.add(this.map.averageLifeSpan);
        this.map.toWriteToCSV.add(this.map.averageNumberOfChildren);
    }

    public void addObserver(IGuiUpdateObserver o){
        this.observer = o;
    }

    public void setMagical(boolean isMagical){
        this.magical = isMagical;
    }
}