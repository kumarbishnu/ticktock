package com.kumarbishnu.ticktock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditAlarmActivity extends AppCompatActivity {

    private NumberPicker hourPicker, minutePicker, periodPicker;
    private ChipGroup repeatChipGroup;
    private Spinner soundSpinner, snoozeSpinner;
    private FloatingActionButton addAlarmButton, cancelAlarmButton;

    private Alarm alarm;
    private int index;
    private String[] snoozeArray;
    private List<Field> rawFiles;

    private int hour, minute, period, sound, snooze;
    private boolean[] repeat;
    ArrayList<Alarm> alarms;

    public static SQLiteStatement addAlarmStatement, updateAlarmStatement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        addAlarmStatement = MainActivity.database.compileStatement("INSERT INTO test VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        updateAlarmStatement = MainActivity.database.compileStatement("UPDATE test SET hour = ?, minute = ?, period = ?, sound = ?, snooze = ?, state = ?, sun = ?, mon = ?, tue = ?, wed = ?, thu = ?, fri = ?, sat = ?  WHERE id = ?");

        initComponents();

        Intent intent = getIntent();
        if (intent.hasExtra("alarm")) {
            alarm = (Alarm) intent.getSerializableExtra("alarm");
            setAlarmValues();
        }
        index = intent.getIntExtra("index", -1);

    }

    private void initComponents() {
        hourPicker = findViewById(R.id.alarmHourPicker);
        minutePicker = findViewById(R.id.alarmMinutePicker);
        periodPicker = findViewById(R.id.alarmPeriodPicker);

        repeatChipGroup = findViewById(R.id.repeatChipGroup);
        soundSpinner = findViewById(R.id.soundSpinner);
        snoozeSpinner = findViewById(R.id.snoozeSpinner);

        addAlarmButton = findViewById(R.id.addNewAlarmButton);
        cancelAlarmButton = findViewById(R.id.cancelAlarmButton);


        hourPicker.setMaxValue(11);
        minutePicker.setMaxValue(59);
        periodPicker.setMaxValue(1);

        Calendar calendar = Calendar.getInstance();
        hourPicker.setValue(calendar.get(Calendar.HOUR));
        minutePicker.setValue(calendar.get(Calendar.MINUTE));

        String[] periods = {"AM", "PM"};
        periodPicker.setDisplayedValues(periods);

        Field[] rawFilesArray = R.raw.class.getFields();
        List<String> sounds = new ArrayList<>();
        for (Field field: rawFilesArray) {
            try {
                if (field.getInt(field) == R.raw.splash) {continue;}
            } catch (Exception e) {e.printStackTrace();}
            sounds.add(field.getName().replace("_", " "));
        }
        soundSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, sounds));

        snoozeArray = getResources().getStringArray(R.array.snooze);
        snoozeSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, snoozeArray));


        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });
        cancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setAlarmValues() {
        hourPicker.setValue(alarm.getHour());
        minutePicker.setValue(alarm.getMinute());
        periodPicker.setValue(alarm.getPeriod());
        boolean[] repeatDays = alarm.getRepeat();
        for (int i = 0; i < repeatChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) repeatChipGroup.getChildAt(i);
            chip.setChecked(repeatDays[i]);
        }
        soundSpinner.setSelection(alarm.getSound());
        snoozeSpinner.setSelection(alarm.getSnooze());
    }

    private void setAlarm() {
        hour = hourPicker.getValue();
        minute = minutePicker.getValue();
        period = periodPicker.getValue();

        repeat = new boolean[7];
        for (int i = 0; i < repeatChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) repeatChipGroup.getChildAt(i);
            if (chip.isChecked()) {
                repeat[i] = true;
            }
        }

        sound = soundSpinner.getSelectedItemPosition();
        snooze = snoozeSpinner.getSelectedItemPosition();

        alarms = AlarmFragment.alarms;
        if (index == -1) {
            addAlarm();
        } else {
            updateAlarm();
        }
        scheduleAlarm();
        AlarmFragment.alarms = alarms;
        AlarmFragment.alarmsAdapter.notifyDataSetChanged();

        finish();
    }


    private void addAlarm() {
        AlarmFragment.ALARM_ID += 1;

        addAlarmStatement.bindString(1, Integer.toString(AlarmFragment.ALARM_ID));
        addAlarmStatement.bindString(2, Integer.toString(hour));
        addAlarmStatement.bindString(3, Integer.toString(minute));
        addAlarmStatement.bindString(4, Integer.toString(period));
        addAlarmStatement.bindString(5, Integer.toString(sound));
        addAlarmStatement.bindString(6, Integer.toString(snooze));
        addAlarmStatement.bindString(7, Integer.toString(1));

        int bindIndex = 8;
        for (boolean b: repeat) {
            if (b) {
                addAlarmStatement.bindString(bindIndex, Integer.toString(1));
            } else {
                addAlarmStatement.bindString(bindIndex, Integer.toString(0));
            }
            bindIndex += 1;
        }
        addAlarmStatement.execute();

        alarm = new Alarm(AlarmFragment.ALARM_ID, hour, minute, period, repeat, sound, snooze, true);
        alarms.add(alarm);
    }

    private void updateAlarm() {
        updateAlarmStatement.bindString(1, Integer.toString(hour));
        updateAlarmStatement.bindString(2, Integer.toString(minute));
        updateAlarmStatement.bindString(3, Integer.toString(period));
        updateAlarmStatement.bindString(4, Integer.toString(sound));
        updateAlarmStatement.bindString(5, Integer.toString(snooze));
        updateAlarmStatement.bindString(6, Integer.toString(1));

        int bindIndex = 7;
        for (boolean b: repeat) {
            if (b) {
                updateAlarmStatement.bindString(bindIndex, Integer.toString(1));
            } else {
                updateAlarmStatement.bindString(bindIndex, Integer.toString(0));
            }
            bindIndex += 1;
        }

        updateAlarmStatement.bindString(bindIndex, Integer.toString(alarms.get(index).getId()));
        updateAlarmStatement.execute();

        alarms.get(index).setHour(hour);
        alarms.get(index).setMinute(minute);
        alarms.get(index).setPeriod(period);
        alarms.get(index).setRepeat(repeat);
        alarms.get(index).setSound(sound);
        alarms.get(index).setSnooze(snooze);
        alarms.get(index).setActive(true);
        alarm = alarms.get(index);
    }

    private void scheduleAlarm() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

        long millis = c.getTimeInMillis();
        long excess = millis % (1000 * 60);
        millis -= excess;

        int hour_diff = hour - c.get(Calendar.HOUR_OF_DAY);
        int mins_diff = minute - c.get(Calendar.MINUTE);

        if (period == 1) {hour_diff += 12;}
        if (mins_diff < 0) {mins_diff += 60; hour_diff -= 1;}
        if (hour_diff < 0) {hour_diff += 24;}

        long extra = (hour_diff * 60 + mins_diff) * 60 * 1000;
        millis += extra;
        Log.i("secs", Long.toString(millis));
        Log.i("Time", sdf.format(new Date(millis)));

        String toast = String.format("Alarm set for %d hours and %d minutes from now.", hour_diff, mins_diff);
        if (hour_diff == 0) {toast = String.format("Alarm set for %d minutes from now.", mins_diff);}
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();


        Intent alarmIntent = new Intent(getApplicationContext(), AlarmService.class);
        PendingIntent alarmPendingIntent = PendingIntent.getService(getApplicationContext(), alarm.getId(), alarmIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, millis, alarmPendingIntent);

    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}