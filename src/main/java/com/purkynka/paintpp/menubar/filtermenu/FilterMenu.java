package com.purkynka.paintpp.menubar.filtermenu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class FilterMenu extends Menu {
    private BasicFilterMenu basicFilterMenu;
    private MenuItem invertColorItem;
    private MenuItem blackAndWhiteItem;
    private MenuItem resetFilters;

    public FilterMenu() {
        super("Filters");

        basicFilterMenu = new BasicFilterMenu();

        invertColorItem = new MenuItem("Invert Color");
        blackAndWhiteItem = new MenuItem("Black and White");
        resetFilters = new MenuItem("Reset Filters");

        getItems().addAll(
                basicFilterMenu,
                invertColorItem,
                blackAndWhiteItem,
                resetFilters
        );
    }
}
