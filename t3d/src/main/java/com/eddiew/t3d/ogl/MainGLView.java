package com.eddiew.t3d.ogl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.eddiew.t3d.ObjectData;

/**
 * Contains the touch instructions for the main GLSurfaceView
 * Created by Eddie on 10/11/13.
 */
public class MainGLView extends GLSurfaceView {

    GLRenderer renderer;

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
                renderer.rawData.add(new ObjectData(
                        new float[]{
                                1f, 1f, 1,
                                1, 0, 0, 1,

                                -1f, 1f, 1,
                                0, 1, 0, 1,

                                -1f, -1f, 1,
                                0, 0, 1, 1,

                                1f,-1f,1,
                                1,1,0,1,

                                1f, 1f, -1f,
                                1,0,1,1,

                                -1f,1f,-1f,
                                0,1,1,1,

                                -1f,-1f,-1f,
                                1,1,1,1,

                                1f,-1f,-1f,
                                0,0,0,1
                        },
                        new short[]{
                                0,4,5,
                                0,5,1,
                                1,5,6,
                                1,6,2,
                                0,1,2,
                                0,2,3,
                                4,0,3,
                                4,3,7,
                                5,4,7,
                                5,7,6,
                                6,7,3,
                                6,3,2
                        },
                        modelMatrix,
                        0));
            }
        });
        new Runnable(){

            @Override
            public void run() {
                while(renderer != null){
                    long time = (SystemClock.uptimeMillis() % 4000);
                    float angle = 0.009f * time;
                    double disp = Math.sin(angle);
                    float[] mMatrix = new float[16];
                    Matrix.setIdentityM(mMatrix,0);
                    Matrix.rotateM(mMatrix,0, angle, 0, 1, 0);
                    Matrix.translateM(mMatrix, 0, 0, (float)disp,0);
                    renderer.getGLObjectById(0).modelMatrix = mMatrix;
                }
            }
        }.run();
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
