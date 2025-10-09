package com.purkynka.paintpp.menubar.filemenu;

import com.purkynka.paintpp.event.ConsumerEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;

import static com.purkynka.paintpp.Paintpp.PRIMARY_STAGE;

public class FileMenu extends Menu {
    public static final ConsumerEvent<String> LOAD_IMAGE_EVENT = new ConsumerEvent<>();

    private MenuItem newBlankImageItem;
    private MenuItem loadImageItem;
    private MenuItem generateImageItem;

    private MenuItem saveItem;
    private MenuItem saveAsItem;

    private MenuItem exitItem;

    private FileChooser fileChooser;

    public FileMenu() {
        super("File");

        setupMenuItems();
        setupFileChooser();
    }

    private void setupMenuItems() {
        newBlankImageItem = new MenuItem("New Blank Image");
        newBlankImageItem.setOnAction(_ -> onNewBlankImage());

        loadImageItem = new MenuItem("Load Image");
        loadImageItem.setOnAction(_ -> onLoadImage());

        generateImageItem = new MenuItem("Generate Image");
        generateImageItem.setOnAction(_ -> onGenerateImage());

        saveItem = new MenuItem("Save");
        saveItem.setOnAction(_ -> onSave());

        saveAsItem = new MenuItem("Save As");
        saveAsItem.setOnAction(_ -> onSaveAs());

        exitItem = new MenuItem("Exit");
        exitItem.setOnAction(_ -> onExit());

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

    private void setupFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All images", "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("Other", "*.*")
        );
    }

    private void onNewBlankImage() {}
    private void onLoadImage() {
        var file = fileChooser.showOpenDialog(PRIMARY_STAGE);
        if (file == null) return;

        LOAD_IMAGE_EVENT.send(file.toURI().toString());
    }
    private void onGenerateImage() {}
    private void onSave() {}
    private void onSaveAs() {}
    private void onExit() {}
}
