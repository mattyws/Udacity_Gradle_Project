package com.udacity.gradle.builditbigger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.mattyws.android.displayjoke.DisplayJoke;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;

public class FetchJokeAssyncTask extends AsyncTask<Void, Void, String> {

    public static final String TAG = FetchJokeAssyncTask.class.getName();

    private TaskOnCompleteListener listener;
    private MyApi myApiService = null;

    public FetchJokeAssyncTask(TaskOnCompleteListener listener){
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
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

        try {
            return myApiService.getJoke().execute().getData();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            return "";
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.listener.preExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        this.listener.onComplete(result);
    }

    public interface TaskOnCompleteListener{
        void preExecute();
        void onComplete(String result);
    }
}
