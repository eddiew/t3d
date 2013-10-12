package com.eddiew.t3d.ogl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

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
        // Test stuff. TODO: replace this
        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix,0);
        renderer.glObjects.add(new GLObject(
           new float[]{
               -0.5f,-0.25f,0,
               1,0,0,1,

               0.5f,-0.25f,0,
               0,1,0,1,

               0,0.559016994f,0,
               0,0,1,1
           },
           new short[]{
               0,1,2
           },
           modelMatrix
        ));
        setRenderer(renderer);
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
