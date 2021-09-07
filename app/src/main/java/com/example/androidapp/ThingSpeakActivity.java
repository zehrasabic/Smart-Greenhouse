package com.example.androidapp;
//aktivnost za upravljanje i nadzor staklenika/plastenika koristenjem GPRS usluge mobilne mreze
//sadrzi 3 fragmenta: nadzor temp., nadzor vlaz. tla, upravljanje - konfiguracija parametara
//biblioteke

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.dmoral.toasty.Toasty;

public class ThingSpeakActivity extends AppCompatActivity {
    //deklarisanje fragmenata
    final Fragment fragment1=new Temperature1Fragment();
    final Fragment fragment2=new SoilMoisture1Fragment();
    final Fragment fragment3=new Settings1Fragment();
    final FragmentManager fm=getSupportFragmentManager();
    //pocetni aktivni fragment
    Fragment active=fragment1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_speak);
        //prilikom otvaranje aktivnosti ispis poruke da je aktivirana GPRS opcija
        Toasty.info(ThingSpeakActivity.this,"GPRS usluga aktivirana!", Toast.LENGTH_SHORT).show();
        BottomNavigationView navigation=findViewById(R.id.bottom_navigationn);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //transakcija sadrzaja fragmenata
        fm.beginTransaction().add(R.id.main_containerr,fragment3,"3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_containerr,fragment2,"2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_containerr,fragment1,"1").commit();


    }
    //navigacioni meni za otvaranje proizvoljnog fragmenta
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.temperaturee:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active=fragment1;
                    return true;
                case R.id.soilmoisturee:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active=fragment2;
                    return true;
                case R.id.settingss:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active=fragment3;
                    return true;
            }
            return false;
        }
    };
    //pritiskom na tipku za nazad, vraca se na izbornik
    @Override
    public void onBackPressed() {
        //ThingSpeakActivity.this.finish();
        System.exit(0);
    }
}
