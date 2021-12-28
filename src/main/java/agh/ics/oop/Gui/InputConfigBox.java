package agh.ics.oop.Gui;

import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;


public class InputConfigBox {
    public HBox hBox = new HBox();
    public Label label = new Label();
    public Spinner<Integer> intSpinner;
    public Spinner<Double> doubleSpinner;

    public InputConfigBox(int minVal, int maxVal, int initVal, String text){
        this.label.setText(text);
        this.intSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minVal, maxVal, initVal);
        this.intSpinner.setValueFactory(valueFactory);
        this.intSpinner.setMaxSize(60, 15);
        this.hBox.setSpacing(15);
        this.hBox.getChildren().addAll(this.label, this.intSpinner);
    }

    public InputConfigBox(double minVal, double maxVal, double initVal, String text){
        this.label.setText(text);
        this.doubleSpinner = new Spinner<>();
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(minVal, maxVal, initVal, 0.01);
        this.doubleSpinner.setValueFactory(valueFactory);
        this.doubleSpinner.setMaxSize(70, 15);
        this.hBox.setSpacing(15);
        this.hBox.getChildren().addAll(this.label, this.doubleSpinner);
    }

    public int getIntValue(){
        return this.intSpinner.getValue();
    }

    public double getDoubleValue(){
        return this.doubleSpinner.getValue();
    }
}
