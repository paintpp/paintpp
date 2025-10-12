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

import java.util.concurrent.atomic.AtomicBoolean;


public class ImageViewer extends StackPane {
    private static final int BORDER_PADDING = 50;
    
    private Label missingImageLabel;
    private ScrollPane imageScrollPane;
    private StackPane imageCenterPane;
    private StackPane imagePaddingPane;
    private Canvas imageCanvas;

    private boolean empty = true;

    private static double minZoom = 0.2d;
    private static double maxZoom = 5d;
    private static double zoomIncrement = 0.05d;
    private double currentZoom = 1d;

    private final double[] loadedImageSize = new double[2];

    public ImageViewer() {
        super();

        setupNodes();
        setupImageProvider();
        setupZoom(ZoomOption.ZOOM_TO_MOUSE);
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
        imageCenterPane = new StackPane();
        imageCenterPane.setAlignment(Pos.CENTER);
        
        imagePaddingPane = new StackPane();
        imagePaddingPane.setPadding(new Insets(BORDER_PADDING));
        imagePaddingPane.setAlignment(Pos.CENTER);
        imageCenterPane.getChildren().add(imagePaddingPane);

        Group imageGroup = new Group();
        imagePaddingPane.getChildren().add(imageGroup);

        imageCanvas = new Canvas();
        imageGroup.getChildren().add(imageCanvas);
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
            imageScrollPane.widthProperty().addListener((_, _, _) -> calculateNewMinMaxZoom());
            imageScrollPane.heightProperty().addListener((_, _, _) -> calculateNewMinMaxZoom());
        }
        
        imageCanvas.setWidth(loadedImageSize[0]);
        imageCanvas.setHeight(loadedImageSize[1]);

        calculateNewMinMaxZoom();
        
        currentZoom = minZoom;
        setCanvasScaling(currentZoom, currentZoom);
        
        var canvasContext = imageCanvas.getGraphicsContext2D();
        canvasContext.clearRect(0, 0, loadedImageSize[0], loadedImageSize[1]);
        canvasContext.drawImage(image, 0, 0);
        
        empty = false;
    }

    private void calculateNewMinMaxZoom() {
        var imageScrollPaneWidth = imageScrollPane.getWidth();
        var imageScrollPaneHeight = imageScrollPane.getHeight();
        imageCenterPane.setPrefSize(imageScrollPaneWidth, imageScrollPaneHeight);

        var contextWidth = imageScrollPaneWidth - BORDER_PADDING * 2;
        var contextHeight = imageScrollPaneHeight - BORDER_PADDING * 2;

        var wantedMinZoomWidth = contextWidth / (loadedImageSize[0]);
        var wantedMinZoomHeight = contextHeight / (loadedImageSize[1]);

        var newMinZoom = Math.max(Math.min(wantedMinZoomWidth, wantedMinZoomHeight), 0.001);

        var newZoom = newMinZoom / (minZoom / currentZoom);
        minZoom = Math.min(newMinZoom, 1d);
        currentZoom = Math.min(newZoom, 1d);

        var wantedMaxZoomWidth = imageScrollPaneWidth / (loadedImageSize[0]) * 5;
        var wantedMaxZoomHeight = imageScrollPaneHeight / (loadedImageSize[1]) * 5;
        
        maxZoom = Math.max(wantedMaxZoomWidth, wantedMaxZoomHeight);
        
        setCanvasScaling(currentZoom, currentZoom);
    }

    private void setCurrentZoomFromDelta(double zoomDelta) {
        if (zoomDelta == 0) return;

        var lastZoom = currentZoom;
        var zoomChange = zoomDelta > 0 ? zoomIncrement * currentZoom : -zoomIncrement * currentZoom;
        var zoom = Math.clamp(currentZoom + zoomChange, minZoom, maxZoom);

        System.out.println("ImageStackPane width: " + imagePaddingPane.getWidth() + ", height: " + imagePaddingPane.getHeight());
        if (lastZoom == zoom) return;
        currentZoom = zoom;
    }

    private void setScrollPaneSliders(double vValue, double hValue) {
        vValue = Math.clamp(vValue, 0, 1);
        hValue = Math.clamp(hValue, 0, 1);

        imageCenterPane.setPrefSize(imageScrollPane.getWidth(), imageScrollPane.getHeight());

        imageScrollPane.setVvalue(vValue);
        imageScrollPane.setHvalue(hValue);
    }

    private void setCanvasScaling(double xScale, double yScale) {
        xScale = Math.clamp(xScale, minZoom, maxZoom);
        yScale = Math.clamp(yScale, minZoom, maxZoom);

        imageCanvas.setScaleX(xScale);
        imageCanvas.setScaleY(yScale);
    }

    private void setupZoom(ZoomOption zoomOption) {
        switch (zoomOption) {
            case ZOOM_CENTER -> setupCenterZoom();
            case ZOOM_LAST_POSITION -> setupLastPositionZoom();
            case ZOOM_TO_MOUSE -> setupToMouseZoom();
        }
    }

    private void setupCenterZoom() {
        imagePaddingPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (empty || !e.isControlDown()) return;

            setCurrentZoomFromDelta(e.getDeltaY());
            setCanvasScaling(currentZoom, currentZoom);
            
            Platform.runLater(() -> setScrollPaneSliders(0.5, 0.5));
            
            e.consume();
        });
    }

    private void setupLastPositionZoom() {
        AtomicBoolean isPanning = new AtomicBoolean(false);
        final double[] lastNormalizedPosition = new double[2];
        
        imageScrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, _ -> isPanning.set(true));
        imageScrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, _ -> isPanning.set(false));

        imageScrollPane.vvalueProperty().addListener((_, _, newValue) -> {
            if (isPanning.get()) lastNormalizedPosition[0] = newValue.doubleValue();
        });

        imageScrollPane.hvalueProperty().addListener((_, _, newValue) -> {
            if (isPanning.get()) lastNormalizedPosition[1] = newValue.doubleValue();
        });

        imagePaddingPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (empty || !e.isControlDown()) return;

            var oldZoom = currentZoom;
            setCurrentZoomFromDelta(e.getDeltaY());
            setCanvasScaling(currentZoom, currentZoom);

            double scaleRatio = currentZoom / oldZoom;
            Platform.runLater(() -> setScrollPaneSliders(
                    lastNormalizedPosition[0] * scaleRatio,
                    lastNormalizedPosition[1] * scaleRatio
            ));

            e.consume();
        });
    }
    
    private void setupToMouseZoom() {
        final double[] lastNormalizedPosition = new double[2];
        final double[] lastMousePosition = new double[2];
        AtomicBoolean moved = new AtomicBoolean(false);
        
        imageCanvas.setOnMouseMoved((event) -> {
            lastMousePosition[0] = event.getX();
            lastMousePosition[1] = event.getY();
            
            // Cool movement
            // setScrollPaneSliders(lastNormalizedPosition[1], lastNormalizedPosition[0]);
            moved.set(true);
        });

        imagePaddingPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (empty || !e.isControlDown()) return;
            
            var oldZoom = currentZoom;
            setCurrentZoomFromDelta(e.getDeltaY());
            if (oldZoom == currentZoom) {
                e.consume();
                return;
            }
            var zoomingOut = oldZoom < currentZoom;
            
            setCanvasScaling(currentZoom, currentZoom);
            
            if (zoomingOut) {
                var imageCanvasWidth = imageCanvas.getWidth();
                var imageCanvasHeight = imageCanvas.getHeight();

                lastNormalizedPosition[0] = lastMousePosition[0] / imageCanvasWidth;
                lastNormalizedPosition[1] = lastMousePosition[1] / imageCanvasHeight;
            }
            double scaleRatio = currentZoom / oldZoom;

            Platform.runLater(() -> {
                if (!moved.get() || zoomingOut) {
                    setScrollPaneSliders(
                            lastNormalizedPosition[1] * scaleRatio,
                            lastNormalizedPosition[0] * scaleRatio
                    );
                } else {
                    setScrollPaneSliders(
                            lastNormalizedPosition[1],
                            lastNormalizedPosition[0]
                    );
                    Platform.runLater(() -> moved.set(false));
                }
            });
            
            e.consume();
        });
    }
}
