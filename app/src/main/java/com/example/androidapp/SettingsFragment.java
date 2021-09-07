package com.example.androidapp;
//fragment za konfiguraciju parametara -> SMS usluga
//biblioteke

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import es.dmoral.toasty.Toasty;

public class SettingsFragment extends Fragment implements View.OnClickListener{
    private static final String SMS_RECEIVED="android.provider.Telephony.SMS_RECEIVED";
    //deklarisanje svih UI elemenata koristenih u pripadajucem xml fajlu
    TextView texttemp,textvlaz1,textvlaz2,textvlaz3,textvlaz4,t,v1,v2,v3,v4;
    ImageButton uptemp,downtemp,upvlaz1,downvlaz1,upvlaz2,downvlaz2,upvlaz3,downvlaz3,upvlaz4,downvlaz4;
    Button buttonsend;
    //deklaracija varijabli za spremanje vrijednosti temperature i vlaznosti tla
    String podataktemp="";
    String podatakvlaz1="";
    String podatakvlaz2="";
    String podatakvlaz3="";
    String podatakvlaz4="";
    String poslatiporuku="";
    private int countertemp=0;
    private int countervlaz1=0;
    private int countervlaz2=0;
    private int countervlaz3=0;
    private int countervlaz4=0;
    String temp="";
    String vlaz1="";
    String vlaz2="";
    String vlaz3="";
    String vlaz4="";
    //neophodan prazan javni konstruktor
    public SettingsFragment() {
    }
    //prijemnik za povratnu poruku nakon slanja zeljenih parametara
    MyReceiver receiver=new MyReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            //uslovna petlja za pretragu kljucnih rijeci
            if(msg.indexOf("Poruka")!=-1){
                //dijalog upozorenja
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setMessage("SMS USLUGA -> ŽELJENE VRIJEDNOSTI TEMPERATURE I VLAŽNOSTI TLA PRIMLJENE U STAKLENIKU/PLASTENIKU!!!");
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }); alert.show();
            }
        }
    };
    @Override
    public void onResume() {
        //dozvola za koristenje SMS usluge na mobilnom uredjaju
        if(ContextCompat.checkSelfPermission(getActivity().getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
             buttonsend.setEnabled(true);
             t=getView().findViewById(R.id.texttemp);
             v1=getView().findViewById(R.id.textvlaz1);
             v2=getView().findViewById(R.id.textvlaz2);
             v3=getView().findViewById(R.id.textvlaz3);
             v4=getView().findViewById(R.id.textvlaz4);
             //citanje zadnje poruke poslane s modula pri prvom otvaranju dijela aplikacije za upravljanje SMS-om
            //postavljanje pocetnih vrijednosti temp. i vlaznosti tla procitanih iz zadnje poruke
            String sms="";
            Uri uri=Uri.parse("content://sms/inbox");
            String[] projection={"address","body"};
            String phoneNumber="+387603383946";
            Cursor cursor=getActivity().getContentResolver().query(uri,projection,"address=?",new String[] {phoneNumber},"date DESC LIMIT 1");
            if(cursor!=null && cursor.moveToFirst()){
                sms=cursor.getString(cursor.getColumnIndex("body"));
            }
            if(sms.indexOf("T:")!=-1){
                temp=sms.substring(sms.indexOf("T:")+2,sms.indexOf(",V1"));
                vlaz1=sms.substring(sms.indexOf("V1:")+3,sms.indexOf(",V2"));
                vlaz2=sms.substring(sms.indexOf("V2:")+3,sms.indexOf(",V3"));
                vlaz3=sms.substring(sms.indexOf("V3:")+3,sms.indexOf(",V4"));
                vlaz4=sms.substring(sms.indexOf("V4:")+3,sms.indexOf("!"));
            }
            //postavljanje zadnje poslanih vrijednosti vrijednosti temp. i vlaz. tla
            t.setText(temp);
            v1.setText(vlaz1);
            v2.setText(vlaz2);
            v3.setText(vlaz3);
            v4.setText(vlaz4);
        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            requestPermissions(new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        getActivity().registerReceiver(receiver,new IntentFilter(SMS_RECEIVED));
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_settings, container, false);
        //deklarisanje svih UI elemenata koristenih pri kreiranju izgleda u fragment_settings.xml fajlu
        texttemp=view.findViewById(R.id.texttemp);
        textvlaz1=view.findViewById(R.id.textvlaz1);
        textvlaz2=view.findViewById(R.id.textvlaz2);
        textvlaz3=view.findViewById(R.id.textvlaz3);
        textvlaz4=view.findViewById(R.id.textvlaz4);
        uptemp=view.findViewById(R.id.uptemp);
        downtemp=view.findViewById(R.id.downtemp);
        upvlaz1=view.findViewById(R.id.upvlaz1);
        downvlaz1=view.findViewById(R.id.downvlaz1);
        upvlaz2=view.findViewById(R.id.upvlaz2);
        downvlaz2=view.findViewById(R.id.downvlaz2);
        upvlaz3=view.findViewById(R.id.upvlaz3);
        downvlaz3=view.findViewById(R.id.downvlaz3);
        upvlaz4=view.findViewById(R.id.upvlaz4);
        downvlaz4=view.findViewById(R.id.downvlaz4);
        buttonsend=view.findViewById(R.id.buttonsend);
        //dugme za slanje parametara
        buttonsend.setOnClickListener(this);
        //dugmad za odabir vrijednosti parametara
        uptemp.setOnClickListener(this);
        downtemp.setOnClickListener(this);
        upvlaz1.setOnClickListener(this);
        downvlaz1.setOnClickListener(this);
        upvlaz2.setOnClickListener(this);
        downvlaz2.setOnClickListener(this);
        upvlaz3.setOnClickListener(this);
        downvlaz3.setOnClickListener(this);
        upvlaz4.setOnClickListener(this);
        downvlaz4.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v) {
        //dugme za povecavanje temp
        if(v==uptemp){
            countertemp=Integer.valueOf((String) texttemp.getText());
            countertemp++;
            texttemp.setText(String.valueOf(countertemp));
            //max odabrana temp = 35
            if(countertemp>=35){
                uptemp.setClickable(false);
            }
            else {
                uptemp.setClickable(true);
            }
        }
        //dugme za smanjivanje temp
        if(v==downtemp) {
            countertemp=Integer.valueOf((String) texttemp.getText());
            countertemp--;
            texttemp.setText(String.valueOf(countertemp));
            //min odabrana temp = 5
            if(countertemp<=5){
                downtemp.setClickable(false);
            }
            else{
                downtemp.setClickable(true);
            }
        }
        //dugme za povecavanje vlaz tla prvog dijela staklenika/plasenika
        if(v==upvlaz1){
            countervlaz1=Integer.valueOf((String) textvlaz1.getText());
            countervlaz1++;
            textvlaz1.setText(String.valueOf(countervlaz1));
            //max odabrana vlaz1 = 99
            if(countervlaz1>=99){
                upvlaz1.setClickable(false);
            }
            else
            {
                upvlaz1.setClickable(true);
            }
        }
        //dugme za smanjivanje vlaz tla prvog dijela staklenika/plasenika
        if(v==downvlaz1){
            countervlaz1=Integer.valueOf((String) textvlaz1.getText());
            countervlaz1--;
            textvlaz1.setText(String.valueOf(countervlaz1));
            //min odabrana vlaz1 = 0
            if(countervlaz1<=0){
                downvlaz1.setClickable(false);
            }
            else{
                downvlaz1.setClickable(true);
            }
        }
        //dugme za povecavanje vlaz tla drugog dijela staklenika/plasenika
        if(v==upvlaz2){
            countervlaz2=Integer.valueOf((String) textvlaz2.getText());
            countervlaz2++;
            textvlaz2.setText(String.valueOf(countervlaz2));
            //max odabrana vlaz2 = 99
            if(countervlaz2>=99){
                upvlaz2.setClickable(false);
            }
            else{
                upvlaz2.setClickable(true);
            }
        }
        //dugme za smanjivanje vlaz tla drugog dijela staklenika/plasenika
        if(v==downvlaz2){
            countervlaz2=Integer.valueOf((String) textvlaz2.getText());
            countervlaz2--;
            textvlaz2.setText(String.valueOf(countervlaz2));
            //min odabrana vlaz2 = 0
            if(countervlaz2<=0){
                downvlaz2.setClickable(false);
            }
            else{
                downvlaz2.setClickable(true);
            }
        }
        //dugme za povecavanje vlaz tla treceg dijela staklenika/plasenika
        if(v==upvlaz3){
            countervlaz3=Integer.valueOf((String) textvlaz3.getText());
            countervlaz3++;
            textvlaz3.setText(String.valueOf(countervlaz3));
            //max odabrana vlaz3 = 99
            if(countervlaz3>=99){
                upvlaz3.setClickable(false);
            }
            else{
                upvlaz3.setClickable(true);
            }
        }
        //dugme za smanjivanje vlaz tla treceg dijela staklenika/plasenika
        if(v==downvlaz3){
            countervlaz3=Integer.valueOf((String) textvlaz3.getText());
            countervlaz3--;
            textvlaz3.setText(String.valueOf(countervlaz3));
            //min odabrana vlaz3 = 0
            if(countervlaz3<=0){
                downvlaz3.setClickable(false);
            }
            else{
                downvlaz3.setClickable(true);
            }
        }
        //dugme za povecavanje vlaz tla cetvrtog dijela staklenika/plasenika
        if(v==upvlaz4){
            countervlaz4=Integer.valueOf((String) textvlaz4.getText());
            countervlaz4++;
            textvlaz4.setText(String.valueOf(countervlaz4));
            //max odabrana vlaz4 = 99
            if(countervlaz4>=99){
                upvlaz4.setClickable(false);
            }
            else{
                upvlaz4.setClickable(true);
            }
        }
        //dugme za smanjivanje vlaz tla cetvrtog dijela staklenika/plasenika
        if(v==downvlaz4){
            countervlaz4=Integer.valueOf((String) textvlaz4.getText());
            countervlaz4--;
            textvlaz4.setText(String.valueOf(countervlaz4));
            //min odabrana vlaz4 = 0
            if(countervlaz4<=0){
                downvlaz4.setClickable(false);
            }
            else{
                downvlaz4.setClickable(true);
            }
        }
        //dugme za slanje zeljenih vrijednosti temperature i vlaznosti tla
        if(v==buttonsend){
            //uzimanje podataka iz UI elemenata koji sadrze odabrane vrijednosti temp i vlaz tla
            podataktemp=texttemp.getText().toString();
            podatakvlaz1=textvlaz1.getText().toString();
            podatakvlaz2=textvlaz2.getText().toString();
            podatakvlaz3=textvlaz3.getText().toString();
            podatakvlaz4=textvlaz4.getText().toString();
            //string s odabranim podacima za slanje
            poslatiporuku="T:"+podataktemp+",V1:"+podatakvlaz1+",V2:"+podatakvlaz2+",V3:"+podatakvlaz3+",V4:"+podatakvlaz4+"!";
            String phoneNumber="+387603383946";
            if(ContextCompat.checkSelfPermission(getActivity().getBaseContext(), "android.permission.SEND_SMS") == PackageManager.PERMISSION_GRANTED)
            {
                String SENT_ACTION = "SMS_SENT_ACTION";
                String DELIVERY_ACTION = "SMS_DELIVERED_ACTION";
                PendingIntent sentIntent = PendingIntent.getBroadcast(getActivity(), 100, new Intent(), 0);

                PendingIntent deliveryIntent = PendingIntent.getBroadcast(getActivity(), 200, new
                        Intent(DELIVERY_ACTION), 0);
                getActivity().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("SMS ", "sent");
                    }
                }, new IntentFilter(SENT_ACTION));
                getActivity().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("SMS ", "delivered");
                    }
                }, new IntentFilter(DELIVERY_ACTION));
                //slanje SMS poruke sa sadrzajem zeljenih vrijednosti temp i vlaz tla
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber,null,poslatiporuku,sentIntent,deliveryIntent);
                Toasty.success(getActivity(), "Slanje SMS poruke uspješno!", Toast.LENGTH_LONG).show();
            }
            else {
                Toasty.error(getActivity()
                        , "Greška!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
