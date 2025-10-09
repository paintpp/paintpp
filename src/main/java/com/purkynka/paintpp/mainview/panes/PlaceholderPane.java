package com.purkynka.paintpp.mainview.panes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PlaceholderPane extends VBox {
    public PlaceholderPane(String text) {
        super();

        VBox.setVgrow(this, Priority.ALWAYS);

        var label = new Label(text);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        VBox.setVgrow(label, Priority.ALWAYS);

        getChildren().add(label);
    }
}
