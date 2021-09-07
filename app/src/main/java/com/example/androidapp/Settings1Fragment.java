package com.example.androidapp;
//fragment za konfiguraciju parametara -> GPRS i ThingSpeak kanal
//bliblioteke

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import es.dmoral.toasty.Toasty;

public class Settings1Fragment extends Fragment implements View.OnClickListener{
    //deklarisanje svih UI elemenata koristenih u pripadajucem xml fajlu
    TextView referencetext,statustext;
    String rezultatstatuss="";
    TextView texttempp, textvlaz11, textvlaz22, textvlaz33, textvlaz44, referenceidd, statusidd,statusidd1;
    ImageButton uptempp, downtempp, upvlaz11, downvlaz11, upvlaz22, downvlaz22, upvlaz33, downvlaz33, upvlaz44, downvlaz44;
    Button buttonsendd;
    //deklaracija varijabli za spremanje vrijednosti temperature i vlaznosti tla
    String podataktempp = "";
    String podatakvlaz11 = "";
    String podatakvlaz22 = "";
    String podatakvlaz33 = "";
    String podatakvlaz44 = "";
    private int countertempp = 0;
    private int countervlaz11 = 0;
    private int countervlaz22 = 0;
    private int countervlaz33 = 0;
    private int countervlaz44 = 0;
    //neophodan prazan javni konstruktor
    public Settings1Fragment() {
    }
    Runnable mTicker;
    Runnable mTicker1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings1, container, false);
        setRetainInstance(true);
        //deklarisanje svih UI elemenata koristenih pri kreiranju izgleda u fragment_settings1.xml fajlu
        referencetext=view.findViewById(R.id.referencetext);
        statustext=view.findViewById(R.id.statustext);
        texttempp = view.findViewById(R.id.texttempp);
        textvlaz11 = view.findViewById(R.id.textvlaz11);
        textvlaz22 = view.findViewById(R.id.textvlaz22);
        textvlaz33 = view.findViewById(R.id.textvlaz33);
        textvlaz44 = view.findViewById(R.id.textvlaz44);
        referenceidd = view.findViewById(R.id.referenceIDD);
        statusidd = view.findViewById(R.id.statusIDD);
        statusidd1=view.findViewById(R.id.statustext1);
        uptempp = view.findViewById(R.id.uptempp);
        downtempp = view.findViewById(R.id.downtempp);
        upvlaz11 = view.findViewById(R.id.upvlaz11);
        downvlaz11 = view.findViewById(R.id.downvlaz11);
        upvlaz22 = view.findViewById(R.id.upvlaz22);
        downvlaz22 = view.findViewById(R.id.downvlaz22);
        upvlaz33 = view.findViewById(R.id.upvlaz33);
        downvlaz33 = view.findViewById(R.id.downvlaz33);
        upvlaz44 = view.findViewById(R.id.upvlaz44);
        downvlaz44 = view.findViewById(R.id.downvlaz44);
        buttonsendd = view.findViewById(R.id.buttonsendd);
        //dugme za slanje parametara
        buttonsendd.setOnClickListener(this);
        //dugmad za odabir vrijednosti parametara
        uptempp.setOnClickListener(this);
        downtempp.setOnClickListener(this);
        upvlaz11.setOnClickListener(this);
        downvlaz11.setOnClickListener(this);
        upvlaz22.setOnClickListener(this);
        downvlaz22.setOnClickListener(this);
        upvlaz33.setOnClickListener(this);
        downvlaz33.setOnClickListener(this);
        upvlaz44.setOnClickListener(this);
        downvlaz44.setOnClickListener(this);
        //ocitavanje realnih vrijednosti koje se postavljaju u UI elemente pri prvom ulasku u aplikaciju
        new ReadRealValues().execute();
        //dugme za slanje zeljenih vrijednosti temperature i vlaznosti tla
        buttonsendd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                podataktempp = texttempp.getText().toString();
                podatakvlaz11 = textvlaz11.getText().toString();
                podatakvlaz22 = textvlaz22.getText().toString();
                podatakvlaz33 = textvlaz33.getText().toString();
                podatakvlaz44 = textvlaz44.getText().toString();
                //ucitavanje zeljenih vrijednosti na ThingSpeak kanal
                new UcitajNaKanal().execute();
                //dugme onemoguceno na 20 sekundi
                buttonsendd.setClickable(false);
                buttonsendd.setBackgroundColor(Color.parseColor("#AB0404"));
            }
        });

        return view;
    }
    @Override
    public void onClick(View v) {
        if (v == uptempp) {
            countertempp=Integer.valueOf((String) texttempp.getText());
            countertempp++;
            texttempp.setText(String.valueOf(countertempp));
            if (countertempp >= 35) {
                uptempp.setClickable(false);
            } else {
                uptempp.setClickable(true);
            }
        }
        if (v == downtempp) {
            countertempp=Integer.valueOf((String) texttempp.getText());
            countertempp--;
            texttempp.setText(String.valueOf(countertempp));
            if (countertempp <= 5) {
                downtempp.setClickable(true);
            } else {
                downtempp.setClickable(true);
            }
        }
        if (v == upvlaz11) {
            countervlaz11=Integer.valueOf((String) textvlaz11.getText());
            countervlaz11++;
            textvlaz11.setText(String.valueOf(countervlaz11));
            if (countervlaz11 >= 99) {
                upvlaz11.setClickable(false);
            } else {
                upvlaz11.setClickable(true);
            }
        }
        if (v == downvlaz11) {
            countervlaz11=Integer.valueOf((String) textvlaz11.getText());
            countervlaz11--;
            textvlaz11.setText(String.valueOf(countervlaz11));
            if (countervlaz11 <= 0) {
                downvlaz11.setClickable(false);
            } else {
                downvlaz11.setClickable(true);
            }
        }
        if (v == upvlaz22) {
            countervlaz22=Integer.valueOf((String) textvlaz22.getText());
            countervlaz22++;
            textvlaz22.setText(String.valueOf(countervlaz22));
            if (countervlaz22 >= 99) {
                upvlaz22.setClickable(false);
            } else {
                upvlaz22.setClickable(true);
            }
        }
        if (v == downvlaz22) {
            countervlaz22=Integer.valueOf((String) textvlaz22.getText());
            countervlaz22--;
            textvlaz22.setText(String.valueOf(countervlaz22));
            if (countervlaz22 <= 0) {
                downvlaz22.setClickable(false);
            } else {
                downvlaz22.setClickable(true);
            }
        }
        if (v == upvlaz33) {
            countervlaz33=Integer.valueOf((String) textvlaz33.getText());
            countervlaz33++;
            textvlaz33.setText(String.valueOf(countervlaz33));
            if (countervlaz33 >= 99) {
                upvlaz33.setClickable(false);
            } else {
                upvlaz33.setClickable(true);
            }
        }
        if (v == downvlaz33) {
            countervlaz33=Integer.valueOf((String) textvlaz33.getText());
            countervlaz33--;
            textvlaz33.setText(String.valueOf(countervlaz33));
            if (countervlaz33 <= 0) {
                downvlaz33.setClickable(false);
            } else {
                downvlaz33.setClickable(true);
            }
        }
        if (v == upvlaz44) {
            countervlaz44=Integer.valueOf((String) textvlaz44.getText());
            countervlaz44++;
            textvlaz44.setText(String.valueOf(countervlaz44));
            if (countervlaz44 >= 99) {
                upvlaz44.setClickable(false);
            } else {
                upvlaz44.setClickable(true);
            }
        }
        if (v == downvlaz44) {
            countervlaz44=Integer.valueOf((String) textvlaz44.getText());
            countervlaz44--;
            textvlaz44.setText(String.valueOf(countervlaz44));
            if (countervlaz44 <= 0) {
                downvlaz44.setClickable(false);
            } else {
                downvlaz44.setClickable(true);
            }
        }
    }
    class ReadRealValues extends AsyncTask<Void, Void, String> {
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
                    String count = channel.getString("field1");
                    texttempp.setText(String.valueOf(count));
                    String count1 = channel.getString("field2");
                    textvlaz11.setText(String.valueOf(count1));
                    String count2 = channel.getString("field3");
                    textvlaz22.setText(String.valueOf(count2));
                    String count3 = channel.getString("field4");
                    textvlaz33.setText(String.valueOf(count3));
                    String count4 = channel.getString("field5");
                    textvlaz44.setText(String.valueOf(count4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Information extends AsyncTask<Void, Void, String> {
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
                    String status = channel.getString("field6");
                    statusidd.setText(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    //klasa za ucitavanje referentnih vrijednosti temperature i vlaz. tla na kanal "Reference Values"
    class UcitajNaKanal extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... urls) {
            try {
                String urlString = "https://api.thingspeak.com/update?api_key=6XUDQZXI6XJ5V1C6&field1=" + podataktempp.toString() + "&field2=" + podatakvlaz11.toString() + "&field3=" + podatakvlaz22.toString() + "&field4=" + podatakvlaz33.toString() + "&field5=" + podatakvlaz44.toString();
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
                Toasty.error(getActivity(), "Greška!", Toast.LENGTH_LONG).show();
                return;
            } else {
                Toasty.success(getActivity(), "Učitavanje vrijednosti parametara uspješno!", Toast.LENGTH_LONG).show();

                final Handler myHandler1 = new Handler();
                myHandler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonsendd.setClickable(true);
                        buttonsendd.setBackgroundColor(Color.parseColor("#668B6A"));
                    }
                }, 20000);

                //povratna informacija
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Information().execute();
                        rezultatstatuss=statusidd.getText().toString();

                        if(rezultatstatuss.equals("1")) {

                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setMessage("GPRS USLUGA -> ŽELJENE VRIJEDNOSTI TEMPERATURE I VLAŽNOSTI TLA PRIMLJENE U STAKLENIKU/PLASTENIKU!!!");
                            alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alert.show();

                        }
                        statusidd.setText("0");
                    }
                }, 35000);

                return;
            }
        }
    }
}