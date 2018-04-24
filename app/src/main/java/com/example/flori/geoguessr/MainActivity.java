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

        //Button b_novice = (Button) findViewById(R.id.button_novice);
        /*Button b_medium = (Button) findViewById(R.id.button_medium);
        Button b_expert = (Button) findViewById(R.id.button_expert);
        Button b_stats = (Button) findViewById(R.id.button_stats);*/
    }

    /** Called when the user taps the Send button */
    public void novicePlay(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        int extra = 1;
        intent.putExtra("LEVEL", extra);
        startActivity(intent);
    }
}
