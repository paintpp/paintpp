module com.purkynka.paintpp {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;


    exports com.purkynka.paintpp;
    opens com.purkynka.paintpp to javafx.fxml;
}