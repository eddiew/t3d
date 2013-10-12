package com.eddiew.t3d;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eddiew.t3d.ogl.MainGLView;

/**
 * This fragment contains the user's simulated world.
 * TODO: Saving/Loading, Item Addition/Deletion, God Powers
 * Created by Eddie on 10/11/13.
 */
public class MainFragment extends Fragment {
    MainGLView mainGLView;
    Callbacks callbacks;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainFragment() {
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        assert(activity instanceof Callbacks);
        callbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //TODO: stuff
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        mainGLView = (MainGLView)getActivity().findViewById(R.id.view_main);
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * Callbacks to the wrapper Activity
     */
    public interface Callbacks {
    }
}
