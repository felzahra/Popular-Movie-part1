package com.sallyezzat.popularmovies_s1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainGridActivity extends Activity {
    private MovieAdapter mMovieAdapter;
    private static final String MOVIEDB_API_KEY = "e4f5a649dfc6f56a0d9a7d8deacf3608";
    private List<Movie> mMovies = new ArrayList<>();
    private GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        gridview = findViewById(R.id.gridview);
        mMovieAdapter = new MovieAdapter(this, mMovies);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {//grid items click listener
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                openDetailActivity(position); //opens MoveDetailActivity to show details of selected movie
            }
        });

        String sortType = "N";
        runMovieTask(sortType); //run the task that will fetch movies


    }

    private void openDetailActivity(int position) {
        Intent myIntent = new Intent(MainGridActivity.this, MovieDetailActivity.class);  //intent to pass movie data when click on poster image
        myIntent.putExtra("movie", mMovies.get(position)); //pass the whole object of movie so implemented Serializable on class
        MainGridActivity.this.startActivity(myIntent); //open the detail Activity

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.popular: {

                runMovieTask("P"); //sort by popularity

                return true;


            }
            case R.id.rate: {
                runMovieTask("R"); //sort by rated
                return true;
            }
            default:
                return false;

        }

    }

    class MoviesTask extends AsyncTask<String, String, List<Movie>> { //inner class for asyncTask
        // private final String LOG_TAG = MoviesTask.class.getSimpleName();
        private final ProgressDialog dialog = new ProgressDialog(MainGridActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.wait_msg));
            dialog.setIndeterminate(true);
            dialog.show();

        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            // retrieved movies information as json from the api
            String movieJsonStr;

            try {

                String sortPart;
                switch (params[0]) {
                    case "P":
                        sortPart = "movie/popular"; //popular
                        break;
                    case "R":
                        sortPart = "movie/top_rated"; //top rated
                        break;
                    default:
                        sortPart = "discover/movie"; //default
                        break;
                }

                String urlPath = "http://api.themoviedb.org/3/" + sortPart + "?api_key=" + MOVIEDB_API_KEY;  //to be like that http://api.themoviedb.org/3/movie/popular?api_key = MOVIEDB_API_KEY
                URL url = new URL(urlPath);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                publishProgress("start reading input stream");
                movieJsonStr = StreamToString(in);
                mMovies = getMovieDataFromJson(movieJsonStr);
                in.close();

            } catch (Exception e) {
                publishProgress("cannot connect to moviedb");
            }

            return mMovies;
        }


        @Override
        protected void onPostExecute(List<Movie> movies) {
            //    super.onPostExecute(movies);

            mMovieAdapter.add(movies);
            gridview.setAdapter(new MovieAdapter(MainGridActivity.this, mMovies));
            dialog.dismiss();
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        private List<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");
            mMovies.clear(); //empty movies list
            Movie m;
            for (int i = 0; i < movieArray.length(); i++) {
                m = new Movie();
                JSONObject movie = movieArray.getJSONObject(i);
                m.setPosterPath(getString(R.string.img_root) + movie.getString("poster_path"));
                m.setId(movie.getString("id"));
                m.setOverview(movie.getString("overview"));
                m.setTitle(movie.getString("title"));
                m.setReleaseDate(movie.getString("release_date"));
                m.setVoteAverage(Double.valueOf(movie.getString("vote_average")));

                mMovies.add(m);
            }
            return mMovies;
        }


        public String StreamToString(InputStream inputStream) { //convert the input stream to string
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String Text = "";
            try {
                while ((line = bReader.readLine()) != null) {
                    Text += line;
                }
                inputStream.close();
            } catch (Exception ignored) {
            }
            return Text;
        }
    }

    private void runMovieTask(String sortType) { //run the async task to get movies according to parameter
        if (isApiKeyFound())
            if (isConnected()) { //check if connected to internet
                MoviesTask movieTask = new MoviesTask();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)  //to solve doInBackground not executed issue
                    movieTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sortType);
                else
                    movieTask.execute(sortType);
            }
    }


    private boolean isConnected() {//to alert if there is no internet connection instead of the not understood blank page
        NetworkInfo netInfo = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(MainGridActivity.this, R.string.no_connection_msg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isApiKeyFound() { //to alert if API key not set

        if (MOVIEDB_API_KEY.equals("")) {
            Toast.makeText(MainGridActivity.this, R.string.api_ky_msg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }
}









