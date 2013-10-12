package com.eddiew.t3d;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity wrapper for MainFragment
 * Created by Eddie on 10/11/13.
 */
public class MainActivity extends Activity implements MainFragment.Callbacks{
    boolean isTwoPane;
    Fragment mainFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // If the activity us running on a large display
        if(findViewById(R.id.container_main) != null){
            isTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_item_config:
                // TODO: show ItemFragment or start ItemActivity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
