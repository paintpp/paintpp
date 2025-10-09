package com.purkynka.paintpp;

import atlantafx.base.theme.CupertinoDark;
import com.purkynka.paintpp.menubar.MenuBar;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Paintpp extends Application {
    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

        var root = new VBox();

        var menuBar = new MenuBar();

        var mainSplitPane = new SplitPane();
        VBox.setVgrow(mainSplitPane, Priority.ALWAYS);

        var brushesAndMatrix = new VBox();
        //brushesAndMatrix.setStyle("-fx-background-color: white;");
        
        var basicFiltersLabel = new Label();
        basicFiltersLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(basicFiltersLabel, Priority.ALWAYS);
        basicFiltersLabel.setText("Basic filters");
        basicFiltersLabel.setAlignment(Pos.CENTER);
        
        var matrixLabel = new Label();
        matrixLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(matrixLabel, Priority.ALWAYS);
        matrixLabel.setText("Matrix");
        matrixLabel.setAlignment(Pos.CENTER);
        
        brushesAndMatrix.getChildren().addAll(basicFiltersLabel, matrixLabel);

        var pictureFrame = new VBox();
        
        var pictureLabel = new Label();
        pictureLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(pictureLabel, Priority.ALWAYS);
        pictureLabel.setText("Picture");
        pictureLabel.setAlignment(Pos.CENTER);
        
        pictureFrame.getChildren().addAll(pictureLabel);
        
        var ColorAndLayers = new VBox();


        var colorPickerLabel = new Label();
        colorPickerLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(colorPickerLabel, Priority.ALWAYS);
        colorPickerLabel.setText("Color picker");
        colorPickerLabel.setAlignment(Pos.CENTER);

        var brushesLabel = new Label();
        brushesLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(brushesLabel, Priority.ALWAYS);
        brushesLabel.setText("Brushes");
        brushesLabel.setAlignment(Pos.CENTER);
        
        var layersLabel = new Label();
        layersLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(layersLabel, Priority.ALWAYS);
        layersLabel.setText("Layers");
        layersLabel.setAlignment(Pos.CENTER);
        
        ColorAndLayers.getChildren().addAll(colorPickerLabel, brushesLabel, layersLabel);
        
        mainSplitPane.setDividerPositions(0.2, 0.8);

        mainSplitPane.getItems().addAll(brushesAndMatrix, pictureFrame, ColorAndLayers);
        root.getChildren().addAll(menuBar, mainSplitPane);
        
        Scene scene = new Scene(root, 810, 540);
        stage.setTitle("Paint++");
        stage.setMinWidth(810);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }
}
