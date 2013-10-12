package com.eddiew.t3d;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eddiew.t3d.ogl.ItemGLView;

/**
 * This fragment allows the user to select, view, modify, and save the item he can then add to the world
 * Created by Eddie on 10/11/13.
 */
public class ItemFragment extends Fragment {
    ItemGLView itemGLView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_item, container, false);
    }
    @Override
    public void onStart(){
        itemGLView = (ItemGLView)getActivity().findViewById(R.id.view_item);
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
}
