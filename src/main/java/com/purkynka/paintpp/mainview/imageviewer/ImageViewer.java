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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class ImageViewer extends StackPane {
    private static final ZoomOption zoomOption = ZoomOption.ZOOM_TOWARDS_MOUSE_POSITION;
    
    private Label missingImageLabel;
    private ScrollPane imageScrollPane;
    private StackPane imageCenterPane;
    private StackPane imagePaddingPane;
    private Canvas imageCanvas;

    private boolean empty = true;

    private static double minZoom = 0.2d;
    private static double maxZoom = 5d;
    private double currentZoom = 1d;

    private final double[] loadedImageSize = new double[2];
    private final double[] imageScrollPaneSliders = new double[2];

    public ImageViewer(Stage stage) {
        super();

        setupNodes();
        setupImageProvider();
        setupZoom(zoomOption);

        stage.fullScreenProperty().addListener((_, _, _) -> Platform.runLater(() -> setScrollPaneSliders(imageScrollPaneSliders[0], imageScrollPaneSliders[1])));
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

        imageScrollPane.vvalueProperty().addListener((_, _, newValue) -> imageScrollPaneSliders[0] = newValue.doubleValue());
        imageScrollPane.hvalueProperty().addListener((_, _, newValue) -> imageScrollPaneSliders[1] = newValue.doubleValue());
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
        setZoom(currentZoom);
        
        var canvasContext = imageCanvas.getGraphicsContext2D();
        canvasContext.clearRect(0, 0, loadedImageSize[0], loadedImageSize[1]);
        canvasContext.drawImage(image, 0, 0);

        Platform.runLater(() -> Platform.runLater(() -> setScrollPaneSliders(0.5, 0.5)));
        empty = false;
    }

    private void calculateNewMinMaxZoom() {
        var imageScrollPaneWidth = imageScrollPane.getWidth();
        var imageScrollPaneHeight = imageScrollPane.getHeight();
        imageCenterPane.setPrefSize(imageScrollPaneWidth, imageScrollPaneHeight);

        var contextWidth = imageScrollPaneWidth - Math.min(loadedImageSize[0] / 10, 100);
        var contextHeight = imageScrollPaneHeight - Math.min(loadedImageSize[1] / 10, 100);

        var wantedMinZoomWidth = contextWidth / (loadedImageSize[0]);
        var wantedMinZoomHeight = contextHeight / (loadedImageSize[1]);

        var newMinZoom = Math.max(Math.min(wantedMinZoomWidth, wantedMinZoomHeight), 0.001);

        var newZoom = newMinZoom / (minZoom / currentZoom);
        minZoom = Math.min(newMinZoom, 1d);
        var oldZoom = currentZoom;
        currentZoom = Math.min(newZoom, 1d);

        var wantedMaxZoomWidth = imageScrollPaneWidth / (loadedImageSize[0]) * 5;
        var wantedMaxZoomHeight = imageScrollPaneHeight / (loadedImageSize[1]) * 5;

        maxZoom = Math.max(wantedMaxZoomWidth, wantedMaxZoomHeight);

        setZoom(currentZoom);
        
        double scaleRatio = currentZoom / oldZoom;
        setScrollPaneSliders(
                imageScrollPane.getVvalue() * scaleRatio,
                imageScrollPane.getHvalue() * scaleRatio
        );
    }

    private void setCurrentZoomFromDelta(double zoomDelta) {
        if (zoomDelta == 0) return;

        var lastZoom = currentZoom;
        double zoomIncrement = 0.05d;
        var zoomChange = zoomDelta > 0 ? zoomIncrement * currentZoom : -zoomIncrement * currentZoom;
        var zoom = Math.clamp(currentZoom + zoomChange, minZoom, maxZoom);

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

    private void setZoom(double zoom) {
        zoom = Math.clamp(zoom, minZoom, maxZoom);

        imageCanvas.setScaleX(zoom);
        imageCanvas.setScaleY(zoom);
        
        var topBottom = imageScrollPane.getHeight() / 2;
        var leftRight = imageScrollPane.getWidth() / 2;
        imagePaddingPane.setPadding(new Insets(topBottom, leftRight, topBottom, leftRight));
    }

    private void setupZoom(ZoomOption zoomOption) {
        switch (zoomOption) {
            case ZOOM_IMAGE_CENTER -> setupZoomImageCenter();
            case ZOOM_POSITION_CENTER -> setupZoomPositionCenter();
            case ZOOM_LAST_MOUSE_POSITION -> setupZoomLastMousePosition();
            case ZOOM_TOWARDS_MOUSE_POSITION -> setupZoomTowardsMousePosition();
        }
    }
    
    private void setupZoomImageCenter() {
        imageScrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (empty || !e.isControlDown()) return;

            var oldZoom = currentZoom;
            setCurrentZoomFromDelta(e.getDeltaY());

            if (currentZoom == oldZoom) {
                e.consume();
                return;
            }
            
            setZoom(currentZoom);

            setScrollPaneSliders(0.5, 0.5);

            e.consume();
        });
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
            imageScrollPane.setHvalue(imageScrollPane.getHvalue() * scaleRatio);
            imageScrollPane.setVvalue(imageScrollPane.getVvalue() * scaleRatio);
            
            e.consume();
        });
    }

    private void setupZoomLastMousePosition() {
        final double[] lastMousePosition = new double[2];

        imageCanvas.setOnMouseMoved((event) -> {
            lastMousePosition[0] = event.getX();
            lastMousePosition[1] = event.getY();
        });

        imageScrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (empty || !e.isControlDown()) return;

            var oldZoom = currentZoom;
            setCurrentZoomFromDelta(e.getDeltaY());

            if (currentZoom == oldZoom) {
                e.consume();
                return;
            }

            setZoom(currentZoom);
            
            var hValue = imageScrollPane.getHvalue();
            var vValue = imageScrollPane.getVvalue();

            var zoomingIn = oldZoom < currentZoom;
            if (zoomingIn) {
                var imageCanvasWidth = imageCanvas.getWidth();
                var imageCanvasHeight = imageCanvas.getHeight();

                hValue = lastMousePosition[0] / imageCanvasWidth;
                vValue = lastMousePosition[1] / imageCanvasHeight;
            }
            
            double scaleRatio = currentZoom / oldZoom;
            setScrollPaneSliders(
                    vValue * scaleRatio,
                    hValue * scaleRatio
            );
        });
    }

    private void setupZoomTowardsMousePosition() {
        imageScrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (empty || !e.isControlDown()) return;

            var oldZoom = currentZoom;
            setCurrentZoomFromDelta(e.getDeltaY());

            if (currentZoom == oldZoom) {
                e.consume();
                return;
            }

            setZoom(currentZoom);
            
            double range = Math.min(0.1, 0.1 / (currentZoom * 2));
            double offsetX, offsetY;
            
            var zoomingIn = oldZoom < currentZoom;
            if (zoomingIn) {
                offsetX = 1 + ((e.getX() / imageScrollPane.getWidth()) - 0.5) * (range * 2);
                offsetY = 1 + ((e.getY() / imageScrollPane.getHeight()) - 0.5) * (range * 2);
            } else {
                offsetX = 1 - ((e.getX() / imageScrollPane.getWidth()) - 0.5) * (range * 2);
                offsetY = 1 - ((e.getY() / imageScrollPane.getHeight()) - 0.5) * (range * 2);
            }
            
            double scaleRatio = currentZoom / oldZoom;
            
            if (zoomingIn) {
                setScrollPaneSliders(
                        imageScrollPane.getVvalue() * scaleRatio * offsetY, 
                        imageScrollPane.getHvalue() * scaleRatio * offsetX
                );
            } else {
                imageScrollPane.setHvalue(imageScrollPane.getHvalue() * scaleRatio);
                imageScrollPane.setVvalue(imageScrollPane.getVvalue() * scaleRatio);
                
                /* Centering experiment
                double hValue = imageScrollPane.getHvalue();
                double vValue = imageScrollPane.getVvalue();
                double zoomProgress = (currentZoom - maxZoom) / (minZoom - maxZoom);
                
                hValue += (0.5 - hValue) * Math.sqrt(zoomProgress) * 0.5;
                vValue += (0.5 - vValue) * Math.sqrt(zoomProgress) * 0.5;

                imageScrollPane.setHvalue(hValue);
                imageScrollPane.setVvalue(vValue);
                */
            }
            
            e.consume();
        });
    }
}
