package com.example.androidapp;
//aktivnost s animacijom pri ulasku u aplikaciju
//biblioteke

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    //deklaracija UI elemenata
    private TextView naziv;
    private ImageView ikona;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //pridruživanje UI elemenata iz XML fajla
        naziv=findViewById(R.id.naziv);
        ikona=findViewById(R.id.ikona);
        //animacija pri ulasku u aplikaciju
        Animation myanim= AnimationUtils.loadAnimation(this,R.anim.mytransition);
        //pocetak animacije UI elemenata aplikacije kreiranih u pripadajucem xml fajlu
        naziv.startAnimation(myanim);
        ikona.startAnimation(myanim);
        //otvaranje nove aktivnosti nakon isteka animacije
        final Intent intent=new Intent(this,AfterSplash.class);
        //trajanje animacije
        Thread timer=new Thread(){
            public void run(){
                try{
                    sleep(5000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    startActivity(intent);
                    finish();
                }
            }
        }; timer.start();
    }
}
