package com.udacity.gradle.builditbigger.paidTest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.mattyws.android.displayjoke.DisplayJoke;
import com.udacity.gradle.builditbigger.FetchJokeAssyncTask;
import com.udacity.gradle.builditbigger.MainActivity;
import com.udacity.gradle.builditbigger.R;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import junit.framework.Assert;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ServerResponseTest implements FetchJokeAssyncTask.TaskOnCompleteListener{

    CountDownLatch signal;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class, true, true);

    Context context;

    @Before
    public void setup(){
        context = InstrumentationRegistry.getContext();
    }

    @Test
    public void testJokeAssyncTask () throws Throwable {
        signal = new CountDownLatch(1);

        final FetchJokeAssyncTask myTask = new FetchJokeAssyncTask(this);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {

            @Override
            public void run() {
                myTask.execute();
            }
        });

        /* The testing thread will wait here until the UI thread releases it
         * above with the countDown() or 30 seconds passes and it times out.
         */
        signal.await(30, TimeUnit.SECONDS);
    }


    @Override
    public void preExecute() {

    }

    @Override
    public void onComplete(String result) {
        signal.countDown();
        Assert.assertTrue(result != null);
        Assert.assertTrue(result != "");
    }
}
