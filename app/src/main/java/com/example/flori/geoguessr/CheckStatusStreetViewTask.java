package com.example.flori.geoguessr;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class CheckStatusStreetViewTask extends AsyncTask<URL, Void, String> {

    private Exception exception;

    //UTILISATION de AsyncReponse pour recuperer le resultat de la requete en JSON
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public CheckStatusStreetViewTask(AsyncResponse delegate){
        this.delegate = delegate;
    }

    protected String doInBackground(URL... urls) {
        HttpURLConnection connection;
        BufferedReader reader;
        JSONObject jsonObject = null;
        String status = "KO";
        URL url = urls[0];
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }
            jsonObject = new JSONObject(buffer.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            status = jsonObject.getString("status");
            System.out.println(url.toString());
            System.out.println("Status :" + status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return status;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
