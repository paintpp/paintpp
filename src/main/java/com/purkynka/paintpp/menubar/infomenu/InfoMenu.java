package com.purkynka.paintpp.menubar.infomenu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class InfoMenu extends Menu {
    private MenuItem creditsItem;

    public InfoMenu() {
        super("Info");

        creditsItem = new MenuItem("Credits");

        getItems().addAll(
                creditsItem
        );
    }
}
