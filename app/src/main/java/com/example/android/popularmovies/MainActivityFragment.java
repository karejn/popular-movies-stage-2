package com.example.android.popularmovies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.Data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ekzhu on 03.07.2018.
 */

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    String PATH_SORT = "popular";
    private Callbacks mCallbacks = sDummyCallbacks;
    private int mActivatedPosition = GridView.INVALID_POSITION;

    public interface Callbacks {
        public void onItemSelected(MovieObject m);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(MovieObject m) {
        }
    };

    public MainActivityFragment() {
    }

    public MovieCursorAdapter movieCursorAdapter;
    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS =
            {
                    MovieContract.MovieEntry.COLUMN_ID,
                    MovieContract.MovieEntry.COLUMN_TITLE,
                    MovieContract.MovieEntry.COLUMN_OVERVIEW,
                    MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                    MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                    MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                    MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID
            };

    static final int COL_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_OVERVIEW = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_VOTE_AVERAGE = 4;
    static final int COL_POSTER_PATH = 5;
    static final int COL_BACKDROP_PATH = 6;
    static final int COL_MOVIE_ID = 7;

    MovieObject movieClicked;

    String popular = "popular";
    public boolean favMenuSelected;
    public boolean popMenuselected = true;
    String top = "top_rated";
    List<MovieObject> listmovie;
    MovieObject[] movieObjects;
    private MovieAdapter movieAdapter;
    GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_popular) {
            favMenuSelected = false;
            popMenuselected = true;
            gridView.setAdapter(movieAdapter);
            PATH_SORT = "popular";
            new FetchMovieTask().execute(popular);
            return true;

        }
        if (id == R.id.action_top_rated) {
            favMenuSelected = false;
            popMenuselected = false;
            gridView.setAdapter(movieAdapter);
            PATH_SORT = "top_rated";
            new FetchMovieTask().execute(top);
            return true;
        }

        if (id == R.id.action_favourite) {
            favMenuSelected = true;
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieObjects = new MovieObject[]{new MovieObject("a"), new MovieObject("b")};
        listmovie = new ArrayList<MovieObject>(Arrays.asList(movieObjects));
        movieAdapter = new MovieAdapter(getActivity(), R.layout.grid_item_movies, R.id.grid_item_movies_imageview, listmovie);
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        new FetchMovieTask().execute("popularity.desc");
        gridView = (GridView) rootview.findViewById(R.id.gridview_movies);

        String url = "content://com.example.android.popularmovies.app/movie";

        Uri fetchUri = Uri.parse(url);
        Cursor c = getContext().getContentResolver().query(fetchUri, null, null, null, null);


        if (!favMenuSelected)
            gridView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (!favMenuSelected) {
                    movieClicked = (MovieObject) movieAdapter.getItem(position);
                    Log.v("ABC", movieClicked.toString());

                    mCallbacks.onItemSelected(movieClicked);


                } else {

                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    MovieObject mo = new MovieObject();
                    Log.v("ABC", mo.toString());
                    mo.title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                    mo.id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                    mo.poster_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                    mo.backdrop_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH));
                    mo.vote_average = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                    mo.release_date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                    mo.overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                    Log.v("Movie Detail :", cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));

                    mCallbacks.onItemSelected(mo);
                }


            }
        });

        return rootview;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v("onCreateLoader :", "-----------------------Called Successfully-----------------");

        String url = "content://com.example.android.popularmovies.app/movie";

        Uri fetchUri = Uri.parse(url);
        return new CursorLoader(getActivity(), fetchUri, MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        Log.v("onLoadFinished", "-----------------------Called Successfully-----------------");

        if (movieCursorAdapter == null)
            movieCursorAdapter = new MovieCursorAdapter(getContext(), c, MOVIE_LOADER);
        movieCursorAdapter.swapCursor(c);
        Log.v("Favourite VERBOSE", String.valueOf(favMenuSelected));

        if (favMenuSelected)
            gridView.setAdapter(movieCursorAdapter);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v("onLoaderReset", "-----------------------Called Successfully-----------------");

        movieCursorAdapter.swapCursor(null);
    }


    class FetchMovieTask extends AsyncTask<String, Void, MovieObject[]> {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        ContentLoadingProgressBar progressBar = new ContentLoadingProgressBar(getContext());
        MovieObject[] movieObjects = null;

        @Override
        protected void onPostExecute(MovieObject[] str) {
            if (str == null)
                return;
            if (str != null)
                movieAdapter.clear();

            for (MovieObject m : str) {
                movieAdapter.add(m);
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.show();

        }

        @Override
        protected MovieObject[] doInBackground(String... strings) {
            final String movieBaseUrl = "http://api.themoviedb.org/3";
            String PATH_MOVIES = "movie";
            String PARAM_QUERY = "api_key";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONStr = null;
            try {

                Uri buildUri = Uri.parse(movieBaseUrl).buildUpon()
                        .appendPath(PATH_MOVIES)
                        .appendPath(PATH_SORT)
                        .appendQueryParameter(PARAM_QUERY, BuildConfig.MOVIE_DB_API_KEY)
                        .build();


                Log.v("URL", buildUri.toString());
                URL url = new URL(buildUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    movieJSONStr = null;
                }
                assert inputStream != null;
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                if (buffer.length() == 0) {
                    movieJSONStr = null;
                }
                movieJSONStr = buffer.toString();
                JSONObject movieJSONObject = new JSONObject(movieJSONStr);
                JSONArray movieJSONArray = movieJSONObject.optJSONArray("results");
                movieObjects = new MovieObject[movieJSONArray.length()];
                for (int i = 0; i < movieJSONArray.length(); i++) {
                    movieObjects[i] = new MovieObject();
                    JSONObject jsonObject = movieJSONArray.getJSONObject(i);
                    movieObjects[i].title = jsonObject.optString("title");
                    movieObjects[i].overview = jsonObject.optString("overview");
                    movieObjects[i].poster_path = jsonObject.optString("poster_path");
                    movieObjects[i].release_date = jsonObject.getString("release_date");
                    movieObjects[i].vote_average = jsonObject.getString("vote_average");
                    movieObjects[i].backdrop_path = jsonObject.getString("backdrop_path");
                    movieObjects[i].id = jsonObject.getString("id");
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return movieObjects;

        }

    }


    @Override
    public void onResume() {
        super.onResume();
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != GridView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        Log.v("ABC : ", "setActivatedOnItemClick Executed");

        gridView.setChoiceMode(activateOnItemClick
                ? GridView.CHOICE_MODE_SINGLE
                : GridView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {

        Log.v("ABC : ", "setActivatedPosition Executed");
        if (position == GridView.INVALID_POSITION) {
            gridView.setItemChecked(mActivatedPosition, false);
        } else {
            gridView.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

}
