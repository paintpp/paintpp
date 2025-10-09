package com.purkynka.paintpp.mainview.leftsidepanel;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

public class LeftSidePanel extends SplitPane {
    private ToolsPane toolsPane;
    private ToolSettingsPane toolSettinsPane;

    public LeftSidePanel() {
        super();

        setOrientation(Orientation.VERTICAL);

        toolsPane = new ToolsPane();
        toolSettinsPane = new ToolSettingsPane();

        getItems().addAll(
                toolsPane,
                toolSettinsPane
        );
    }
}
