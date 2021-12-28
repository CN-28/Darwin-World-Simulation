package agh.ics.oop.MapElements;

import java.util.Arrays;
import java.util.Random;

public class Genes {
    public int[] genotype = new int[32];

    // constructor for animals created at the initialization of the map
    public Genes(){
        Random rand = new Random();
        for (int i = 0; i < this.genotype.length; i++)
            genotype[i] = rand.nextInt(8);
        Arrays.sort(genotype);
    }

    // constructor for animals born during simulation, parent1 is the parent with larger number of energy
    public Genes(Animal parent1, Animal parent2){
        Random rand = new Random();
        double energyRatio = ((double) parent1.getEnergy()) / (parent1.getEnergy() + parent2.getEnergy());
        int left = rand.nextInt(2);
        if (left == 1){
            if ((int) (energyRatio * this.genotype.length) >= 0)
                System.arraycopy(parent1.genes.genotype, 0, this.genotype, 0, (int) (energyRatio * this.genotype.length));
            if (this.genotype.length - (int) (energyRatio * this.genotype.length) >= 0)
                System.arraycopy(parent2.genes.genotype, (int) (energyRatio * this.genotype.length), this.genotype, (int) (energyRatio * this.genotype.length), this.genotype.length - (int) (energyRatio * this.genotype.length));
        }
        else{
            if ((int) ((1 - energyRatio) * this.genotype.length) >= 0)
                System.arraycopy(parent2.genes.genotype, 0, this.genotype, 0, (int) ((1 - energyRatio) * this.genotype.length));
            if (this.genotype.length - (int) ((1 - energyRatio) * this.genotype.length) >= 0)
                System.arraycopy(parent1.genes.genotype, (int) ((1 - energyRatio) * this.genotype.length), this.genotype, (int) ((1 - energyRatio) * this.genotype.length), this.genotype.length - (int) ((1 - energyRatio) * this.genotype.length));
        }
    }
}
