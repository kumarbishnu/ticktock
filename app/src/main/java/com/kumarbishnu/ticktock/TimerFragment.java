package com.kumarbishnu.ticktock;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TimerFragment extends Fragment {

    private View view;
    private TextView timerTextView;
    private NumberPicker timerHourPicker, timerMinutePicker, timerSecondPicker;
    private FloatingActionButton startTimerButton;
    private Button cancelTimerButton;

    private CountDownTimer timer;

    private static final int STOPPED = -1;
    private static final int PAUSED = 0;
    private static final int RUNNING = 1;
    private int state = STOPPED;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_timer, container, false);

        initComponents();
        initNumberPickers();
        addButtonListeners();

        return view;
    }

    private void initComponents() {
        timerHourPicker = view.findViewById(R.id.timerHourPicker);
        timerMinutePicker = view.findViewById(R.id.timerMinutePicker);
        timerSecondPicker = view.findViewById(R.id.timerSecondPicker);

        startTimerButton = view.findViewById(R.id.startTimerButton);
        cancelTimerButton = view.findViewById(R.id.cancelTimerButton);
        cancelTimerButton.setVisibility(View.INVISIBLE);
    }

    private void initNumberPickers() {
        timerHourPicker.setMaxValue(99);
        timerMinutePicker.setMaxValue(59);
        timerSecondPicker.setMaxValue(59);

        NumberPicker.Formatter  formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        };

        timerHourPicker.setFormatter(formatter);
        timerMinutePicker.setFormatter(formatter);
        timerSecondPicker.setFormatter(formatter);
    }

    private void addButtonListeners() {
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (state) {
                    case STOPPED:
                        startTimer();
                        break;
                    case RUNNING:
                        pauseTimer();
                        break;
                    case PAUSED:
                        resumeTimer();
                        break;
                }
            }
        });
        cancelTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });
    }

    private void startTimer() {
        timer = new CountDownTimer(getTimerInMillis(), 1000) {
            @Override
            public void onTick(long l) {
                int sec = (int) l / 1000;
                timerHourPicker.setValue(sec/3600);
                timerMinutePicker.setValue(sec/60);
                timerSecondPicker.setValue(sec%60);
            }

            @Override
            public void onFinish() {
                resetTimer();
            }
        };
        timer.start();
        state = RUNNING;
        cancelTimerButton.setVisibility(View.VISIBLE);
        startTimerButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause, getContext().getTheme()));
    }

    private void pauseTimer() {
        timer.cancel();
        state = PAUSED;
        startTimerButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_start, getContext().getTheme()));
    }

    private void resumeTimer() {
        timer.start();
        state = RUNNING;
        startTimerButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause, getContext().getTheme()));
    }

    private void resetTimer() {
        timer.cancel();
        timerHourPicker.setValue(0);
        timerMinutePicker.setValue(0);
        timerSecondPicker.setValue(0);
        state = STOPPED;
        cancelTimerButton.setVisibility(View.INVISIBLE);
        startTimerButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_start, getContext().getTheme()));
    }

    private int getTimerInMillis() {
        int hrs = timerHourPicker.getValue();
        int mins = timerMinutePicker.getValue();
        int secs = timerSecondPicker.getValue();
        return (hrs * 3600 + mins * 60 + secs) * 1000;
    }
}