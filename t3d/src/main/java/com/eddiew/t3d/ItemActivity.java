package com.eddiew.t3d;

import android.app.Activity;
import android.os.Bundle;

/**
 * Activity wrapper for an ItemFragment that only gets created on small devices
 * Created by Eddie on 10/11/13.
 */
public class ItemActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
    }
}
