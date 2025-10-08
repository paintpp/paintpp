package com.purkynka.paintpp;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Paintpp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

        var root = new VBox();

        var mainMenuBar = new MenuBar();

        var fileMenu = new Menu();
        fileMenu.setText("File");

        var newBlank = new MenuItem();
        newBlank.setText("New blank image");
        var loadFile = new MenuItem();
        loadFile.setText("Load image");
        var generateImage = new MenuItem();
        generateImage.setText("Generate image");
        var saveImage = new MenuItem();
        saveImage.setText("Save image");
        var saveAs = new MenuItem();
        saveAs.setText("Save as image");

        fileMenu.getItems().addAll(newBlank, loadFile, generateImage, saveImage, saveAs);

        var filterMenu = new Menu();
        filterMenu.setText("Filters");

        var basicFilters = new Menu();
        basicFilters.setText("Basic filters");

        var sharpen = new CustomMenuItem();
        var sharpenGrid = new VBox();
        var sharpenLabel = new Label();
        sharpenLabel.setText("Sharpen");
        var sharpenSlider = new Slider();
        sharpenSlider.setValue(50);
        sharpenGrid.getChildren().addAll(sharpenLabel, sharpenSlider);
        sharpen.setContent(sharpenGrid);

        var saturation = new CustomMenuItem();
        var saturationGrid = new VBox();
        var saturationLabel = new Label();
        saturationLabel.setText("Saturation");
        var saturationSlider = new Slider();
        saturationSlider.setValue(50);
        saturationGrid.getChildren().addAll(saturationLabel, saturationSlider);
        saturation.setContent(saturationGrid);

        basicFilters.getItems().addAll(sharpen, saturation);

        filterMenu.getItems().add(basicFilters);

        var invertColor =  new MenuItem();
        invertColor.setText("Invert color");
        var blackAndWhite = new MenuItem();
        blackAndWhite.setText("Black and White");
        var resetFilters = new MenuItem();
        resetFilters.setText("Reset filters");

        filterMenu.getItems().addAll(invertColor, blackAndWhite, resetFilters);

        var infoMenu = new Menu();
        infoMenu.setText("Info");

        var credits = new MenuItem();
        credits.setText("Credits");

        infoMenu.getItems().addAll(credits);

        mainMenuBar.getMenus().addAll(fileMenu, filterMenu, infoMenu);

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
        root.getChildren().addAll(mainMenuBar, mainSplitPane);
        
        Scene scene = new Scene(root, 810, 540);
        stage.setTitle("Paint++");
        stage.setMinWidth(810);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }
}
