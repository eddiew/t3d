package com.eddiew.t3d.ogl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Base class for all render-able objects
 * Created by Eddie on 10/11/13.
 */
public class GLObject {

    FloatBuffer vertexBuffer;
    ShortBuffer drawListBuffer;
    int glProgram;
    static final int DIMENSIONS = 3;
    static final int BYTES_PER_FLOAT = 4;
    int positionOffset = 0, positionValues = 3;
    int colorOffset = positionValues, colorValues = 4;
    int strideBytes = (positionValues+colorValues)*BYTES_PER_FLOAT;
    float[] modelMatrix = new float[16];

    String vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 aPosition;" +
        "attribute vec4 aColor;" +
        "varying vec4 vColor;" +
        "void main() {" +
        "   vColor = aColor;" +
        "   gl_Position = uMBPMatrix*aPosition;" +
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
     * @param modelMatrix Position/orientation matrix for this object. X,Y,Z, lookX, lookY, lookZ?
     */
    public GLObject(float[] vertexData, short[] drawOrder, float[] modelMatrix){

        //compile & load shaders
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        glProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(glProgram, vertexShader);
        GLES20.glAttachShader(glProgram, fragmentShader);
        GLES20.glLinkProgram(glProgram);

        // initialize vertex byte buffer for shape coordinates
        vertexBuffer = ByteBuffer
            .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        if(drawOrder != null){
            ByteBuffer dlb = ByteBuffer.allocateDirect(
                    // (# of vertices to draw * 2 bytes per short)
                    drawOrder.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(drawOrder);
            drawListBuffer.position(0);
        }

        this.modelMatrix = modelMatrix;
    }

    /**
     * Compiles GLSL
     * @param type Can be vertex shader (GLES20.GL_VERTEX_SHADER) or fragment shader (GLES20.GL_FRAGMENT_SHADER)
     * @param shaderCode The GLSL shader code to be compiled
     * @return
     */
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void draw(float[] viewMatrix, float[] projectionMatrix){
        float[] mvpMatrix = new float[16];
        GLES20.glUseProgram(glProgram);

        //position info
        int positionHandle = GLES20.glGetAttribLocation(glProgram, "aPosition");
        GLES20.glVertexAttribPointer(positionHandle, positionValues, GLES20.GL_FLOAT, false, strideBytes, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        //color info
        int colorHandle = GLES20.glGetAttribLocation(glProgram, "aColor");
        GLES20.glVertexAttribPointer(colorHandle, colorValues, GLES20.GL_FLOAT, false, strideBytes, vertexBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        //matrix transformations
        int matrixHandle = GLES20.glGetUniformLocation(glProgram, "uMVPMatrix");
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0);

        //finally draw the damn thing
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexBuffer.capacity()/7);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
