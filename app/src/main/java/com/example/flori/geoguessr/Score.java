package com.example.flori.geoguessr;

import com.orm.SugarRecord;

import java.util.Date;

public class Score extends SugarRecord<Score> {
    float score;
    String level;
    Date date;

    public Score() {
    }

    public Score(float score, String level, Date date) {
        this.score = score;
        this.level = level;
        this.date = date;
    }
}
