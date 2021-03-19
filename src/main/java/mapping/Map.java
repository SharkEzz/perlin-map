package mapping;

import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import perlin.PerlinNoise;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Map {

    private float[][] data;
    private Mesh meshObject;
    private float scale;
    private int waterLevel;
    private int dimention;
    private WritableImage image;

    public Map(float scale, long seed, int dimensions, float noiseValue, float waterLever)
    {
        this.data = new float[dimensions][dimensions];
        this.scale = scale;
        this.waterLevel = (int) Math.floor(waterLever % 1f * (dimensions / 6));
        this.dimention = dimensions;
        PerlinNoise noise = new PerlinNoise(seed);

        this.image = new WritableImage(dimensions, dimensions);
        PixelWriter pixelWriter = this.image.getPixelWriter();

        float xOf = 0f;
        for(int x = 0; x < dimensions; x++)
        {
            float zOf = 0f;
            for(int z = 0; z < dimensions; z++)
            {
                float res = (float)noise.noise(xOf, zOf);
                float out = Map.map(res, -1, 1, 0, dimensions / 6);
                this.data[x][z] = out;
                pixelWriter.setColor(x, z, res > 0.6 ? Color.WHITE : (res > 0.3 ? Color.GRAY : res > -0.1 ? Color.GREEN : Color.YELLOW));
                zOf += noiseValue;
            }
            xOf += noiseValue;
        }
        this.meshObject = this.mesh(data);
                try {
        File file = new File("test.png");
        ImageIO.write(SwingFXUtils.fromFXImage(this.image, null),
                    "png", file);
                } catch (IOException ex) {

                }
        System.out.println("generated");
    }

    private TriangleMesh mesh(float[][] generatedYValues) {
        int sizeX = this.data.length, sizeZ = this.data[0].length;
        ObservableFloatArray points = FXCollections.observableFloatArray();
        ObservableFloatArray textures = FXCollections.observableFloatArray();
        ObservableIntegerArray faces = FXCollections.observableIntegerArray();
        Integer[][] vertexID = new Integer[sizeX][sizeZ];
        int ctr = 0;
        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeZ; z++) {
                float tmpX = x * this.scale;
                float tmpY = generatedYValues[x][z] * this.scale;
                float tmpZ = z * this.scale;
                if (z + 1 < sizeZ && x + 1 < sizeX) {
                    Integer vCurrent = vertexID[x][z];
                    Integer vDown = vertexID[x][z + 1];
                    Integer vRight = vertexID[x + 1][z];
                    if (vCurrent == null) {
                        points.addAll(tmpX);
                        points.addAll(tmpY);
                        points.addAll(tmpZ);
                        textures.addAll((float)x / this.dimention,(float) z/ this.dimention);
                        vertexID[x][z] = ctr++;
                        vCurrent = vertexID[x][z];
                    }
                    if (vDown == null) {
                        points.addAll(tmpX);
                        points.addAll(generatedYValues[x][z + 1] * this.scale);
                        points.addAll(tmpZ + this.scale);
                        textures.addAll((float)x/ this.dimention, (float)(z + 1)/ this.dimention);
                        vertexID[x][z + 1] = ctr++;
                        vDown = vertexID[x][z + 1];
                    }
                    if (vRight == null) {
                        points.addAll(tmpX + this.scale);
                        points.addAll(generatedYValues[x + 1][z] * this.scale);
                        points.addAll(tmpZ);
                        textures.addAll((float)(x + 1)/ this.dimention, (float)z/ this.dimention);
                        vertexID[x + 1][z] = ctr++;
                        vRight = vertexID[x + 1][z];
                    }
                    faces.addAll(vCurrent);
                    faces.addAll(0);
                    faces.addAll(vDown);
                    faces.addAll(0);
                    faces.addAll(vRight);
                    faces.addAll(0);
                }
                if (z - 1 >= 0 && x - 1 >= 0) {
                    Integer vCurrent = vertexID[x][z];
                    Integer vUp = vertexID[x][z - 1];
                    Integer vLeft = vertexID[x - 1][z];
                    if (vCurrent == null) {
                        points.addAll(tmpX);
                        points.addAll(tmpY);
                        points.addAll(tmpZ);
                        textures.addAll((float)x/ this.dimention, (float)z/ this.dimention);
                        vertexID[x][z] = ctr++;
                        vCurrent = vertexID[x][z];
                    }
                    if (vUp == null) {
                        points.addAll(tmpX - this.scale);
                        points.addAll(generatedYValues[x - 1][z] * this.scale);
                        points.addAll(tmpZ);
                        textures.addAll((float)(x - 1)/ this.dimention, (float)z/ this.dimention);
                        vertexID[x][z - 1] = ctr++;
                        vUp = vertexID[x][z - 1];
                    }
                    if (vLeft == null) {
                        //point below
                        points.addAll(tmpX);
                        points.addAll(generatedYValues[x][z - 1] * this.scale);
                        points.addAll(tmpZ - this.scale);
                        textures.addAll((float)x/ this.dimention, (float)(z - 1)/ this.dimention);
                        vertexID[x - 1][z] = ctr++;
                        vLeft = vertexID[x - 1][z];
                    }
                    faces.addAll(vCurrent);
                    faces.addAll(0);
                    faces.addAll(vUp);
                    faces.addAll(0);
                    faces.addAll(vLeft);
                    faces.addAll(0);
                }
            }
        }
        TriangleMesh mesh = new TriangleMesh();


//        mesh.getTexCoords().addAll(0, 0);
//        textures.addAll();

        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(textures);
        mesh.getFaces().addAll(faces);
        System.out.println("last texture x:" + textures.get(textures.size() - 2) + ",y:" + textures.get(textures.size()-1));
        PixelReader p = this.image.getPixelReader();
        for(int i = 0; i < this.dimention*this.dimention -2; i+=2)
        {
             int x=Math.round(textures.get(i)*this.dimention),
                y=Math.round(textures.get(i+1)*this.dimention);
             Color c = p.getColor(x, y);
            System.out.println(c.getRed() +","+ c.getGreen() +","+ c.getBlue());
        }
        return mesh;
    }

    public static float map(float x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public MeshView meshView(){

        MeshView t = new MeshView(this.meshObject);
        t.setDrawMode(DrawMode.LINE); //
        PhongMaterial m = new PhongMaterial();
        m.setDiffuseMap(this.image);
        t.setMaterial(m);
        return t;
    }

    public Box water(){
        Box waterBox = new Box();
        waterBox.setWidth(this.dimention * this.scale);
        waterBox.setDepth(this.dimention * this.scale);
        waterBox.setHeight(this.waterLevel * this.scale);
        waterBox.setDrawMode(DrawMode.FILL);
        waterBox.setMaterial(new PhongMaterial(new Color(0, 0, 0.6, 0.5)));
        waterBox.setTranslateX(this.dimention * this.scale / 2);
        waterBox.setTranslateZ(this.dimention * this.scale / 2);
        waterBox.setTranslateY(this.waterLevel * this.scale / 2);
        return waterBox;
    }

    public Group view()
    {
        return new Group(this.meshView(), this.water());
    }
}