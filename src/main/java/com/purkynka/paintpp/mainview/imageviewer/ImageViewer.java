package com.purkynka.paintpp.mainview.imageviewer;

import atlantafx.base.theme.Styles;
import com.purkynka.paintpp.logic.imageprovider.ImageProvider;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ImageViewer extends StackPane {
    private Label missingImageLabel;
    private ScrollPane innerScrollPane;
    private Canvas imageCanvas;

    private ImageProvider imageProvider;

    public ImageViewer() {
        super();

        setupMissingImageLabel();
        setupInnerScrollPane();
        setupResizeHandler();
        setupImageProvider();

        getChildren().addAll(
                missingImageLabel,
                innerScrollPane
        );
    }

    private void setupMissingImageLabel() {
        missingImageLabel = new Label("No Image Selected...");
        missingImageLabel.setAlignment(Pos.CENTER);
        missingImageLabel.getStyleClass().add(Styles.TEXT_SUBTLE);
    }

    private void setupInnerScrollPane() {
        innerScrollPane = new ScrollPane();
        innerScrollPane.setPannable(true);
        innerScrollPane.setPrefSize(getWidth(), getHeight());
    }

    private void resizeInnerScrollPane() {
        if (imageCanvas == null) return;

        var newWidth = Math.min(getWidth(), imageCanvas.getWidth());
        var newHeight =  Math.min(getHeight(), imageCanvas.getHeight());

        innerScrollPane.setMaxSize(newWidth, newHeight);

        System.out.println(innerScrollPane.getWidth());
    }

    private void setupResizeHandler() {
        widthProperty().addListener(_ -> resizeInnerScrollPane());
        heightProperty().addListener(_ -> resizeInnerScrollPane());
    }

    private void onNewImage(Image image) {
        var children = getChildren();

        if (imageCanvas == null) children.remove(missingImageLabel);
        imageCanvas = new Canvas(image.getWidth(), image.getHeight());

        var graphicsContext = imageCanvas.getGraphicsContext2D();
        graphicsContext.drawImage(image, 0, 0);

        resizeInnerScrollPane();
        innerScrollPane.setContent(imageCanvas);
    }

    private void setupImageProvider() {
        imageProvider = new ImageProvider();
        ImageProvider.NEW_IMAGE_EVENT.addListener(this::onNewImage);
    }
}
