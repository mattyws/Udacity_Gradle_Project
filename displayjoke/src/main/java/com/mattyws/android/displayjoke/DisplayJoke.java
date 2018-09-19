package com.mattyws.android.displayjoke;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DisplayJoke extends AppCompatActivity {

    public static final String JOKE_TAG = "com.mattyws.android.displayjoke.JOKE";
    TextView displayJokeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentThatStartTheActivity = getIntent();
        if(!intentThatStartTheActivity.hasExtra(JOKE_TAG)) finish();


        setContentView(R.layout.activity_display_joke);

        String joke = intentThatStartTheActivity.getStringExtra(JOKE_TAG);
        displayJokeTextView = (TextView) findViewById(R.id.display_joke_textView);
        displayJokeTextView.setText(joke);
    }
}
