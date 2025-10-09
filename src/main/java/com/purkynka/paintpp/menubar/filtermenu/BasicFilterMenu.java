package com.purkynka.paintpp.menubar.filtermenu;

import com.purkynka.paintpp.menubar.menuitem.SliderMenuItem;
import javafx.scene.control.Menu;

public class BasicFilterMenu extends Menu {
    private SliderMenuItem sharpenItem;
    private SliderMenuItem saturationItem;

    public BasicFilterMenu() {
        super("Basic Filters");

        sharpenItem = new SliderMenuItem("Sharpness");
        saturationItem = new SliderMenuItem("Saturation");

        getItems().addAll(
                sharpenItem,
                saturationItem
        );
    }
}
