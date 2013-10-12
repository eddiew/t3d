package com.eddiew.t3d.ogl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Eddie on 10/11/13.
 */
public class GLRenderer implements GLSurfaceView.Renderer {
    float[] viewMatrix = new float[16];
    float[] projectionMatrix = new float[16];
    public ArrayList<GLObject> glObjects = new ArrayList<GLObject>();

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //initialize view matrix
        final float camX = 0f, camY = 0f, camZ = 1.5f;
        final float lookX = 0f, lookY = 0f, lookZ = -1f;
        final float upX = 0f, upY = 1f, upZ = 0f;
        Matrix.setLookAtM(viewMatrix, 0, camX, camY, camZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        // Recreate the projection matrix. Height stays the same, width varies with aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio, right = ratio;
        final float bottom = -1.0f, top = 1.0f;
        final float near = 1.0f, far = 1000.0f;
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        for(GLObject o : glObjects){
            o.draw(viewMatrix, projectionMatrix);
        }
    }

}
