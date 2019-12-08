package GUI;

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


/**
 *
 * @author Matthias
 */
public class Cubie extends Group {
    
    private static Image tex = null;

    private double offset;
    private Group rotate = new Group();
    private Group translate = new Group();

    private MeshView box;
    private PhongMaterial mat;

    Point3D pivotX = new Point3D(1, 0, 0);
    Point3D pivotY = new Point3D(0, 1, 0);
    Point3D pivotZ = new Point3D(0, 0, 1);

    private Direction startPosition;
    private Direction position;
    private Direction faceX, faceY, faceZ;
    private char startFaceX, startFaceY, startFaceZ;
    
    private Transform currentRotation;
    private Transform curTransform;

    private class Direction {
        int x;
        int y;
        int z;

        int nbTurns = 0;
        public Direction(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public void rotateX(){
            int temp = y;
            y = z;
            z = -temp;
            nbTurns++;
        }
        public void rotateY(){
            int temp = z;
            z = x;
            x = -temp;
            nbTurns++;
        }
        public void rotateZ(){
            int temp = x;
            x = y;
            y = -temp;
            nbTurns++;
        }
    }
    
    static {
        tex = new Image("file:CubeTexture.png");
    }

    public Cubie(int x, int y, int z, double offset) {
        this.offset = offset;

        init(x, y, z);
    }

    private void init(int x, int y, int z) {
        startPosition = new Direction(x, y, z);
        
        // shifted so the center is 0. otherwise rotation doesn't work
        position = new Direction(x-1, y-1, z-1);
        
        
        faceX = x == 0 ? new Direction(-1, 0, 0) : new Direction(1, 0, 0);
        faceY = y == 0 ? new Direction(0, -1, 0) : new Direction(0, 1, 0);
        faceZ = z == 0 ? new Direction(0, 0, -1) : new Direction(0, 0, 1);
        startFaceX = x == 0 ? 'L' : 'R';
        startFaceY = y == 0 ? 'D' : 'U';
        startFaceZ = z == 0 ? 'B' : 'F';

        Translate t = new Translate();
        t.setX((x - offset) * RubiksCube.SCALE);
        t.setY((y - offset) * RubiksCube.SCALE);
        t.setZ((z - offset) * RubiksCube.SCALE);
        translate.getTransforms().addAll(t);

        Rotate r = new Rotate();
        rotate.getTransforms().addAll(r);
        rotate.getChildren().addAll(translate);

        getChildren().addAll(rotate);

        float scale = (float) RubiksCube.SCALE / 2f;

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
    
    public void setMat(double alpha){
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

    public void rotateGraphical(double angle, char axis) {
        if(currentRotation == null) {
            curTransform = rotate.getTransforms().get(0);
            currentRotation = new Rotate(0, axisToPivot(axis));
        }
        currentRotation = new Rotate(angle, axisToPivot(axis)).createConcatenation(currentRotation);
        try{
            rotate.getTransforms().set(0, currentRotation.createConcatenation(curTransform));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void finishRotation(double angle, char axis) {
        currentRotation = null;
        curTransform = new Rotate(angle, axisToPivot(axis)).createConcatenation(curTransform);
        try{
            rotate.getTransforms().set(0, curTransform);
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void rotate(char axis) {
        switch (axis) {
            case 'X':
                faceX.rotateX();
                faceY.rotateX();
                faceZ.rotateX();
                position.rotateX();
                break;
            case 'Y':
                faceX.rotateY();
                faceY.rotateY();
                faceZ.rotateY();
                position.rotateY();
                break;
            case 'Z':
                faceX.rotateZ();
                faceY.rotateZ();
                faceZ.rotateZ();
                position.rotateZ();
                break;
        }
    }

    public int getStartX() {
        return startPosition.x;
    }

    public int getStartY() {
        return startPosition.y;
    }

    public int getStartZ() {
        return startPosition.z;
    }
    
    public int getX() {
        return position.x + 1;
    }

    public int getY() {
        return position.y + 1;
    }

    public int getZ() {
        return position.z + 1;
    }

    public boolean isInCorrectPosition(){
        return getX() == getStartX()
                && getY() == getStartY()
                && getZ() == getStartZ();
    }
    
    public boolean isSolved(){
        return isInCorrectPosition() 
                && faceX.x == (startPosition.x == 0 ? -1 : 1)
                && faceY.y == (startPosition.y == 0 ? -1 : 1)
                && faceZ.z == (startPosition.z == 0 ? -1 : 1);
    }

    /**
     * What face is the cubie on, specifically the sticker that normally lies
     * on the passed axis
     * @param axis
     * @return 
     */
    public char getFace(char axis){
        Direction dir = null;

        switch(axis){
            case 'X':
                dir = faceX;
                break;
            case 'Y':
                dir = faceY;
                break;
            case 'Z':
                dir = faceZ;
                break;
        }
        
        if(dir.x == -1) return 'L';
        if(dir.x ==  1) return 'R';
        if(dir.y == -1) return 'D';
        if(dir.y ==  1) return 'U';
        if(dir.z == -1) return 'B';
        return 'F';
    }

    /**
     * What face the cubie started on, specifically the sticker that lies
     * on the passed axis
     * @param axis
     * @return 
     */
    public char getStartFace(char axis){
        switch(axis){
            case 'X':
                return startFaceX;
            case 'Y':
                return startFaceY;
            default:
                return startFaceZ;
        }
    }
    
    public void highlight(){
        setMat(1);
    }
    
    public void unsetHighlight(){
        setMat(0);
    }
}
