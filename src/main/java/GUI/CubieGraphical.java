package GUI;

import BL.RubiksCube;
import Data.Direction;
import Util.Rotatable;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

/**
 *
 * @author Matthias
 */
public class CubieGraphical extends Group implements Rotatable {

    private static final Point3D pivotX = new Point3D(1, 0, 0);
    private static final Point3D pivotY = new Point3D(0, 1, 0);
    private static final Point3D pivotZ = new Point3D(0, 0, 1);
    private static final Image tex;

    private double offset;
    private Group rotate = new Group();
    private Group translate = new Group();

    private MeshView box;
    private PhongMaterial mat;

    Transform currentTransform;
    
    static {
        tex = new Image("file:CubeTexture.png");
    }

    public CubieGraphical(int x, int y, int z, double offset) {
        this.offset = offset;

        init(x, y, z);
    }

    private void init(int x, int y, int z) {
        Translate t = new Translate();
        t.setX((x - offset) * RubiksCubeGraphical.SCALE);
        t.setY((y - offset) * RubiksCubeGraphical.SCALE);
        t.setZ((z - offset) * RubiksCubeGraphical.SCALE);
        translate.getTransforms().addAll(t);

        Rotate r = new Rotate();
        rotate.getTransforms().addAll(r);
        rotate.getChildren().addAll(translate);

        getChildren().addAll(rotate);

        float scale = (float) RubiksCubeGraphical.SCALE / 2f;

        float[] points = {
            scale, scale, scale,
            scale, scale, -scale,
            scale, -scale, scale,
            scale, -scale, -scale,
            -scale, scale, scale,
            -scale, scale, -scale,
            -scale, -scale, scale,
            -scale, -scale, -scale
        };

        float[] texCoords = {
            .25f, .25f,
            .5f, .25f,
            0f, .5f,
            .25f, .5f,
            .5f, .5f,
            .75f, .5f,
            1f, .5f,
            0f, .75f,
            .25f, .75f,
            .5f, .75f,
            .75f, .75f,
            1f, .75f,
            .25f, 1f,
            .5f, 1f
        };

        int[] faces = {
            0, 10, 2, 5, 1, 9,
            2, 5, 3, 4, 1, 9,
            4, 7, 5, 8, 6, 2,
            6, 2, 5, 8, 7, 3,
            0, 13, 1, 9, 4, 12,
            4, 12, 1, 9, 5, 8,
            2, 1, 6, 0, 3, 4,
            3, 4, 6, 0, 7, 3,
            0, 10, 4, 11, 2, 5,
            2, 5, 4, 11, 6, 6,
            1, 9, 3, 4, 5, 8,
            5, 8, 3, 4, 7, 3
        };

        int[] faceSmoothing = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        };

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(texCoords);
        mesh.getFaces().addAll(faces);
        mesh.getFaceSmoothingGroups().addAll(faceSmoothing);

        box = new MeshView(mesh);
        setMat(1);
        translate.getChildren().add(box);
    }

    public void setMat(double alpha) {
        mat = new PhongMaterial(Color.rgb(255, 255, 255, alpha), tex, null, null, null);
        box.setMaterial(mat);
    }

    public Point3D axisToPivot(char axis) {
        switch (axis) {
            case 'X':
                return pivotX;
            case 'Y':
                return pivotY;
            case 'Z':
                return pivotZ;
        }
        return null;
    }

    public void rotate(double angle, char axis, double turnTime) {
        currentTransform = rotate.getTransforms().get(0);
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(turnTime));
                setInterpolator(Interpolator.LINEAR);
            }

            @Override
            protected void interpolate(double frac) {
                rotate.getTransforms().set(0, new Rotate(angle * frac, axisToPivot(axis)).createConcatenation(currentTransform));
            }
        };
        animation.play();
    }


    public void highlight() {
        setMat(1);
    }

    public void unsetHighlight() {
        setMat(0);
    }

    @Override
    public void rotate(char axis) {
    }
}
