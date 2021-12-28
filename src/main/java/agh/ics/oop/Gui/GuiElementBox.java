package agh.ics.oop.Gui;

import agh.ics.oop.Maps.AbstractWorldMap;
import agh.ics.oop.MapElements.Animal;
import agh.ics.oop.MapElements.IMapElement;
import agh.ics.oop.MapElements.Vector2d;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class GuiElementBox {
    public VBox vBox;

    public GuiElementBox(IMapElement mapElement, AbstractWorldMap map, int size) {
        ImageView imageView = mapElement.getPicture();
        imageView.setFitWidth((double) 16/36 * (double) size);
        imageView.setFitHeight((double) 16/36 * (double) size);
        vBox = new VBox();
        vBox.setSpacing(1 * ((double) size / 36));
        if (mapElement instanceof Animal animal){
            ImageView energyView = animal.getEnergyPicture(animal.getEnergy());
            energyView.setFitHeight((double) 16/(36 * 3) * (double) size);
            energyView.setFitWidth((double) 24/36 * (double) size);
            vBox.setOnMouseClicked(e -> {

                String genotypeStr = "";
                StringBuilder genotypeResBuilder = new StringBuilder(genotypeStr);
                for (int num : animal.genes.genotype)
                    genotypeResBuilder.append(num).append(" ");
                genotypeStr = genotypeResBuilder.toString();
                map.guiElements.trackedAnimalLabel.setText("Tracked animal genotype: " + genotypeStr);
                map.trackedAnimal = animal;

                map.numberOfDescendants = 0;
                map.numberOfChildren = 0;
                for (Vector2d animalPos : map.animals.keySet()){
                    for (Animal tempAnimal : map.animals.get(animalPos))
                        tempAnimal.isDescendant = false;
                }

                map.guiElements.trackedAnimalDeathDayLabel.setText("");
                map.guiElements.trackedAnimalDescendantsLabel.setText("");
                map.guiElements.trackedAnimalChildrenLabel.setText("");

            });
            vBox.getChildren().addAll(imageView, energyView);
        }

        else
            vBox.getChildren().add(imageView);

        vBox.setAlignment(Pos.CENTER);
    }
}
