package agh.ics.oop;

import java.util.Random;

public class Genes {
    protected int[] genotype = new int[32];

    // constructor for animals created at the initialization of the map
    public Genes(){
        Random rand = new Random();
        for (int i = 0; i < this.genotype.length; i++)
            genotype[i] = rand.nextInt(8);
    }

    // constructor for animals born during simulation, parent1 is the parent with larger number of energy
    public Genes(Animal parent1, Animal parent2){
        Random rand = new Random();
        double energyRatio = ((double) parent1.getEnergy()) / (parent1.getEnergy() + parent2.getEnergy());
        int left = rand.nextInt(2);
        if (left == 1){
            for (int i = 0; i < (int) (energyRatio * this.genotype.length); i++)
                this.genotype[i] = parent1.genes.genotype[i];
            for (int i = (int) (energyRatio * this.genotype.length); i < this.genotype.length; i++)
                this.genotype[i] = parent2.genes.genotype[i];
        }
        else{
            for (int i = 0; i < (int) ((1 - energyRatio) * this.genotype.length); i++)
                this.genotype[i] = parent2.genes.genotype[i];
            for (int i = (int) ((1 - energyRatio) * this.genotype.length); i < this.genotype.length; i++)
                this.genotype[i] = parent1.genes.genotype[i];
        }
    }
}
