package com.purkynka.paintpp.mainview;

import com.purkynka.paintpp.mainview.imageviewer.ImageViewer;
import com.purkynka.paintpp.mainview.leftsidepanel.LeftSidePanel;
import com.purkynka.paintpp.mainview.rightsidepanel.RightSidePanel;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView extends SplitPane {
    private LeftSidePanel leftSidePanel;
    private ImageViewer imageViewer;
    private RightSidePanel rightSidePanel;

    public MainView(Stage stage) {
        super();

        leftSidePanel = new LeftSidePanel();
        imageViewer = new ImageViewer(stage);
        rightSidePanel = new RightSidePanel();

        VBox.setVgrow(this, Priority.ALWAYS);

        getItems().addAll(
                leftSidePanel,
                imageViewer,
                rightSidePanel
        );

        setDividerPositions(0.2, 0.8);
    }
}
