package com.purkynka.paintpp.menubar.filemenu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class FileMenu extends Menu {
    private MenuItem newBlankImageItem;
    private MenuItem loadImageItem;
    private MenuItem generateImageItem;

    private MenuItem saveItem;
    private MenuItem saveAsItem;

    private MenuItem exitItem;

    public FileMenu() {
        super("File");

        newBlankImageItem = new MenuItem("New Blank Image");
        loadImageItem = new MenuItem("Load Image");
        generateImageItem = new MenuItem("Generate Image");
        saveItem = new MenuItem("Save");
        saveAsItem = new MenuItem("Save As");
        exitItem = new MenuItem("Exit");

        getItems().addAll(
                newBlankImageItem,
                loadImageItem,
                generateImageItem,
                new SeparatorMenuItem(),
                saveItem,
                saveAsItem,
                new SeparatorMenuItem(),
                exitItem
        );
    }
}
