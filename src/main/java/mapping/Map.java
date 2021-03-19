package mapping;

import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import perlin.PerlinNoise;

public class Map {

    private float[][] data;
    private Mesh meshObject;
    private float scale;

    public Map(float scale, long seed, int dimensions, float noiseValue)
    {
        this.data = new float[dimensions][dimensions];
        this.scale = scale;
        PerlinNoise noise = new PerlinNoise(seed);
        float xOf = 0f;
        for(int x = 0; x < dimensions; x++)
        {
            float zOf = 0f;
            for(int z = 0; z < dimensions; z++)
            {
                float ret = (float)noise.noise(xOf, zOf);
                float out = Map.map(ret, -1, 1, 0, dimensions / 6);
                this.data[x][z] = out;
                zOf += noiseValue;
            }
            xOf += noiseValue;
        }
        this.meshObject = this.mesh(data);
    }

    private TriangleMesh mesh(float[][] generatedYValues) {
        int sizeX = this.data.length, sizeZ = this.data[0].length;
        ObservableFloatArray points = FXCollections.observableFloatArray();
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
                        vertexID[x][z] = ctr++;
                        vCurrent = vertexID[x][z];
                    }
                    if (vDown == null) {
                        points.addAll(tmpX);
                        points.addAll(generatedYValues[x][z + 1] * this.scale);
                        points.addAll(tmpZ + this.scale);
                        vertexID[x][z + 1] = ctr++;
                        vDown = vertexID[x][z + 1];
                    }
                    if (vRight == null) {
                        points.addAll(tmpX + this.scale);
                        points.addAll(generatedYValues[x + 1][z] * this.scale);
                        points.addAll(tmpZ);
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
                        vertexID[x][z] = ctr++;
                        vCurrent = vertexID[x][z];
                    }
                    if (vUp == null) {
                        points.addAll(tmpX - this.scale);
                        points.addAll(generatedYValues[x - 1][z] * this.scale);
                        points.addAll(tmpZ);
                        vertexID[x][z - 1] = ctr++;
                        vUp = vertexID[x][z - 1];
                    }
                    if (vLeft == null) {
                        //point below
                        points.addAll(tmpX);
                        points.addAll(generatedYValues[x][z - 1] * this.scale);
                        points.addAll(tmpZ - this.scale);
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
        mesh.getTexCoords().addAll(0, 0);
        mesh.getPoints().addAll(points);
        mesh.getFaces().addAll(faces);
        return mesh;
    }

    public static float map(float x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public MeshView meshView(){
        return new MeshView(this.meshObject);
    }
}