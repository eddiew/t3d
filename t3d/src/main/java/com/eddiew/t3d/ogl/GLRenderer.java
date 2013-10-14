package com.eddiew.t3d.ogl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Eddie on 10/11/13.
 */
public class GLRenderer implements GLSurfaceView.Renderer {

    volatile public float[] viewMatrix = new float[16];
    volatile public float[] projectionMatrix = new float[16];
    private HashMap<Integer,GLObject> glObjects = new HashMap<Integer,GLObject>();
    volatile public HashMap<Integer, float[]> modelMatrices = new HashMap<Integer, float[]>();
    volatile public ArrayList<ObjectData> rawData = new ArrayList<ObjectData>();
    private float[] lightModelMatrix = new float[16];
    final float[] lightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};//empty because the light is centered on itself
    final float[] lightPosInWorldSpace = new float[4];
    final float[] lightPosInEyeSpace = new float[4];

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //initialize view matrix
        final float camX = 0f, camY = 2f, camZ = 3f;
        final float lookX = 0f, lookY = 0f, lookZ = 0f;
        final float upX = 0f, upY = 1f, upZ = 0f;
        Matrix.setLookAtM(viewMatrix, 0, camX, camY, camZ, lookX, lookY, lookZ, upX, upY, upZ);
//        //Test stuff
//        final float[] modelMatrix = new float[16];
//        Matrix.setIdentityM(modelMatrix,0);
//        addObject( new float[]{
//                0f, 0.622008459f, 0,
//                1, 0, 0, 1,
//
//                -0.5f, -0.311004243f, 0,
//                0, 1, 0, 1,
//
//                0.5f, -0.311004243f, 0,
//                0, 0, 1, 1
//        }, new short[]{
//                0, 1, 2
//        }, modelMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        // Recreate the projection matrix. Height stays the same, width varies with aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio, right = ratio;
        final float bottom = -1.0f, top = 1.0f;
        final float near = 1, far = 10;
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if(!rawData.isEmpty()){
            processRawData();
            rawData.clear();
        }
//        float[] vpMatrix = new float[16];
//        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        Matrix.setIdentityM(lightModelMatrix,0);
        Matrix.translateM(lightModelMatrix, 0, 0, 2, 4);
        //Why the hell would you do the following???
//        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
//        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);
        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0);

        for(GLObject o : glObjects.values()){
            o.draw(viewMatrix, projectionMatrix);
        }
    }

    /**
     * Compiles GLSL
     * @param type Can be vertex shader (GLES20.GL_VERTEX_SHADER) or fragment shader (GLES20.GL_FRAGMENT_SHADER)
     * @param shaderCode The GLSL shader code to be compiled
     * @return
     */
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
//        if(shader == 0){
//            throw new RuntimeException("Error creating shader\n" + shaderCode);
//        }
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);

        // TODO: error-check toggle
//        // Get the compilation status.
//        final int[] compileStatus = new int[1];
//        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
//
//        // If the compilation failed, delete the shader.
//        if (compileStatus[0] == 0)
//        {
//            throw new RuntimeException("Error compiling shader\n" + shaderCode);
//        }

        return shader;
    }

//    /**
//     * Utility method for debugging OpenGL calls. Provide the name of the call
//     * just after making it:
//     *
//     * <pre>
//     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
//     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
//     *
//     * If the operation is not successful, the check throws an error.
//     *
//     * @param glOperation - Name of the OpenGL call to check.
//     */
//    public static void checkGlError(String glOperation) {
//        int error;
//        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
//            Log.e("GLObject", glOperation + ": glError " + error);
//            throw new RuntimeException(glOperation + ": glError " + error);
//        }
//    }

    public void addObject(float[] vertexData, short[] drawOrder, int drawMode, int objectId){
        glObjects.put(objectId,new GLObject(vertexData,drawOrder, this, drawMode, objectId));
    }
    // This probably isn't necessary. TODO: Figure out a better way to update the world from another thread
    void processRawData(){
        for(ObjectData o : rawData){
            addObject(o.vertexData, o.drawOrder, o.drawMode, o.objectId);
        }
    }

    GLObject getGLObjectById(int id){
        return glObjects.get(id);
    }
}

class ObjectData{
    float[] vertexData;
    short[] drawOrder;
    int objectId;
    int drawMode;
    ObjectData(float[] vertexData, short[] drawOrder, int drawMode, int objectId){
        this.vertexData = vertexData;
        this.drawOrder = drawOrder;
        this.drawMode = drawMode;
        this.objectId = objectId;
    }
}

class GLObject {

    final int glProgram;
    final int objectId;
    final GLRenderer parentRenderer;
    final int drawMode;
    static final int BYTES_PER_FLOAT = 4;
    static final int BYTES_PER_SHORT = 2;
    static final int positionOffset = 0, positionValues = 3;
    static final int normalOffset = positionValues*BYTES_PER_FLOAT, normalValues = 3;
    static final int colorOffset = (positionValues + normalValues)*BYTES_PER_FLOAT, colorValues = 4;
    static final int strideBytes = (positionValues+normalValues+colorValues)*BYTES_PER_FLOAT;
    final int nVertices;
    final int[] bufferObjectHandles = new int[2];

    String vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
        "uniform mat4 uMVMatrix;" +
        "attribute vec4 aPosition;" +
        "attribute vec4 aColor;" +
        "attribute vec3 aNormal;" +
        "varying vec3 vPosition;" +
        "varying vec4 vColor;" +
        "varying vec3 vNormal;" +
        "void main() {" +
            "vPosition = vec3(uMVMatrix*aPosition);" +
            "vColor = aColor;" +
            "vNormal = vec3(uMVMatrix*vec4(aNormal,0.0));" +
            "gl_Position = uMVPMatrix*aPosition;" +
        "}";

    String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec3 uLightPos;" +
        "varying vec3 vPosition;" +
        "varying vec4 vColor;" +
        "varying vec3 vNormal;" +
        "void main() {" +
            "float dist = length(uLightPos-vPosition);" +
            "vec3 lightVector = normalize(uLightPos-vPosition);" +
            "float brightness = max(dot(vNormal, lightVector),0.0);" +
            "brightness *= max(10.0/(dist*dist),0.75);" +//the 10 is the light source's strength
            "gl_FragColor = vColor*brightness;" +
        "}";

    /**
     * @param vertexData Each vertex is defined by 7-adjacent values: X, Y, X, R, G, B, A
     * @param drawOrder Contains indices of vertexes in draw-order. 3 per triangle, in CCW order.
     */
    GLObject(float[] vertexData, short[] drawOrder, GLRenderer parentRenderer, int drawMode, int objectId){

        this.parentRenderer = parentRenderer;
        this.objectId = objectId;
        this.drawMode = drawMode;

        GLES20.glGenBuffers(2, bufferObjectHandles, 0);

        // initialize vertex buffer
        FloatBuffer vertexBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);

        //bind the VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjectHandles[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity()* BYTES_PER_FLOAT, vertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // initialize index buffer
        ShortBuffer drawListBuffer = ByteBuffer
                .allocateDirect(drawOrder.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        nVertices = drawListBuffer.capacity();

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferObjectHandles[1]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, drawListBuffer.capacity()*BYTES_PER_SHORT, drawListBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        //compile & load shaders
        int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        glProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(glProgram, vertexShader);
        GLES20.glAttachShader(glProgram, fragmentShader);
        GLES20.glLinkProgram(glProgram);
    }

    public int getObjectId(){
        return objectId;
    }

    void draw(float[] viewMatrix, float[] projectionMatrix){
        GLES20.glUseProgram(glProgram);

        //position info
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjectHandles[0]);
        final int positionHandle = GLES20.glGetAttribLocation(glProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, positionValues, GLES20.GL_FLOAT, false, strideBytes, 0);

        //normal info
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjectHandles[0]);
        final int normalHandle = GLES20.glGetAttribLocation(glProgram, "aNormal");
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glVertexAttribPointer(normalHandle, normalValues, GLES20.GL_FLOAT, false, strideBytes, normalOffset);

        //color info
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjectHandles[0]);// TODO: does this really need to be repeated?
        final int colorHandle = GLES20.glGetAttribLocation(glProgram, "aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, colorValues, GLES20.GL_FLOAT, false, strideBytes, colorOffset);

        // Clear the vertex buffer
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        //model-view Matrix
        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, parentRenderer.modelMatrices.get(objectId), 0);
        final int mvMatrixHandle = GLES20.glGetUniformLocation(glProgram, "uMVMatrix");
        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvpMatrix, 0);

        //model-view-projection Matrix
        final int matrixHandle = GLES20.glGetUniformLocation(glProgram, "uMVPMatrix");
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0);

        //Lighting
        final int lightHandle = GLES20.glGetUniformLocation(glProgram, "uLightPos");
        GLES20.glUniform3f(lightHandle, parentRenderer.lightPosInEyeSpace[0], parentRenderer.lightPosInEyeSpace[1], parentRenderer.lightPosInEyeSpace[2]);
        //TODO: is there not a better way to accomplish the above?

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferObjectHandles[1]);
        //finally draw the damn thing
        GLES20.glDrawElements(drawMode, nVertices, GLES20.GL_UNSIGNED_SHORT, 0);

        //Cleanup stuff
//        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
