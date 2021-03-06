package agh.ics.oop.Maps;

import agh.ics.oop.MapElements.Animal;
import agh.ics.oop.MapElements.IPositionChangeObserver;
import agh.ics.oop.MapElements.Plant;
import agh.ics.oop.MapElements.Vector2d;
import agh.ics.oop.Gui.CSVWriter;
import agh.ics.oop.Gui.MapGuiElements;
import javafx.application.Platform;

import java.util.*;

import static java.lang.Math.sqrt;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    public final Vector2d lowerLeft;
    public final Vector2d upperRight;
    public final HashMap <Vector2d, TreeSet<Animal>> animals = new HashMap<>();
    public final HashMap <Vector2d, Plant> plants = new HashMap<>();
    public boolean running = false;
    public int numberOfPlants = 0;
    public int numberOfAnimals = 0;
    public double averageEnergy = 0;
    public double averageLifeSpan = 0;
    public double averageNumberOfChildren = 0;
    public int tempSumLifeSpan = 0;
    public int tempSumAverageEnergy = 0;
    public int numberOfDeadAnimals = 0;
    public int tempSumChildrenAverage = 0;
    public String dominantGenotype = "";
    public ArrayList<Double> toWriteToCSV = new ArrayList<>();
    public Animal trackedAnimal = null;
    public int numberOfDescendants = 0;
    public int trackedAnimalDeath = -1;
    public int numberOfChildren = 0;
    public MapGuiElements guiElements;
    public CSVWriter csvWriter = new CSVWriter(this);

    public static int width, height;
    protected static int numberOfAnimalsAtStart;
    public static Vector2d jungleLowerLeft;
    public static Vector2d jungleUpperRight;
    protected static double jungleRatio;
    public static int moveDelay;
    public static ArrayList<Vector2d> steppeRandomPlants = new ArrayList<>();
    public static ArrayList<Vector2d> jungleRandomPlants = new ArrayList<>();
    public static ArrayList<Vector2d> drawnAnimals = new ArrayList<>();
    public static ArrayList<Vector2d> positionsToDrawFrom = new ArrayList<>();

    public AbstractWorldMap(){
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width - 1, height - 1);
    }

    public static void setInitialValues(){
        // setting the jungle for the maps
        double a = sqrt((width * height - ((double) width * height)/(1 + jungleRatio)) * ((double) width)/ ((double) height));
        double b = ((double) height)/((double) width) * a;
        jungleLowerLeft = new Vector2d(width / 2 - ((int) a) / 2, height / 2 - ((int) b) / 2);
        jungleUpperRight = new Vector2d(width / 2 + ((int) a - 1) / 2, height / 2 + ((int) b - 1) / 2);

        // preparing arrays for functionality of placing plants randomly
        Vector2d tempVector;
        for (int i = 0; i < width * height; i++){
            tempVector = new Vector2d(i % width, i / width);
            positionsToDrawFrom.add(tempVector);
            if (tempVector.follows(jungleLowerLeft) && tempVector.precedes(jungleUpperRight))
                jungleRandomPlants.add(tempVector);
            else
                steppeRandomPlants.add(tempVector);
        }

        // drawing initial positions for animals, which will be placed at the initialization of maps
        Collections.shuffle(positionsToDrawFrom);
        for (int i = 0 ; i < numberOfAnimalsAtStart; i++)
            drawnAnimals.add(positionsToDrawFrom.get(i));
    }

    public void placePlants(){
        // place one plant in the jungle
        placePlant(jungleRandomPlants);

        // place one plant on the steppe
        placePlant(steppeRandomPlants);
    }

    private void placePlant(ArrayList<Vector2d> terrain){
        Collections.shuffle(terrain);
        for (Vector2d position : terrain){
            if (!isOccupied(position)){
                Plant plant = new Plant(position, this);
                plants.put(position, plant);
                if (!guiElements.boxNodes.containsKey(position)){
                    Platform.runLater(() -> {
                        guiElements.gridNodes[upperRight.y - position.y][position.x].getChildren().clear();
                        guiElements.fillCell(position.x, position.y);
                        guiElements.gridNodes[upperRight.y - position.y][position.x].getChildren().add(plant.box.vBox);
                    });
                    guiElements.boxNodes.put(position, plant.box.vBox);
                }
                this.numberOfPlants++;
                break;
            }
        }
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Object a){
        this.animals.get(oldPosition).remove((Animal) a);
        if (this.animals.get(oldPosition).size() == 0)
            this.animals.remove(oldPosition);

        this.animals.computeIfAbsent(newPosition, k -> new TreeSet<>((a1, a2) -> {
            if (a1.getEnergy() == a2.getEnergy() && a1 == a2)
                return 0;
            else if (a1.getEnergy() < a2.getEnergy())
                return 1;
            return -1;
        }));
        this.animals.get(newPosition).add((Animal) a);
    }

    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }


    // method for placing animals on the initialization of the map
    public boolean place(Animal animal) {
        Vector2d animalPos = animal.getPosition();
        if (objectAt(animalPos) instanceof Animal)
            throw new IllegalArgumentException("The position " + animalPos + " is invalid");

        this.animals.put(animalPos, new TreeSet<>((a1, a2) -> {
            if (a1.getEnergy() == a2.getEnergy() && a1 == a2)
                return 0;
            else if (a1.getEnergy() < a2.getEnergy())
                return 1;
            return -1;
        }));

        animal.addObserver(this);
        this.animals.get(animalPos).add(animal);
        this.numberOfAnimals++;
        return true;
    }


    public Animal reproduceAnimals(Animal parent1, Animal parent2, int epoch){
        Animal animal;
        if (parent1.getEnergy() >= parent2.getEnergy())
            animal = new Animal(this, parent1, parent2, epoch);
        else
            animal = new Animal(this, parent2, parent1, epoch);

        if (parent1 == this.trackedAnimal || parent2 == this.trackedAnimal || parent1.isDescendant || parent2.isDescendant){
            this.numberOfDescendants++;
            animal.isDescendant = true;
        }
        if (parent1 == this.trackedAnimal || parent2 == this.trackedAnimal)
            this.numberOfChildren++;

        parent1.numberOfChildren++;
        parent2.numberOfChildren++;
        this.animals.get(parent1.getPosition()).remove(parent1);
        parent1.decreaseEnergy((int) (((double) parent1.getEnergy()) / 4));
        this.animals.get(parent1.getPosition()).add(parent1);

        this.animals.get(parent2.getPosition()).remove(parent2);
        parent2.decreaseEnergy((int) (((double) parent2.getEnergy()) / 4));
        this.animals.get(parent2.getPosition()).add(parent2);

        animal.addObserver(this);
        this.animals.get(animal.getPosition()).add(animal);
        this.numberOfAnimals++;
        return animal;
    }

    public void removeDeadAnimal(Animal animal, int epoch, boolean updateMagical, ArrayList<Animal> toAdd){
        if (animal == this.trackedAnimal)
            this.trackedAnimalDeath = epoch;

        Vector2d animalPos = animal.getPosition();
        this.animals.get(animalPos).remove(animal);
        if (this.animals.get(animalPos).size() == 0)
            this.animals.remove(animalPos);


        Vector2d pos = new Vector2d(animalPos.x, upperRight.y - animalPos.y);
        Platform.runLater(() -> {
            if (animal.box.vBox.equals(guiElements.boxNodes.get(pos))){
                // remove dead animal
                guiElements.gridNodes[pos.y][pos.x].getChildren().clear();
                guiElements.fillCell(animalPos.x, animalPos.y);
                guiElements.boxNodes.remove(pos);

                // if plant or animal there render it
                if (animals.containsKey(animalPos) && animals.get(animalPos).size() > 0){
                    guiElements.gridNodes[pos.y][pos.x].getChildren().add(animals.get(animalPos).first().box.vBox);
                    guiElements.boxNodes.put(pos, animals.get(animalPos).first().box.vBox);
                }
                else if (plants.containsKey(animalPos)){
                    guiElements.gridNodes[pos.y][pos.x].getChildren().add(plants.get(animalPos).box.vBox);
                    guiElements.boxNodes.put(pos, plants.get(animalPos).box.vBox);
                }
            }

            if (updateMagical && toAdd != null) {
                for (Animal newAnimal : toAdd){
                    guiElements.gridNodes[upperRight.y - newAnimal.getPosition().y][newAnimal.getPosition().x].getChildren().clear();
                    guiElements.fillCell(newAnimal.getPosition().x, newAnimal.getPosition().y);
                    guiElements.gridNodes[upperRight.y - newAnimal.getPosition().y][newAnimal.getPosition().x].getChildren().add(newAnimal.box.vBox);
                    guiElements.boxNodes.put(new Vector2d(newAnimal.getPosition().x, upperRight.y - newAnimal.getPosition().y), newAnimal.box.vBox);
                }
            }
        });

        animal.removeObserver(this);
        this.numberOfAnimals--;
        this.tempSumLifeSpan += epoch - animal.birthDay;
        this.numberOfDeadAnimals++;
    }

    public Object objectAt(Vector2d position) {
        if (this.animals.get(position) != null)
            return animals.get(position).first();


        if (this.plants.get(position) != null)
            return this.plants.get(position);

        return null;
    }

    public static void setWidthHeight(int widthValue, int heightValue){
        width = widthValue;
        height = heightValue;
    }

    public static void setNumberOfAnimalsAtStart(int numberOfAnimalsAtStartValue){
        numberOfAnimalsAtStart = numberOfAnimalsAtStartValue;
    }

    public static void setJungleRatio(double jungleRatioValue){
        jungleRatio = jungleRatioValue;
    }

    public static void setMoveDelay(int moveDelayValue){
        moveDelay = moveDelayValue;
    }
}
