package com.example.androidapp;
//fragment za prikaz realnih vrijednosti vlaznosti tla -> SMS usluga
//biblioteke

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SoilMoisture extends Fragment {
    TextView ispisvlaz1,ispisvlaz2,ispisvlaz3,ispisvlaz4;
    //neophodan prazan javni konstruktor
    public SoilMoisture() {
    }

    private static final String SMS_RECEIVED="android.provider.Telephony.SMS_RECEIVED";
    //prijemnik za nove vrijednosti, poslane s mikrokontrolera - SMS
    MyReceiver receiver=new MyReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            //prikaz stvarnih vrijednosti vlaznosti tla//////////
            TextView sm1=getView().findViewById(R.id.sm1);
            TextView sm2=getView().findViewById(R.id.sm2);
            TextView sm3=getView().findViewById(R.id.sm3);
            TextView sm4=getView().findViewById(R.id.sm4);
            sm1.setText(msg.substring(msg.indexOf("V1:")+3,msg.indexOf(",V2"))+" %");
            sm2.setText(msg.substring(msg.indexOf("V2:")+3,msg.indexOf(",V3"))+" %");
            sm3.setText(msg.substring(msg.indexOf("V3:")+3,msg.indexOf(",V4"))+" %");
            sm4.setText(msg.substring(msg.indexOf("V4:")+3,msg.indexOf("!"))+" %");
            ////////////////////////////////////////////////
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
        View view= inflater.inflate(R.layout.fragment_soil_moisture, container, false);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        ispisvlaz1=getView().findViewById(R.id.sm1);
        ispisvlaz2=getView().findViewById(R.id.sm2);
        ispisvlaz3=getView().findViewById(R.id.sm3);
        ispisvlaz4=getView().findViewById(R.id.sm4);
        String vlaz1="";
        String vlaz2="";
        String vlaz3="";
        String vlaz4="";
        //uslovna petlja za dozvolu koristenja SMS usluga unutar aplikacije
        if(ContextCompat.checkSelfPermission(getActivity().getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            //prilikom pocetnog ulaska u dio za upravljanje i nadzor pomocu SMS-a
            //cita se zadnja poruka poslana s modula mikrokontrolera
            String sms="";
            Uri uri=Uri.parse("content://sms/inbox");
            String[] projection={"address","body"};
            String phoneNumber="+387603383946";
            Cursor cursor=getActivity().getContentResolver().query(uri,projection,"address=?",new String[] {phoneNumber},"date DESC LIMIT 1");
            if(cursor!=null && cursor.moveToFirst()){
                sms=cursor.getString(cursor.getColumnIndex("body"));
            }
            //izdvajanje stvarnih vrijednosti vlaz. tla iz zadnje poruke poslane s modula i spremanje u varijable tipa string
            if(sms.indexOf("T:")!=-1){
                vlaz1=sms.substring(sms.indexOf("V1:")+3,sms.indexOf(",V2"));
                vlaz2=sms.substring(sms.indexOf("V2:")+3,sms.indexOf(",V3"));
                vlaz3=sms.substring(sms.indexOf("V3:")+3,sms.indexOf(",V4"));
                vlaz4=sms.substring(sms.indexOf("V4:")+3,sms.indexOf("!"));
            }
            //postavljanje vrijednosti temp. na UI element pripadajuceg xml fajla
            String v1=vlaz1+" %";
            String v2=vlaz2+" %";
            String v3=vlaz3+" %";
            String v4=vlaz4+" %";
            ispisvlaz1.setText(v1);
            ispisvlaz2.setText(v2);
            ispisvlaz3.setText(v3);
            ispisvlaz4.setText(v4);
        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            requestPermissions(new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        getActivity().registerReceiver(receiver,new IntentFilter(SMS_RECEIVED));
    }
}
