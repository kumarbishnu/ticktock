package com.kumarbishnu.ticktock;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class WorldClockFragment extends Fragment {

    private View view;
    private TextView timezoneTextView, timeTextView, dateTextView, zoneNameTextView;
    private ImageView hourImageView, secondImageView, minuteImageView;

    private TimeZone timezone;
    private long milliseconds;
    private Runnable runnable;

    private static final Calendar calendar = Calendar.getInstance();
    private static final Handler handler = new Handler();
    private static final SimpleDateFormat stf = new SimpleDateFormat("hh:mm a");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_world_clock, container, false);
        timezone = TimeZone.getTimeZone(getArguments().getString("timezone"));

        initComponents();

        timezone.getID().split("/");
        String name = "";
        for (String i: timezone.getID().split("/")) {name = i + ", " + name;}

        timezoneTextView.setText(name.substring(0, name.length()-2));
        zoneNameTextView.setText(timezone.getDisplayName());

        startClock();

        return view;
    }

    private void initComponents() {
        timezoneTextView = view.findViewById(R.id.timezoneTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        dateTextView = view.findViewById(R.id.dateTextView);
        zoneNameTextView = view.findViewById(R.id.zoneNameTextView);

        hourImageView = getActivity().findViewById(R.id.hourImageView);
        minuteImageView = getActivity().findViewById(R.id.minuteImageView);
        secondImageView = getActivity().findViewById(R.id.secondImageView);
    }

    private long getGMTTime() {

        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();

        int offset = timeZone.getRawOffset();
        if (timeZone.inDaylightTime(calendar.getTime())) {
            offset += timeZone.getDSTSavings();
        }
        return calendar.getTimeInMillis() - offset;

    }

    private void startClock() {
        milliseconds = getGMTTime() + timezone.getRawOffset();
        calendar.setTimeInMillis(milliseconds);

        timeTextView.setText(stf.format(calendar.getTime()).toUpperCase());
        dateTextView.setText(sdf.format(calendar.getTime()));

        runnable = new Runnable() {
            @Override
            public void run() {
            calendar.setTimeInMillis(milliseconds);

            timeTextView.setText(stf.format(calendar.getTime()).toUpperCase());
            dateTextView.setText(sdf.format(calendar.getTime()));

            int hour = calendar.get(Calendar.HOUR);
            int mins = calendar.get(Calendar.MINUTE);
            int secs = calendar.get(Calendar.SECOND);

            hourImageView.setRotation((float) 360 / (12 * 60) * (hour * 60 + mins));
            minuteImageView.setRotation((float) 360 / (60 * 60) * (mins * 60 + secs));
            secondImageView.setRotation((float) 360 / 60 * secs);

            milliseconds += 1000;
        handler.postDelayed(this, 1000);
            }
         };;
    }

    @Override
    public void onPause() {
        super.onPause();

        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.removeCallbacks(runnable);
        milliseconds = getGMTTime() + timezone.getRawOffset();
        positionHands();
        handler.post(runnable);
    }

    private void positionHands() {
        calendar.setTimeInMillis(milliseconds);

        int hour = calendar.get(Calendar.HOUR);
        int mins = calendar.get(Calendar.MINUTE);
        int secs = calendar.get(Calendar.SECOND);

        hourImageView.animate().rotation((float) 360 / (12 * 60) * (hour * 60 + mins)).setDuration(1000);
        minuteImageView.animate().rotation((float) 360 / (60 * 60) * (mins * 60 + secs)).setDuration(1000);
        secondImageView.animate().rotation((float) 360 / 60 * secs).setDuration(600);
    }

}