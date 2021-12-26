package agh.ics.oop.gui;

import agh.ics.oop.Animal;
import agh.ics.oop.IMapElement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

public class GuiElementBox {
    public VBox vBox;
    public Label boxLabel;

    public GuiElementBox(IMapElement mapElement) {
        ImageView imageView = mapElement.getPicture();
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        vBox = new VBox();
        if (mapElement instanceof Animal)
            boxLabel = new Label(((Animal) mapElement).getEnergy() + "");
        else
            boxLabel = new Label("Plant");

        boxLabel.setStyle("-fx-font-size: 11;");
        vBox.getChildren().addAll(imageView, boxLabel);
        vBox.setAlignment(Pos.CENTER);
    }
}
