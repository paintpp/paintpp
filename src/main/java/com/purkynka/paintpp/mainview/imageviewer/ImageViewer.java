package com.purkynka.paintpp.mainview.imageviewer;

import atlantafx.base.theme.Styles;
import com.purkynka.paintpp.logic.imageprovider.ImageProvider;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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
    private StackPane imageStackPane;

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
     * If the {@link ImageViewer} doesn't contain an {@link Image}
     */
    private boolean empty = true;

    /**
     * Minimum amount of zoom
     */
    private static double minZoom;

    /**
     * Maximum amount of zoom
     */
    private static final double MAX_ZOOM = 4d;

    /**
     * The increment to use when zooming
     */
    private static final double ZOOM_INCREMENT = 0.05d;

    /**
     * Current zoom level, used for scaling the {@link #imageCanvas}
     */
    private double currentZoom = 1d;

    private final double[] loadedImageSize = new double[2];
    private final double[] imageScrollPaneSliders = new double[2];
    private boolean isPanning = false;
    
    public ImageViewer() {
        super();

        setupMissingImageLabel();
        setupImageNodes();
        setupImageScrollPane();

        setupImageProvider();

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
     * Creates the {@link #imageStackPane} containing the {@link #imageCanvas}
     */
    private void setupImageNodes() {
        imageStackPane = new StackPane();
        imageStackPane.setPadding(new Insets(100));
        imageStackPane.setAlignment(Pos.CENTER);

        Group imageGroup = new Group();
        imageStackPane.getChildren().add(imageGroup);

        imageCanvas = new Canvas();
        imageGroup.getChildren().add(imageCanvas);
    }

    /**
     * Creates the {@link #imageScrollPane} that contains the {@link #imageStackPane}
     */
    private void setupImageScrollPane() {
        imageScrollPane = new ScrollPane();
        imageScrollPane.setPannable(true);
        imageScrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        imageScrollPane.setContent(imageStackPane);

        imageScrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, _ -> isPanning = true);
        imageScrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, _ -> isPanning = false);
        
        imageScrollPane.vvalueProperty().addListener((_, _, newValue) -> {
            if (isPanning) {
                imageScrollPaneSliders[0] = newValue.doubleValue();
            }
        });
        imageScrollPane.hvalueProperty().addListener((_, _, newValue) -> {
            if (isPanning) {
                imageScrollPaneSliders[1] = newValue.doubleValue();
            }
        });
    }

    /**
     * Sets up the {@link ImageProvider} and related event handlers
     */
    private void setupImageProvider() {
        ImageProvider _ = new ImageProvider();
        ImageProvider.NEW_IMAGE_EVENT.addListener(this::onNewImage);
    }

    /**
     * Runs when a new {@link Image} is provided by the user, either by creating
     * or loading it
     *
     * @param image The new {@link Image} to show
     */
    private void onNewImage(Image image) {
        loadedImageSize[0] = image.getWidth();
        loadedImageSize[1] = image.getHeight();

        if (empty) getChildren().remove(missingImageLabel);

        calculateNewMinZoom();

        imageCanvas.setWidth(loadedImageSize[0]);
        imageCanvas.setHeight(loadedImageSize[1]);
        imageCanvas.setScaleX(currentZoom);
        imageCanvas.setScaleY(currentZoom);

        var canvasContext = imageCanvas.getGraphicsContext2D();
        canvasContext.clearRect(0, 0, loadedImageSize[0], loadedImageSize[1]);
        canvasContext.drawImage(image, 0, 0);

        if (empty) {
            imageScrollPane.widthProperty().addListener((_, _, _) -> calculateNewMinZoom());
            imageScrollPane.heightProperty().addListener((_, _, _) -> calculateNewMinZoom());
        }

        Platform.runLater(() -> {
            setZoom(minZoom);
            imageScrollPaneSliders[0] = 0.5;
            imageScrollPaneSliders[1] = 0.5;
            imageScrollPane.setVvalue(0.5);
            imageScrollPane.setHvalue(0.5);
        });

        empty = false;
    }

    private void calculateNewMinZoom() {
        var contextWidth = this.getWidth() - 200;
        var contextHeight = this.getHeight() - 200;

        var wantedZoomWidth = contextWidth / (loadedImageSize[0]);
        var wantedZoomHeight = contextHeight / (loadedImageSize[1]);

        var newMinZoom = Math.max(Math.min(wantedZoomWidth, wantedZoomHeight), 0.001);

        var newZoom = newMinZoom / (minZoom / currentZoom);
        minZoom = newMinZoom;
        currentZoom = newZoom;

        setZoom(currentZoom);
    }

    /**
     * Calculates a new {@link #currentZoom} value and then scales the {@link #imageCanvas}
     * based on it
     *
     * @param zoomDelta Scroll wheel delta from a {@link ScrollEvent}
     */
    private void onZoom(double zoomDelta) {
        if (zoomDelta == 0) return;

        var lastZoom = currentZoom;
        var zoomChange = zoomDelta > 0 ? ZOOM_INCREMENT : -ZOOM_INCREMENT;
        var zoom = Math.clamp(currentZoom + zoomChange, minZoom, MAX_ZOOM);

        if (lastZoom == zoom) return;

        setZoom(zoom);
    }
    
    private void setZoom(double zoom) {
        if (zoom <= minZoom) zoom = minZoom;
        else if (zoom >= MAX_ZOOM) zoom = MAX_ZOOM;

        var oldZoom = currentZoom;
        currentZoom = zoom;

        imageCanvas.setScaleX(currentZoom);
        imageCanvas.setScaleY(currentZoom);

        Platform.runLater(() -> {
            double scaleRatio = currentZoom / oldZoom;
            imageScrollPane.setVvalue(Math.clamp(imageScrollPaneSliders[0] * scaleRatio, 0, 1));
            imageScrollPane.setHvalue(Math.clamp(imageScrollPaneSliders[1] * scaleRatio, 0, 1));
        });
    }

    /**
     * Sets up a {@link ScrollEvent} filter on the {@link #imageScrollPane},
     * that handles CTRL + Scroll Wheel events and consumes
     * them to cancel moving the {@link #imageScrollPane} when zooming
     */
    private void setupZoomHandler() {
        imageStackPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (empty || !e.isControlDown()) return;
            onZoom(e.getDeltaY());

            e.consume();
        });
    }
}
