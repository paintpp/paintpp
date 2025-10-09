package com.purkynka.paintpp.mainview.imageviewer;

import atlantafx.base.theme.Styles;
import com.purkynka.paintpp.logic.imageprovider.ImageProvider;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

public class ImageViewer extends StackPane {
    private Label missingImageLabel;
    private ScrollPane innerScrollPane;
    private Group imageContainer;
    private Scale imageScale;
    private Canvas imageCanvas;

    private ImageProvider imageProvider;

    private double currentZoom = 1d;

    public ImageViewer() {
        super();

        setupMissingImageLabel();
        setupInnerScrollPane();
        setupResizeHandler();
        setupImageContainerAndScale();
        setupImageProvider();
        setupZoomHandlers();

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

        var newWidth = Math.min(getWidth(), imageCanvas.getWidth() * currentZoom);
        var newHeight =  Math.min(getHeight(), imageCanvas.getHeight() * currentZoom);

        innerScrollPane.setMaxSize(newWidth, newHeight);
    }

    private void setupResizeHandler() {
        widthProperty().addListener(_ -> resizeInnerScrollPane());
        heightProperty().addListener(_ -> resizeInnerScrollPane());
    }

    private void setupImageContainerAndScale() {
        imageContainer = new Group();
        imageScale = new Scale(currentZoom, currentZoom, 0, 0);
    }

    private void onNewImage(Image image) {
        var imageWidth = image.getWidth();
        var imageHeight = image.getHeight();

        if (imageCanvas == null) getChildren().remove(missingImageLabel);
        else imageContainer.getChildren().remove(imageCanvas);

        imageCanvas = new Canvas(imageWidth, imageHeight);
        imageCanvas.getTransforms().add(imageScale);

        var graphicsContext = imageCanvas.getGraphicsContext2D();
        graphicsContext.drawImage(image, 0, 0);

        resizeInnerScrollPane();
        imageContainer.getChildren().add(imageCanvas);
        innerScrollPane.setContent(imageContainer);
    }

    private void setupImageProvider() {
        imageProvider = new ImageProvider();
        ImageProvider.NEW_IMAGE_EVENT.addListener(this::onNewImage);
    }

    private void resizeImage() {
        imageScale.setX(currentZoom);
        imageScale.setY(currentZoom);

        resizeInnerScrollPane();
    }

    private void setupZoomHandlers() {
        innerScrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (!e.isControlDown()) return;

            var changedBy = e.getDeltaY();
            if (changedBy == 0) return;

            var lastZoom = currentZoom;
            var modifier = changedBy > 0 ? 0.25 : -0.25;
            currentZoom = Math.min(4, Math.max(0.25, currentZoom + modifier));

            if (lastZoom != currentZoom) {
                resizeImage();
            }

            e.consume();
        });
    }
}
