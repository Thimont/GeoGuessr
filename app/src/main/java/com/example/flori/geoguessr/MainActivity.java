package com.example.flori.geoguessr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /** Called when the user taps the novice button */
    public void novicePlay(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        int extra = 1;
        intent.putExtra("LEVEL", extra);
        startActivity(intent);
    }

    /** Called when the user taps the medium button */
    public void mediumPlay(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        int extra = 2;
        intent.putExtra("LEVEL", extra);
        startActivity(intent);
    }

    /** Called when the user taps the expert button */
    public void expertPlay(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        int extra = 3;
        intent.putExtra("LEVEL", extra);
        startActivity(intent);
    }

    public void statistics(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }
}
