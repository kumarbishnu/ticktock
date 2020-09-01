package com.kumarbishnu.ticktock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    public static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = getApplicationContext().openOrCreateDatabase("clockDB", Context.MODE_PRIVATE, null);
//        database.execSQL("DROP TABLE test");
        database.execSQL("CREATE TABLE IF NOT EXISTS timezones (timezone VARCHAR)");
        database.execSQL("CREATE TABLE IF NOT EXISTS test(id INT(3) PRIMARY KEY, hour INT(2), minute INT(2), period INT(1), sound INT(1), snooze INT(1), state INT(1), sun INT(1), mon INT(1), tue INT(1), wed INT(1), thu INT(1), fri INT(1), sat INT(1))");


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.clockFragment:
                        selectedFragment = new ClockFragment();
                        break;
                    case R.id.alarmFragment:
                        selectedFragment = new AlarmFragment();
                        break;
                    case R.id.timerFragment:
                        selectedFragment = new TimerFragment();
                        break;
                    case R.id.stopwatchFragment:
                        selectedFragment = new StopwatchFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, selectedFragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.clockFragment);
    }
}