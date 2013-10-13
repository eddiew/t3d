package com.eddiew.t3d.ogl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.eddiew.t3d.ObjectData;

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

    float[] viewMatrix = new float[16];
    float[] projectionMatrix = new float[16];
    float[] vpMatrix = new float[16];
    volatile public HashMap<Integer,GLObject> glObjects = new HashMap<Integer,GLObject>();
    volatile public ArrayList<ObjectData> rawData = new ArrayList<ObjectData>();

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //initialize view matrix
        final float camX = 0f, camY = 0f, camZ = -3f;
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
        final float near = 3f, far = 7f;
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if(!rawData.isEmpty()){
            processRawData();
            rawData.clear();
        }
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        for(GLObject o : glObjects.values()){
            o.draw(vpMatrix);
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
        if(shader == 0){
            throw new RuntimeException("Error creating shader\n" + shaderCode);
        }
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0)
        {
            throw new RuntimeException("Error compiling shader\n" + shaderCode);
        }

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("GLObject", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public void addObject(float[] vertexData, short[] drawOrder, float[] modelMatrix, int objectId){
        glObjects.put(objectId,new GLObject(vertexData,drawOrder,modelMatrix, objectId));
    }
    public void addObject(GLObject o){
        glObjects.put(o.objectId,o);
    }
    void processRawData(){
        for(ObjectData o : rawData){
            addObject(o.vertexData, o.drawOrder, o.modelMatrix, o.objectId);
        }
    }

    GLObject getGLObjectById(int id){
        return glObjects.get(id);
    }
}

class GLObject {

    FloatBuffer vertexBuffer;
    ShortBuffer drawListBuffer;
    final int glProgram;
    final int objectId;
    static final int DIMENSIONS = 3;
    static final int BYTES_PER_FLOAT = 4;
    static final int BYTES_PER_SHORT = 2;
    int positionOffset = 0, positionValues = 3;
    int colorOffset = positionValues, colorValues = 4;
    int strideBytes = (positionValues+colorValues)*BYTES_PER_FLOAT;
    float[] modelMatrix = new float[16];
    float[] mvpMatrix = new float[16];

    String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 aPosition;" +
                    "attribute vec4 aColor;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "   vColor = aColor;" +
                    "   gl_Position = uMVPMatrix*aPosition;" +
                    "}";

    String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "   gl_FragColor = vColor;" +
                    "}";

    /**
     * @param vertexData Each vertex is defined by 7-adjacent values: X, Y, X, R, G, B, A
     * @param drawOrder Contains indices of vertexes in draw-order. 3 per triangle, in CCW order.
     * @param modelMatrix Position/orientation matrix for this object. Start with an identity matrix and translate/rotate it.
     */
    GLObject(float[] vertexData, short[] drawOrder, float[] modelMatrix, int objectId){

        this.objectId = objectId;

        // initialize vertex byte buffer
        vertexBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        if(drawOrder != null){
            drawListBuffer = ByteBuffer
                    .allocateDirect(drawOrder.length * BYTES_PER_SHORT)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer();
            drawListBuffer.put(drawOrder);
            drawListBuffer.position(0);
        }

        this.modelMatrix = modelMatrix;

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

    void draw(float[] vpMatrix){
        GLES20.glUseProgram(glProgram);

        //position info
        int positionHandle = GLES20.glGetAttribLocation(glProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, positionValues, GLES20.GL_FLOAT, false, strideBytes, vertexBuffer.position(0));

        //color info
        int colorHandle = GLES20.glGetAttribLocation(glProgram, "aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, colorValues, GLES20.GL_FLOAT, false, strideBytes, vertexBuffer.position(positionValues));

        //matrix transformations
        int matrixHandle = GLES20.glGetUniformLocation(glProgram, "uMVPMatrix");
        GLRenderer.checkGlError("glGetUniformLocation");
        Matrix.multiplyMM(mvpMatrix, 0, modelMatrix, 0, vpMatrix, 0);
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0);
        GLRenderer.checkGlError("glUniformMatrix4fv");

        //finally draw the damn thing
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, drawListBuffer.capacity());
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
