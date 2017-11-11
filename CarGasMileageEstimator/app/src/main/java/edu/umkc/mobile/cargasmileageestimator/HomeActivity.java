package edu.umkc.mobile.cargasmileageestimator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.content,new HomeFragment() ).commit();
                    return true;
                case R.id.navigation_addFuel:
                    fragmentTransaction.replace(R.id.content,new AddFuelFragment() ).commit();
                    return true;
                case R.id.navigation_stats:
                    fragmentTransaction.replace(R.id.content,new StatsFragment() ).commit();
                    return true;
                case R.id.navigation_settings:
                    fragmentTransaction.replace(R.id.content,new SettingsFragment() ).commit();
                    return true;
                case R.id.navigation_testDrive:
                    fragmentTransaction.replace(R.id.content,new SettingsFragment() ).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content,new HomeFragment() ).commit();
    }

}
