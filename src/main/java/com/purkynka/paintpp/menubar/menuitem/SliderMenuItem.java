package com.purkynka.paintpp.menubar.menuitem;

import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class SliderMenuItem extends CustomMenuItem {
    private VBox sliderLayoutVBox;
    private Label sliderLabel;
    private Slider slider;

    public SliderMenuItem(String labelText, double minValue, double maxValue, double defaultValue) {
        super();

        sliderLayoutVBox = new VBox();
        sliderLabel = new Label(labelText);
        slider = new Slider(minValue, maxValue, defaultValue);

        sliderLayoutVBox.getChildren().addAll(
                sliderLabel,
                slider
        );

        setContent(sliderLayoutVBox);
    }

    public SliderMenuItem(String labelText, double defaultValue) {
        this(labelText, 0, 100, defaultValue);
    }

    public SliderMenuItem(String labelText) {
        this(labelText, 0, 100, 0);
    }
}
