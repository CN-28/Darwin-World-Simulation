package agh.ics.oop;

import java.util.*;

import static java.lang.Math.sqrt;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    public final Vector2d lowerLeft;
    public final Vector2d upperRight;
    protected final MapVisualizer mapVisualization;
    public final HashMap <Vector2d, TreeSet<Animal>> animals = new HashMap<>();
    public final HashMap <Vector2d, Plant> plants = new HashMap<>();

    protected static int width, height;
    protected static int numberOfAnimalsAtStart;
    protected static Vector2d jungleLowerLeft;
    protected static Vector2d jungleUpperRight;
    protected static final double jungleRatio = 0.6;
    protected static ArrayList<Vector2d> steppeRandomPlants = new ArrayList<>();
    protected static ArrayList<Vector2d> jungleRandomPlants = new ArrayList<>();
    protected static ArrayList<Vector2d> drawnAnimals = new ArrayList<>();

    public AbstractWorldMap(){
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width - 1, height - 1);
        this.mapVisualization = new MapVisualizer(this, jungleLowerLeft, jungleUpperRight);
        this.placePlants();
    }

    public static void setInitialValues(){
        // setting the jungle for the maps
        double a = sqrt((width * height - ((double) width * height)/(1 + jungleRatio)) * ((double) width)/ ((double) height));
        double b = ((double) height)/((double) width) * a;
        jungleLowerLeft = new Vector2d(width / 2 - ((int) a) / 2, height / 2 - ((int) b) / 2);
        jungleUpperRight = new Vector2d(width / 2 + ((int) a - 1) / 2, height / 2 + ((int) b - 1) / 2);

        // preparing arrays for functionality of placing plants randomly
        Vector2d tempVector;
        ArrayList<Vector2d> positionsToDrawFrom = new ArrayList<>();
        for (int i = 0; i < width * height; i++){
            tempVector = new Vector2d(i / width, i % width);
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
        Collections.shuffle(jungleRandomPlants);
        for (Vector2d position : jungleRandomPlants){
            if (!isOccupied(position)){
                plants.put(position, new Plant(position));
                break;
            }
        }

        // place one plant on the steppe
        Collections.shuffle(steppeRandomPlants);
        for (Vector2d position : steppeRandomPlants){
            if (!isOccupied(position)){
                plants.put(position, new Plant(position));
                break;
            }
        }
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Object a){
        this.animals.get(oldPosition).remove((Animal) a);
        if (this.animals.get(oldPosition).size() == 0)
            this.animals.remove(oldPosition);

        if (this.animals.get(newPosition) == null) {
            this.animals.put(newPosition, new TreeSet<>((a1, a2) -> {
                if (a1 == a2)
                    return 0;
                else if (a1.getEnergy() < a2.getEnergy())
                    return 1;
                return -1;
            }));
        }
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
            if (a1 == a2)
                return 0;
            else if (a1.getEnergy() < a2.getEnergy())
                return 1;
            return -1;
        }));

        animal.addObserver(this);
        this.animals.get(animalPos).add(animal);
        return true;
    }


    public Animal reproduceAnimals(Animal parent1, Animal parent2){
        Animal animal = new Animal(this, parent1, parent2);
        parent1.decreaseEnergy((int) (((double) parent1.getEnergy()) / 4));
        parent2.decreaseEnergy((int) (((double) parent2.getEnergy()) / 4));
        this.animals.get(animal.getPosition()).add(animal);
        return animal;
    }

    public void removeDeadAnimal(Animal animal){
        Vector2d animalPos = animal.getPosition();
        this.animals.get(animalPos).remove(animal);
        if (this.animals.get(animalPos).size() == 0)
            this.animals.remove(animalPos);
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

    public String toString(){
        return this.mapVisualization.draw(this.lowerLeft, this.upperRight);
    }
}
