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
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        vBox = new VBox();
        if (mapElement instanceof Animal)
            boxLabel = new Label(mapElement.getPosition().toString());
        else
            boxLabel = new Label("Plant");

        vBox.getChildren().addAll(imageView, boxLabel);
        vBox.setAlignment(Pos.CENTER);
    }
}
