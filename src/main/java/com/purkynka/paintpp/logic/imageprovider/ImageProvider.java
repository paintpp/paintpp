package com.purkynka.paintpp.logic.imageprovider;

import com.purkynka.paintpp.event.ConsumerEvent;
import com.purkynka.paintpp.menubar.filemenu.FileMenu;
import javafx.scene.image.Image;

public class ImageProvider {
    public static ConsumerEvent<Image> NEW_IMAGE_EVENT = new ConsumerEvent<>();

    private Image originalImage;

    public ImageProvider() {
        FileMenu.LOAD_IMAGE_EVENT.addListener(this::onLoadImage);
    }

    private void onLoadImage(String imageURI) {
        originalImage = new Image(imageURI);

        NEW_IMAGE_EVENT.send(originalImage);
    }
}
