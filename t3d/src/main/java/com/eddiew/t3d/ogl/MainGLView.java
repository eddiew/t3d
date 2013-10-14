package com.eddiew.t3d.ogl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Contains the touch instructions for the main GLSurfaceView
 * Created by Eddie on 10/11/13.
 */
public class MainGLView extends GLSurfaceView {

    final GLRenderer renderer;

    public MainGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        renderer = new GLRenderer();
        setRenderer(renderer);
        // Test stuff. TODO: replace this
        final float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.modelMatrices.put(0,modelMatrix);
                renderer.rawData.add(new ObjectData( new float[]{
                        //Front Face
                        1,1,1,
                        0,0,1,
                        1,0,0,1,

                        -1,1,1,
                        0,0,1,
                        1,0,0,1,

                        -1,-1,1,
                        0,0,1,
                        1,0,0,1,

                        1,-1,1,
                        0,0,1,
                        1,0,0,1,
                        //Top Face
                        1,1,-1,
                        0,1,0,
                        0,1,0,1,

                        -1,1,-1,
                        0,1,0,
                        0,1,0,1,

                        -1,1,1,
                        0,1,0,
                        0,1,0,1,

                        1,1,1,
                        0,1,0,
                        0,1,0,1,
                        //Right Face
                        1,1,-1,
                        1,0,0,
                        0,0,1,1,

                        1,1,1,
                        1,0,0,
                        0,0,1,1,

                        1,-1,1,
                        1,0,0,
                        0,0,1,1,

                        1,-1,-1,
                        1,0,0,
                        0,0,1,1,
                        //Back Face
                        -1,1,-1,
                        0,0,-1,
                        1,1,0,1,

                        1,1,-1,
                        0,0,-1,
                        1,1,0,1,

                        1,-1,-1,
                        0,0,-1,
                        1,1,0,1,

                        -1,-1,-1,
                        0,0,-1,
                        1,1,0,1,
                        //Left Face
                        -1,1,1,
                        -1,0,0,
                        1,0,1,1,

                        -1,1,-1,
                        -1,0,0,
                        1,0,1,1,

                        -1,-1,-1,
                        -1,0,0,
                        1,0,1,1,

                        -1,-1,1,
                        -1,0,0,
                        1,0,1,1,
                        //Bottom Face
                        1,-1,1,
                        0,-1,0,
                        0,1,1,1,

                        -1,-1,1,
                        0,-1,0,
                        0,1,1,1,

                        -1,-1,-1,
                        0,-1,0,
                        0,1,1,1,

                        1,-1,-1,
                        0,-1,0,
                        0,1,1,1,
                        }, new short[]{
                        0,1,2,
                        0,2,3,
                        4,5,6,
                        4,6,7,
                        8,9,10,
                        8,10,11,
                        12,13,14,
                        12,14,15,
                        16,17,18,
                        16,18,19,
                        20,21,22,
                        20,22,23,
//                                2,3,1,// For GL_TRIANGLE_STRIP. What sorcery is this...
//                                0,
//                                4,
//                                3,
//                                7,
//                                6,
//                                4,
//                                5,
//                                1,
//                                6,
//                                2,
//                                3,
                        }, GLES20.GL_TRIANGLES,
                        0));
            }
        });
        // "Physics"
        new Thread(new Runnable(){
            @Override
            public void run() {
                while(renderer != null){
                    long time = (SystemClock.uptimeMillis() % 9000);
                    float angle = 0.04f * time;
                    double disp = Math.sin(Math.toRadians(2*angle))/4;
                    final float[] mMatrix = new float[16];
                    Matrix.setIdentityM(mMatrix, 0);
                    Matrix.translateM(mMatrix, 0, 0, (float) disp, 0);
                    Matrix.rotateM(mMatrix, 0, angle, 0, 1, 0);
                    renderer.modelMatrices.put(0, mMatrix);
                }
            }
        }).start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                //Renderer runs on openGL thread, so we cannot directly call it
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        //TODO: Tell renderer to transform viewMatrix according to move direction + distance
                    }
                });
                return true;
            default:
                return true;
        }
    }
}
