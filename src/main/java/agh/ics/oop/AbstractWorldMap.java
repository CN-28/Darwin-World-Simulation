package agh.ics.oop;

import java.util.HashMap;
import java.util.TreeSet;
import static java.lang.Math.sqrt;

abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    protected final int width, height;
    protected final Vector2d lowerLeft;
    protected final Vector2d upperRight;
    protected final MapVisualizer mapVisualization;
    protected final HashMap <Vector2d, TreeSet<Animal>> animals = new HashMap<>();
    protected final HashMap <Vector2d, Plant> plants = new HashMap<>();
    protected final double jungleRatio = 0.5;
    protected Vector2d jungleLowerLeft;
    protected Vector2d jungleUpperRight;

    public AbstractWorldMap(int width, int height){
        this.width = width;
        this.height = height;
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width - 1, height - 1);

        double a = sqrt((this.width * this.height - ((double) this.width * this.height)/(1 + this.jungleRatio)) * ((double) this.width)/ ((double) this.height));
        double b = ((double) this.height)/((double) this.width) * a;
        this.jungleLowerLeft = new Vector2d(this.width / 2 - ((int) a) / 2, this.height / 2 - ((int) b) / 2);
        this.jungleUpperRight = new Vector2d(this.width / 2 + ((int) a - 1) / 2, this.height / 2 + ((int) b - 1) / 2);

        this.mapVisualization = new MapVisualizer(this, this.jungleLowerLeft, this.jungleUpperRight);
    }


    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Object a){
        this.animals.get(oldPosition).remove((Animal) a);
        if (this.animals.get(newPosition) == null) {
            this.animals.put(newPosition, new TreeSet<>((a1, a2) -> {
                if (a1 == a2)
                    return 0;
                else if (a1.energy < a2.energy)
                    return -1;
                else return 1;
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
            else if (a1.energy < a2.energy)
                return -1;
            else
                return 1;
        }));

        animal.addObserver(this);
        this.animals.get(animalPos).add(animal);
        return true;
    }

    public Object objectAt(Vector2d position) {
        if (animals.get(position) != null){
            if (animals.get(position).size() > 0)
                return animals.get(position).first();
        }
        return null;
    }

    public String toString(){
        return this.mapVisualization.draw(this.lowerLeft, this.upperRight);
    }
}
