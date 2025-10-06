package com.purkynka.paintpp;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Paintpp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        
        FXMLLoader fxmlLoader = new FXMLLoader(Paintpp.class.getResource("main-view.fxml"));
        
        Scene scene = new Scene(fxmlLoader.load(), 810, 540);
        
        stage.setTitle("Paint++");
        stage.setMinWidth(810);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }
}
