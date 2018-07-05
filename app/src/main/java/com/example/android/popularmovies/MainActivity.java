package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by ekzhu on 16.06.2018.
 */

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;
            ((MainActivityFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_movie))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(MovieObject m) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable("movie", m);
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, DetailActivity.class);
            detailIntent.putExtra("movie", m);
            startActivity(detailIntent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
