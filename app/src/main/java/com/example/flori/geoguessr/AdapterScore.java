package com.example.flori.geoguessr;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterScore extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    private Activity activity;
    private List<Score> scores;

    public AdapterScore(Activity activity, List<Score> scores) {
        super();
        try {
            this.activity = activity;
            this.scores = scores;
        } catch (Exception e) {

        }
    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Score getItem(int position) {
        return scores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {
        public TextView display_score;
        public TextView display_level;
        public TextView display_date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        LayoutInflater inflater=activity.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.three_column, null);
            holder = new ViewHolder();

            holder.display_score = (TextView) convertView.findViewById(R.id.display_score);
            holder.display_level = (TextView) convertView.findViewById(R.id.display_level);
            holder.display_date = (TextView) convertView.findViewById(R.id.display_date);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        System.out.println(String.valueOf(scores.get(position).score));
        holder.display_score.setText(String.valueOf(scores.get(position).score));
        holder.display_level.setText(scores.get(position).level);
        holder.display_date.setText(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm", scores.get(position).date));

        return convertView;
    }
}
