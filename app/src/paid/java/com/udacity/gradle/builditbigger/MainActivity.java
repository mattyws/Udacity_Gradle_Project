package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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


public class MainActivity extends AppCompatActivity implements FetchJokeAssyncTask.TaskOnCompleteListener{

    public static final String TAG = "MainActivity";

    Button tellJokeButton;
    TextView instructionTextView;
    ProgressBar jokeLoadingProgressBar;

    public CountingIdlingResource mCountIdlingResource;

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
        new FetchJokeAssyncTask(this).execute();
    }

    public void hideLoadingProgressBar(){
        instructionTextView.setVisibility(View.VISIBLE);
        tellJokeButton.setVisibility(View.VISIBLE);
        jokeLoadingProgressBar.setVisibility(View.INVISIBLE);
    }

    public void showLoadingProgressBar(){
        instructionTextView.setVisibility(View.INVISIBLE);
        tellJokeButton.setVisibility(View.INVISIBLE);
        jokeLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    public CountingIdlingResource getmCountIdlingResource() {
        if (mCountIdlingResource == null) {
            mCountIdlingResource = new CountingIdlingResource("Recipe_Detail_Fragments_Loading");
        }
        return mCountIdlingResource;
    }

    @Override
    public void preExecute() {
        showLoadingProgressBar();
    }

    @Override
    public void onComplete(String result) {
        mCountIdlingResource.decrement();
        hideLoadingProgressBar();
        Intent intent = new Intent(this, DisplayJoke.class);
        intent.putExtra(DisplayJoke.JOKE_TAG, result);
        startActivity(intent);
    }
}
