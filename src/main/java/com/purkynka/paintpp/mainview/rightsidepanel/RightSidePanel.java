package com.purkynka.paintpp.mainview.rightsidepanel;

import com.purkynka.paintpp.mainview.panes.ColorPickerPane;
import com.purkynka.paintpp.mainview.panes.LayersAndFiltersPane;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

public class RightSidePanel extends SplitPane {
    private ColorPickerPane colorPickerPane;
    private LayersAndFiltersPane layersAndFiltersPane;

    public RightSidePanel() {
        super();

        setOrientation(Orientation.VERTICAL);

        colorPickerPane = new ColorPickerPane();
        layersAndFiltersPane = new LayersAndFiltersPane();

        getItems().addAll(
                colorPickerPane,
                layersAndFiltersPane
        );
    }
}
