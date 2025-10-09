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
import com.purkynka.paintpp.mainview.MainView;

/**
 * Represents the middle of the parent {@link MainView}, displaying
 * the {@link Image} being currently edited
 */
public class ImageViewer extends StackPane {
    /**
     * {@link Label} shown before an image is created or loaded
     */
    private Label missingImageLabel;

    /**
     * Required for zooming to work properly, due to
     * {@link ScrollPane} nodes not understanding scale without a {@link Group}
     */
    private Group imageGroup;

    /**
     * {@link Canvas} holding the currently visible {@link Image}
     */
    private Canvas imageCanvas;

    /**
     * Inner {@link ScrollPane} that actually holds the {@link #imageCanvas},
     * to allow panning when the image is too large
     */
    private ScrollPane imageScrollPane;

    /**
     * Provides the original {@link Image} that was created or loaded
     */
    private ImageProvider imageProvider;

    /**
     * If the {@link ImageViewer} doesn't contain an {@link Image}
     */
    private boolean empty = true;

    /**
     * Minimum amount of zoom
     */
    private static final double MIN_ZOOM = 0.25d;

    /**
     * Maximum amount of zoom
     */
    private static final double MAX_ZOOM = 4d;

    /**
     * The increment to use when zooming
     */
    private static final double ZOOM_INCREMENT = 0.25d;

    /**
     * Current zoom level, used for scaling the {@link #imageCanvas}
     */
    private double currentZoom = 1d;

    public ImageViewer() {
        super();

        setupMissingImageLabel();
        setupImageNodes();
        setupImageScrollPane();

        setupImageProvider();

        setupResizeHandlers();
        setupZoomHandler();

        getChildren().addAll(
                missingImageLabel,
                imageScrollPane
        );
    }

    /**
     * Creates the {@link #missingImageLabel} shown before an {@link Image}
     * is created or loaded
     */
    private void setupMissingImageLabel() {
        missingImageLabel = new Label("No Image Selected...");
        missingImageLabel.setAlignment(Pos.CENTER);
        missingImageLabel.getStyleClass().add(Styles.TEXT_SUBTLE);
    }

    /**
     * Creates the {@link #imageGroup} containing the {@link #imageCanvas}
     */
    private void setupImageNodes() {
        imageGroup = new Group();

        imageCanvas = new Canvas();
        imageGroup.getChildren().add(imageCanvas);
    }

    /**
     * Creates the {@link #imageScrollPane} that contains the {@link #imageGroup}
     */
    private void setupImageScrollPane() {
        imageScrollPane = new ScrollPane(imageGroup);
        imageScrollPane.setPannable(true);
        imageScrollPane.setContent(imageGroup);
    }

    /**
     * Sets up the {@link ImageProvider} and related event handlers
     */
    private void setupImageProvider() {
        imageProvider = new ImageProvider();
        ImageProvider.NEW_IMAGE_EVENT.addListener(this::onNewImage);
    }

    /**
     * Runs when a new {@link Image} is provided by the user, either by creating
     * or loading it
     * @param image The new {@link Image} to show
     */
    private void onNewImage(Image image) {
        var imageWidth = image.getWidth();
        var imageHeight = image.getHeight();

        if (empty) getChildren().remove(missingImageLabel);

        currentZoom = 1d;
        imageCanvas.setWidth(imageWidth);
        imageCanvas.setHeight(imageHeight);

        var canvasContext = imageCanvas.getGraphicsContext2D();
        canvasContext.clearRect(0, 0, imageWidth, imageHeight);
        canvasContext.drawImage(image, 0, 0);

        resizeInnerScrollPane();

        empty = false;
    }

    /**
     * Resizes the {@link #imageScrollPane} to either the size of the currently shown {@link Image}
     * or the available size provided by the parent {@link MainView}
     */
    private void resizeInnerScrollPane() {
        var originalImage = imageProvider.getOriginalImage();
        if (originalImage == null) return;

        var newWidth = Math.min(getWidth(), originalImage.getWidth() * currentZoom);
        var newHeight = Math.min(getHeight(), originalImage.getHeight() * currentZoom);

        imageScrollPane.setMaxSize(newWidth, newHeight);
    }

    /**
     * Sets up listeners that run whenever the size provided by the parent {@link MainView} changes,
     * that resize the {@link #imageScrollPane}
     */
    private void setupResizeHandlers() {
        widthProperty().addListener(_ -> resizeInnerScrollPane());
        heightProperty().addListener(_ -> resizeInnerScrollPane());
    }

    /**
     * Calculates a new {@link #currentZoom} value and then scales the {@link #imageCanvas}
     * based on it
     * @param zoomDelta Scroll wheel delta from a {@link ScrollEvent}
     */
    private void onZoom(double zoomDelta) {
        if (zoomDelta == 0) return;

        var lastZoom = currentZoom;
        var zoomChange = zoomDelta > 0 ? ZOOM_INCREMENT : -ZOOM_INCREMENT;
        currentZoom = Math.clamp(currentZoom + zoomChange, MIN_ZOOM, MAX_ZOOM);

        if (lastZoom == currentZoom) return;

        imageCanvas.setScaleX(currentZoom);
        imageCanvas.setScaleY(currentZoom);

        resizeInnerScrollPane();
    }

    /**
     * Sets up a {@link ScrollEvent} filter on the {@link #imageScrollPane},
     * that handles CTRL + Scroll Wheel events and consumes
     * them to cancel moving the {@link #imageScrollPane} when zooming
     */
    private void setupZoomHandler() {
        imageScrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (empty || !e.isControlDown()) return;

            onZoom(e.getDeltaY());

            e.consume();
        });
    }
}
