package com.kumarbishnu.ticktock;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    public static AlarmsAdapter alarmsAdapter;
    public static ArrayList<Alarm> alarms;
    private FloatingActionButton addNewAlarmButton;

    public static int ALARM_ID = -1;
    public static SQLiteStatement deleteAlarmStatement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_alarm, container, false);

        initComponents();
        getAlarmsFromDB();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmsAdapter = new AlarmsAdapter(getContext(), alarms);
        recyclerView.setAdapter(alarmsAdapter);

        return view;
    }


    private void initComponents() {
        recyclerView = view.findViewById(R.id.alarmsRecyclerView);
        alarms = new ArrayList<>();

        addNewAlarmButton = view.findViewById(R.id.addNewAlarmButton);
        addNewAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditAlarmActivity.class));
            }
        });
    }

    private void getAlarmsFromDB() {
        Cursor c = MainActivity.database.rawQuery("SELECT * FROM test", null);

        int idIndex = c.getColumnIndex("id");
        int hourIndex = c.getColumnIndex("hour");
        int minuteIndex = c.getColumnIndex("minute");
        int periodIndex = c.getColumnIndex("period");
        int soundIndex = c.getColumnIndex("sound");
        int snoozeIndex = c.getColumnIndex("snooze");
        int stateIndex = c.getColumnIndex("state");
        int sunIndex = c.getColumnIndex("sun");
        int monIndex = c.getColumnIndex("mon");
        int tueIndex = c.getColumnIndex("tue");
        int wedIndex = c.getColumnIndex("wed");
        int thuIndex = c.getColumnIndex("thu");
        int friIndex = c.getColumnIndex("fri");
        int satIndex = c.getColumnIndex("sat");

        while (c.moveToNext()) {
            int id = c.getInt(idIndex);
            int hour = c.getInt(hourIndex);
            int minute = c.getInt(minuteIndex);
            int period = c.getInt(periodIndex);
            int sound = c.getInt(soundIndex);
            int snooze = c.getInt(snoozeIndex);
            boolean state = false;
            boolean[] repeat = new boolean[7];

            if (c.getInt(stateIndex) == 1) {state = true;}

            int[] days = {sunIndex, monIndex, tueIndex, wedIndex, thuIndex, friIndex, satIndex};
            for (int i = 0; i < days.length; i++) {
                if (c.getInt(days[i]) == 1) {repeat[i] = true;}
            }

            alarms.add(new Alarm(id, hour, minute, period, repeat, sound, snooze, state));
            ALARM_ID = id;
        }
    }
}