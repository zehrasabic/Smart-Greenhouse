package com.example.androidapp;
//fragment za prikaz realnih vrijednosti vlaz tla. -> GPRS i ThingSpeak kanal
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

public class SoilMoisture1Fragment extends Fragment {
    TextView ispisvlaz11, ispisvlaz22, ispisvlaz33, ispisvlaz44;

    //neophodan prazan javni konstruktor
    public SoilMoisture1Fragment() {
    }

    Runnable mTicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soil_moisture1, container, false);
        ispisvlaz11 = view.findViewById(R.id.sm11);
        ispisvlaz22 = view.findViewById(R.id.sm22);
        ispisvlaz33 = view.findViewById(R.id.sm33);
        ispisvlaz44 = view.findViewById(R.id.sm44);
        final Handler mHandler = new Handler();
        //preuzimanje stvarnih vrijednosti s kanala svake sekunde
        mTicker = new Runnable() {
            @Override
            public void run() {
                //interakcija korisnickog interfejsa i azuriranja podataka
                new PreuzmiVlaznostTla().execute();
                mHandler.postDelayed(mTicker, 1000);
            }
        };
        mHandler.postDelayed(mTicker, 1000);
        return view;
    }

    //klasa za citanje vlaz. tla u stakleniku/plasteniku s kanala "Real Values"
    class PreuzmiVlaznostTla extends AsyncTask<Void, Void, String> {
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
            if (response == null) {
            } else {
                try {
                    JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                    //uzimanje vlaz. tla prvog dijela staklenika/plastenika i prikaz na korisnickom interfejsu
                    int v1 = channel.getInt("field2");
                    ispisvlaz11.setText(String.valueOf(v1) + " %");
                    int v2 = channel.getInt("field3");
                    ispisvlaz22.setText(String.valueOf(v2) + " %");
                    int v3 = channel.getInt("field4");
                    ispisvlaz33.setText(String.valueOf(v3) + " %");
                    int v4 = channel.getInt("field5");
                    ispisvlaz44.setText(String.valueOf(v4) + " %");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}