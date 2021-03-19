package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import mapping.Map;
import org.fxyz3d.shapes.composites.SurfacePlot;
import org.fxyz3d.shapes.primitives.Surface3DMesh;
import perlin.PerlinNoise;

public class Controller {

    @FXML
    BorderPane bPane;

    @FXML
    public void initialize()
    {
//        SurfacePlot map = (new Map((new PerlinNoise(500, 500)).noise())).mesh();
//
//        this.bPane.getChildren().add(map);
    }

    public void addNode(Node n)
    {
        this.bPane.getChildren().add(n);
    }

    public BorderPane getbPane()
    {
        return this.bPane;
    }


}
