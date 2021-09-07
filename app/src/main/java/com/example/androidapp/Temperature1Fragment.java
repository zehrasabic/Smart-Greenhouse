package com.example.androidapp;
//fragment za prikaz realnih vrijednosti temp. -> GPRS i ThingSpeak kanal
//biblioteke

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Temperature1Fragment extends Fragment {
    TextView ispistempp;
    //neophodan prazan javni konstruktor
    public Temperature1Fragment() {
    }
    Runnable mTicker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature1, container, false);
        ispistempp=view.findViewById(R.id.prikazTempp);
        //preuzimanje stvarnih vrijednosti s kanala svake sekunde
        final Handler mHandler = new Handler();
        mTicker = new Runnable() {
            @Override
            public void run() {
                //interakcija korisnickog interfejsa i azuriranja podataka
                new PreuzmiTemperaturu().execute();
                mHandler.postDelayed(mTicker, 1000);
            }
        };
        mHandler.postDelayed(mTicker, 1000);
        return view;
    }
    //klasa za citanje temperature s kanala "Real Values"
    class PreuzmiTemperaturu extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
        }
        protected String doInBackground(Void... urls) {
            try {
                String urlString = "https://api.thingspeak.com/channels/1174455/feeds/last.json?api_key=U3LTU7GYH7FT6SPR";
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
        protected void onPostExecute(String response) {
            if(response==null){

            }
            else {
                try {
                    JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                    //uzimanje temp. i prikaz na korisnickom interfejsu
                    int t=channel.getInt("field1");
                    ispistempp.setText(String.valueOf(t)+" °C");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
