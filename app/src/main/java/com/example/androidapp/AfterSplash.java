package com.example.androidapp;
//aktivnost otvorena nakon animacije, sadrzi prikaz izbornika usluge mobilne mreze (SMS, GPRS)
//biblioteke

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import es.dmoral.toasty.Toasty;

public class AfterSplash extends AppCompatActivity {
    //deklarisanje svih UI elemenata koristenih pri kreiranju izgleda u activity_after_splash.xml fajlu
    Button smsButton;
    Button ThingSpeakButton;
    private static final String SMS_RECEIVED="android.provider.Telephony.SMS_RECEIVED";
    //prijemnik za nove poruke koje stizu na mobilni uredjaj
    MyReceiver receiver=new MyReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            //pretraga odabrane opcije za upravljanje i nadzor staklenika/plastenika
            //odabrana SMS usluga mobilne mreze
            if(msg.indexOf("Sms")!=-1){
                //otvaranje nove aktivnosti gdje se koristi SMS
                Intent sms=new Intent(AfterSplash.this,SMSActivity.class);
                startActivity(sms);
            }
            //odabrana GPRS usluga mobilne mreze
            else if(msg.indexOf("Gprs")!=-1){
                //otvaranje nove aktivnosti gdje se koristi GPRS
                Intent gprs=new Intent(AfterSplash.this,ThingSpeakActivity.class);
                startActivity(gprs);
            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver,new IntentFilter(SMS_RECEIVED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_splash);
        smsButton=findViewById(R.id.idSms);
        ThingSpeakButton=findViewById(R.id.idThingspeak);
        //ako se odobri dozvola za upotrebu SMS usluga unutar aplikacije, dugmad su osposobljena za pritisak
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.SEND_SMS") == PackageManager.PERMISSION_GRANTED) {
            smsButton.setEnabled(true);
            ThingSpeakButton.setEnabled(true);
        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;

            requestPermissions(new String[]{"android.permission.SEND_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);

        }
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.RECEIVE_SMS") == PackageManager.PERMISSION_GRANTED) {
        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            requestPermissions(new String[]{"android.permission.RECEIVE_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        //odabir SMS
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //slanje poruke "odabranSms" adresi SIM kartice u GSM/GPRS A6 modulu
                String poslatiporuku1="odabranSms";
                String phoneNumber="+387603383946";
                if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.SEND_SMS") == PackageManager.PERMISSION_GRANTED)
                {
                    String SENT_ACTION = "SMS_SENT_ACTION";
                    String DELIVERY_ACTION = "SMS_DELIVERED_ACTION";
                    PendingIntent sentIntent = PendingIntent.getBroadcast(AfterSplash.this, 100, new Intent(), 0);

                    PendingIntent deliveryIntent = PendingIntent.getBroadcast(AfterSplash.this, 200, new
                            Intent(DELIVERY_ACTION), 0);

                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Log.d("SMS ", "sent");
                        }
                    }, new IntentFilter(SENT_ACTION));

                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Log.d("SMS ", "delivered");
                        }
                    }, new IntentFilter(DELIVERY_ACTION));

                    SmsManager smsManager=SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber,null,poslatiporuku1,sentIntent,deliveryIntent);
                    Toasty.success(AfterSplash.this,"Slanje SMS poruke uspješno!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toasty.error(AfterSplash.this, "Greška!", Toast.LENGTH_LONG).show();
                }
            }
        });
        //odabir GPRS
        ThingSpeakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //slanje poruke "odabranGprs" adresi SIM kartice u GSM/GPRS A6 modulu
                String poslatiporuku2="odabranGprs";
                String phoneNumber="+387603383946";
                if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.SEND_SMS") == PackageManager.PERMISSION_GRANTED)
                {
                    String SENT_ACTION = "SMS_SENT_ACTION";
                    String DELIVERY_ACTION = "SMS_DELIVERED_ACTION";
                    PendingIntent sentIntent = PendingIntent.getBroadcast(AfterSplash.this, 100, new Intent(), 0);
                    PendingIntent deliveryIntent = PendingIntent.getBroadcast(AfterSplash.this, 200, new
                            Intent(DELIVERY_ACTION), 0);
                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Log.d("SMS ", "sent");
                        }
                    }, new IntentFilter(SENT_ACTION));
                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Log.d("SMS ", "delivered");
                        }
                    }, new IntentFilter(DELIVERY_ACTION));
                    SmsManager smsManager=SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber,null,poslatiporuku2,sentIntent,deliveryIntent);
                    Toasty.success(AfterSplash.this,"Slanje SMS poruke uspješno!",Toast.LENGTH_LONG).show();
                }
                else {
                    Toasty.error(AfterSplash.this, "Greška!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
