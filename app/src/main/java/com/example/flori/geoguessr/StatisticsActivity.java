package com.example.flori.geoguessr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        AdapterScore adapterScore;
        List<Score> scores = Score.listAll(Score.class);

        ListView listView = (ListView) findViewById(R.id.list_score);

        adapterScore = new AdapterScore(StatisticsActivity.this, scores);
        listView.setAdapter(adapterScore);
    }
}
