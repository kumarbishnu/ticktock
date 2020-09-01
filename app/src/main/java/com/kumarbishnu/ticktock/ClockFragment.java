package com.kumarbishnu.ticktock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class ClockFragment extends Fragment {

    private View view;
    private ViewPager2 viewPager;
    private ImageView hourImageView, minuteImageView, secondImageView;
    private FloatingActionButton editZonesButton;

    private WorldClockAdapter adapter;
    private String[] timezonesList;
    private ArrayList<String> myTimezones;
    private boolean[] checkedItems;
    private TimeZone currentZone;

    private SQLiteStatement insertTimezoneStatement, deleteTimezoneStatement;

    private static final SimpleDateFormat stf = new SimpleDateFormat("hh:mm a");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_clock, container, false);


        insertTimezoneStatement = MainActivity.database.compileStatement("INSERT INTO timezones VALUES (?)");
        deleteTimezoneStatement = MainActivity.database.compileStatement("DELETE FROM timezones WHERE timezone = ?");

        initComponents();
        initClock();
        initTimezoneData();

        adapter = new WorldClockAdapter(this, myTimezones);
        viewPager = view.findViewById(R.id.timeZoneViewPager);
        viewPager.setAdapter(adapter);

        return view;
    }

    private void initComponents() {
        hourImageView = view.findViewById(R.id.hourImageView);
        minuteImageView = view.findViewById(R.id.minuteImageView);
        secondImageView = view.findViewById(R.id.secondImageView);

        editZonesButton = view.findViewById(R.id.editZonesButton);
        editZonesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openZonesMenu();
            }
        });
    }

    private void initClock() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int mins = calendar.get(Calendar.MINUTE);
        int secs = calendar.get(Calendar.SECOND);

        hourImageView.setRotation((float) 360 / (12 * 60) * (hour * 60 + mins));
        minuteImageView.setRotation((float) 360 / (60 * 60) * (mins * 60 + secs));
        secondImageView.setRotation((float) 360 / 60 * secs);
    }


    private void initTimezoneData() {
        currentZone = Calendar.getInstance().getTimeZone();
        timezonesList = TimeZone.getAvailableIDs();
        myTimezones = new ArrayList<>();

        Cursor c = MainActivity.database.rawQuery("SELECT * FROM timezones", null);
        int index = c.getColumnIndex("timezone");
        while (c.moveToNext()) {
            myTimezones.add(c.getString(index));
        }
        c.close();


        if (!myTimezones.contains(currentZone)) {
            myTimezones.add(0, Calendar.getInstance().getTimeZone().getID());
        }

        checkedItems = new boolean[timezonesList.length];
        for (int i = 0; i < timezonesList.length; i++) {
            if (myTimezones.contains(timezonesList[i])) {checkedItems[i] = true;}
        }
    }

    private void openZonesMenu() {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle("Edit Timezones");
        builder.setMultiChoiceItems(timezonesList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    myTimezones.add(timezonesList[i]);
                    insertTimezoneStatement.bindString(1, timezonesList[i]);
                    insertTimezoneStatement.execute();
                } else {
                    myTimezones.remove(timezonesList[i]);
                    deleteTimezoneStatement.bindString(1, timezonesList[i]);
                    deleteTimezoneStatement.execute();
                }
                checkedItems[i] = b;
            }
        });
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.notifyDataSetChanged();
                viewPager.setAdapter(adapter);
//                viewPager.setCurrentItem(0, true);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
        builder.create();
    }
}