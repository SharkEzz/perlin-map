module javafx {
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.controls;
    requires org.fxyz3d.core;

    opens application;
    opens mapping;
    opens perlin;
}