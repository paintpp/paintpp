package com.purkynka.paintpp.mainview.imageviewer;

import atlantafx.base.theme.Styles;
import com.purkynka.paintpp.logic.imageprovider.ImageProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class ImageViewer extends StackPane {
    private static final double MAX_ZOOM = 5d;
    private static final double MIN_ZOOM = 0.9d;
    private static final double MIN_ZOOM_LIMIT = 0.001d;
    private static final double ZOOM_SENSITIVITY = 0.05d;

    private static double maxZoom = MAX_ZOOM;
    private static double minZoom = MIN_ZOOM;
    private static double currentZoom = MIN_ZOOM;
    
    private Label missingImageLabel;
    private ScrollPane imageScrollPane;
    private StackPane imageCenterPane;
    private StackPane imagePaddingPane;
    private Canvas imageCanvas;
    
    private boolean empty = true;
    
    private final double[] loadedImageSize = new double[2];

    public ImageViewer(Stage stage) {
        super();

        setupNodes();
        setupImageProvider();
        setupZoomPositionCenter();

        stage.fullScreenProperty().addListener(_ -> onScrollPaneResize());
    }

    private void setupNodes() {
        setupMissingImageLabel();
        setupImageNodes();
        setupImageScrollPane();

        getChildren().addAll(
                missingImageLabel,
                imageScrollPane
        );
    }

    private void setupMissingImageLabel() {
        missingImageLabel = new Label("No Image Selected...");
        missingImageLabel.setAlignment(Pos.CENTER);
        missingImageLabel.getStyleClass().add(Styles.TEXT_SUBTLE);
    }

    private void setupImageNodes() {
        imageCanvas = new Canvas();

        Group imageGroup = new Group();
        imageGroup.getChildren().add(imageCanvas);

        imagePaddingPane = new StackPane();
        imagePaddingPane.setAlignment(Pos.CENTER);
        imagePaddingPane.getChildren().add(imageGroup);

        imageCenterPane = new StackPane();
        imageCenterPane.setAlignment(Pos.CENTER);
        imageCenterPane.getChildren().add(imagePaddingPane);
    }

    private void setupImageScrollPane() {
        imageScrollPane = new ScrollPane();
        imageScrollPane.setPannable(true);
        imageScrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        imageScrollPane.setContent(imageCenterPane);
        
        imageCenterPane.setPrefSize(imageScrollPane.getWidth(), imageScrollPane.getHeight());
    }

    private void setupImageProvider() {
        ImageProvider _ = new ImageProvider();
        ImageProvider.NEW_IMAGE_EVENT.addListener(this::onNewImage);
    }

    private void onNewImage(Image image) {
        loadedImageSize[0] = image.getWidth();
        loadedImageSize[1] = image.getHeight();

        if (empty) {
            getChildren().remove(missingImageLabel);
            imageScrollPane.widthProperty().addListener(_ -> onScrollPaneResize());
            imageScrollPane.heightProperty().addListener(_ -> onScrollPaneResize());
        }

        imageCanvas.setWidth(loadedImageSize[0]);
        imageCanvas.setHeight(loadedImageSize[1]);

        calculateNewMinMaxZoom();
        currentZoom = minZoom;
        setZoom(currentZoom);
        
        var canvasContext = imageCanvas.getGraphicsContext2D();
        canvasContext.clearRect(0, 0, loadedImageSize[0], loadedImageSize[1]);
        canvasContext.drawImage(image, 0, 0);

        setScrollPaneSliders(0.5, 0.5);
        empty = false;
    }

    private void calculateNewMinMaxZoom() {
        var imageScrollPaneWidth = imageScrollPane.getWidth();
        var imageScrollPaneHeight = imageScrollPane.getHeight();

        var wantedMinZoomWidth = imageScrollPaneWidth / loadedImageSize[0] * MIN_ZOOM;
        var wantedMinZoomHeight = imageScrollPaneHeight / loadedImageSize[1] * MIN_ZOOM;

        var newMinZoom = Math.max(Math.min(wantedMinZoomWidth, wantedMinZoomHeight), MIN_ZOOM_LIMIT);

        newMinZoom = Math.min(newMinZoom, MIN_ZOOM);
        currentZoom = newMinZoom / (minZoom / currentZoom);
        minZoom = newMinZoom;
        
        var wantedMaxZoomWidth = imageScrollPaneWidth / loadedImageSize[0] * MAX_ZOOM;
        var wantedMaxZoomHeight = imageScrollPaneHeight / loadedImageSize[1] * MAX_ZOOM;

        maxZoom = Math.max(wantedMaxZoomWidth, wantedMaxZoomHeight);
    }
    
    private void onScrollPaneResize() {
        calculateNewMinMaxZoom();
        
        setScrollPaneSliders(
                imageScrollPane.getHvalue(),
                imageScrollPane.getVvalue()
        );
    }

    private void setCurrentZoomFromDelta(double zoomDelta) {
        if (zoomDelta == 0) return;

        var lastZoom = currentZoom;
        var zoomChange = zoomDelta > 0 ? ZOOM_SENSITIVITY * currentZoom : -ZOOM_SENSITIVITY * currentZoom;
        var zoom = Math.clamp(currentZoom + zoomChange, minZoom, maxZoom);

        if (lastZoom == zoom) return;
        currentZoom = zoom;
    }

    private void setScrollPaneSliders(double hValue, double vValue) {
        hValue = Math.clamp(hValue, 0, 1);
        vValue = Math.clamp(vValue, 0, 1);

        imageCenterPane.setPrefSize(imageScrollPane.getWidth(), imageScrollPane.getHeight());

        imageScrollPane.setHvalue(hValue);
        imageScrollPane.setVvalue(vValue);
    }

    private void setZoom(double zoom) {
        zoom = Math.clamp(zoom, minZoom, maxZoom);

        imageCanvas.setScaleX(zoom);
        imageCanvas.setScaleY(zoom);
        
        var topBottom = imageScrollPane.getHeight() / 2;
        var leftRight = imageScrollPane.getWidth() / 2;
        imagePaddingPane.setPadding(new Insets(topBottom, leftRight, topBottom, leftRight));
    }

    private void setupZoomPositionCenter() {
        imageScrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (empty || !e.isControlDown()) return;
            
            var oldZoom = currentZoom;
            setCurrentZoomFromDelta(e.getDeltaY());
            
            if (currentZoom == oldZoom) {
                e.consume();
                return;
            }
            
            setZoom(currentZoom);
            
            double scaleRatio = currentZoom / oldZoom;
            setScrollPaneSliders(
                    imageScrollPane.getHvalue() * scaleRatio,
                    imageScrollPane.getVvalue() * scaleRatio
            );
            
            e.consume();
        });
    }
}
