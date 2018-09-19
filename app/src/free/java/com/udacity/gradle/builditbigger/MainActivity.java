package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.mattyws.android.displayjoke.DisplayJoke;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    Button tellJokeButton;
    TextView instructionTextView;
    ProgressBar jokeLoadingProgressBar;

    private CountingIdlingResource mCountIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getmCountIdlingResource();
        setContentView(R.layout.activity_main);
        tellJokeButton = (Button) findViewById(R.id.tell_joke_button);
        instructionTextView = (TextView) findViewById(R.id.instructions_text_view);
        jokeLoadingProgressBar = (ProgressBar) findViewById(R.id.joke_loading_progress_bar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {

        mCountIdlingResource.increment();
        new AsyncTask< Context, Void, String>(){
            private MyApi myApiService = null;
            private Context context;
            @Override
            protected String doInBackground(Context... params) {
                if(myApiService == null) {  // Only do this once
                    MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            // options for running against local devappserver
                            // - 10.0.2.2 is localhost's IP address in Android emulator
                            // - turn off compression when running against local devappserver
                            .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                            .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                @Override
                                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                    abstractGoogleClientRequest.setDisableGZipContent(true);
                                }
                            });
                    // end options for devappserver

                    myApiService = builder.build();
                }

                context = params[0];

                try {
                    return myApiService.getJoke().execute().getData();
                } catch (IOException e) {
                    return e.getMessage();
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingProgressBar();
            }

            @Override
            protected void onPostExecute(String result) {
                mCountIdlingResource.decrement();
                hideLoadingProgressBar();
                Intent intent = new Intent(context, DisplayJoke.class);
                intent.putExtra(DisplayJoke.JOKE_TAG, result);
                startActivity(intent);
            }
        }.execute(this);


    }


    public CountingIdlingResource getmCountIdlingResource() {
        if (mCountIdlingResource == null) {
            mCountIdlingResource = new CountingIdlingResource("Recipe_Detail_Fragments_Loading");
        }
        return mCountIdlingResource;
    }

    private void hideLoadingProgressBar(){
        instructionTextView.setVisibility(View.VISIBLE);
        tellJokeButton.setVisibility(View.VISIBLE);
        jokeLoadingProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showLoadingProgressBar(){
        instructionTextView.setVisibility(View.INVISIBLE);
        tellJokeButton.setVisibility(View.INVISIBLE);
        jokeLoadingProgressBar.setVisibility(View.VISIBLE);
    }
}
