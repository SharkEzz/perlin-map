package application;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import mapping.Map;
import org.fxyz3d.scene.SimpleFPSCamera;
import org.fxyz3d.shapes.composites.SurfacePlot;
import perlin.PerlinNoise;

public class Main extends Application {

    private Scene scene;
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        SimpleFPSCamera camera = new SimpleFPSCamera();

        MeshView mort = new Map(20, 50, 500, 0.009f).meshView();
        mort.setRotate(180);

        Group group = new Group(mort, camera);

        Scene scene = new Scene(group, 1000, 1000, true, SceneAntialiasing.BALANCED);
        camera.loadControlsForScene(scene);
        scene.setCamera(camera.getCamera());

        primaryStage.setScene(scene);
        primaryStage.setTitle("fuk la 3d");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
