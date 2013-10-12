package com.eddiew.t3d.ogl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Contains the touch instructions for the item GLSurfaceView
 * Created by Eddie on 10/11/13.
 */
public class ItemGLView extends GLSurfaceView {
    public ItemGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setRenderer(new GLRenderer());
    }
}
