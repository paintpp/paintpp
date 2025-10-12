package com.purkynka.paintpp;

import atlantafx.base.theme.CupertinoDark;
import com.purkynka.paintpp.mainview.MainView;
import com.purkynka.paintpp.menubar.MenuBar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Paintpp extends Application {
    public static Stage PRIMARY_STAGE;

    @Override
    public void start(Stage stage) {
        PRIMARY_STAGE = stage;

        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

        var root = new VBox();

        var menuBar = new MenuBar();
        var mainView = new MainView(stage);

        root.getChildren().addAll(menuBar, mainView);
        
        Scene scene = new Scene(root, 810, 540);
        stage.setTitle("Paint++");
        stage.setMinWidth(810);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }
}
