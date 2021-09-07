package com.example.androidapp;
//fragment za prikaz realnih vrijednosti temperature -> SMS usluga
//biblioteke

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import es.dmoral.toasty.Toasty;

public class TemperatureFragment extends Fragment {
    //varijable za spremanje vrijednosti temperature i vlaznosti tla
    String podataktemp="";
    String podatakvlaz1="";
    String podatakvlaz2="";
    String podatakvlaz3="";
    String podatakvlaz4="";
    String temp="";
    //neophodan prazan javni konstruktor
    public TemperatureFragment() {
    }
    private static final String SMS_RECEIVED="android.provider.Telephony.SMS_RECEIVED";
    //prijemnik za nove vrijednosti temperature, poslane s mikrokontrolera - SMS
    MyReceiver receiver=new MyReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            //prikaz novih stvarnih vrijednosti - SMS
            TextView temperatura=getView().findViewById(R.id.prikazTemp);
            temperatura.setText(msg.substring(msg.indexOf("T:")+2,msg.indexOf(",V1"))+" °C");
            ////////////////////////////////////////////////
            //realne vrijednosti izdvojene iz sadrzaja poruke za slanje na ThingSpeak kanal
            podataktemp=msg.substring(msg.indexOf("T:")+2,msg.indexOf(",V1"));
            podatakvlaz1=msg.substring(msg.indexOf("V1:")+3,msg.indexOf(",V2"));
            podatakvlaz2=msg.substring(msg.indexOf("V2:")+3,msg.indexOf(",V3"));
            podatakvlaz3=msg.substring(msg.indexOf("V3:")+3,msg.indexOf(",V4"));
            podatakvlaz4=msg.substring(msg.indexOf("V4:")+3,msg.indexOf("!"));
            //svakom novom porukom se ucitavaju vrijednosti na ThingSpeak kanal
            new UploadSMSValues().execute();
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);
        return view;
    }
    @Override
    public void onResume() {
        //uslovna petlja za dozvolu koristenja SMS usluga unutar aplikacije
        if(ContextCompat.checkSelfPermission(getActivity().getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            //UI element u fragment_temperature.xml za prikaz temperature
            TextView poruka=getView().findViewById(R.id.prikazTemp);
            //prilikom pocetnog ulaska u dio za upravljanje i nadzor pomocu SMS-a
            //cita se zadnja poruka poslana s mikrokontrolera
            String sms="";
            Uri uri=Uri.parse("content://sms/inbox");
            String[] projection={"address","body"};
            String phoneNumber="+387603383946";
            Cursor cursor=getActivity().getContentResolver().query(uri,projection,"address=?",new String[] {phoneNumber},"date DESC LIMIT 1");
            if(cursor!=null && cursor.moveToFirst()){
                sms=cursor.getString(cursor.getColumnIndex("body"));
            }
            //izdvajanje stvarnih vrijednosti temp. iz zadnje poruke poslane s modula i spremanje u varijablu tipa string
            if(sms.indexOf("T:")!=-1){
                temp=sms.substring(sms.indexOf("T:")+2,sms.indexOf(",V1"));
            }
            //postavljanje vrijednosti temp. na UI element pripadajuceg xml fajla
            String temp1=temp+" °C";
            poruka.setText(temp1);
        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            requestPermissions(new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        getActivity().registerReceiver(receiver,new IntentFilter(SMS_RECEIVED));
        super.onResume();
    }
    //klasa za postavljanje realnih vrijednosti temp. i vlaz. tla na ThingSpeak kanal "Real Values - SMS"
    class UploadSMSValues extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
        }
        protected String doInBackground(Void... urls) {
            try {
                //adresa za postavljanje realnih vrijednosti spremljena u varijablu tipa string
                String urlString = "https://api.thingspeak.com/update?api_key=69F596FLTCD2PTVA&field1=" + podataktemp.toString() + "&field2=" + podatakvlaz1.toString() + "&field3=" + podatakvlaz2.toString() + "&field4=" + podatakvlaz3.toString() + "&field5=" + podatakvlaz4.toString();
               //povezivanje aplikacije i ThingSpeak kanala i postavljanje podataka
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
                //postavljanje podataka na kanal neuspjesno
                Toasty.error(getActivity(), "Greška!", Toast.LENGTH_LONG).show();
                return;
            } else {
                //postavljanje podataka na kanal uspjesno
                Toasty.success(getActivity(), "Stvarne vrijednosti učitane na kanal!!!", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}
