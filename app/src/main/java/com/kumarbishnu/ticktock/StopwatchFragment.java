package com.kumarbishnu.ticktock;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StopwatchFragment extends Fragment {

    private View view;
    private TextView stopwatchTextView;
    private FloatingActionButton startStopwatchButton, stopStopwatchButton, resetStopwatchButton;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private int seconds = 0;

    private int state;
    private static final int STOPPED = -1;
    private static final int PAUSED = 0;
    private static final int RUNNING = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        initComponents();
        addButtonListeners();

        return view;
    }

    private void initComponents() {
        stopwatchTextView = view.findViewById(R.id.stopwatchTextView);
        startStopwatchButton = view.findViewById(R.id.startStopwatchButton);
        stopStopwatchButton = view.findViewById(R.id.stopStopwatchButton);
        resetStopwatchButton = view.findViewById(R.id.resetStopwatchButton);

        runnable = new Runnable() {
            @Override
            public void run() {
                String time = String.format("%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60);
                stopwatchTextView.setText(time);
                seconds++;
                handler.postDelayed(this, 1000);
            }
        };
    }

    private void addButtonListeners() {
        startStopwatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (state) {
                    case STOPPED:
                        startStopwatch();
                        break;
                    case RUNNING:
                        pauseStopwatch();
                        break;
                    case PAUSED:
                        resumeStopwatch();
                        break;
                }
            }
        });
        stopStopwatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopStopwatch();
            }
        });
        resetStopwatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetStopwatch();
            }
        });
    }

    private void startStopwatch() {
        resetStopwatch();
        state = RUNNING;
        handler.post(runnable);
        startStopwatchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause, getContext().getTheme()));
    }

    private void pauseStopwatch() {
        state = PAUSED;
        handler.removeCallbacks(runnable);
        startStopwatchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_start, getContext().getTheme()));
    }

    private void resumeStopwatch() {
        state = RUNNING;
        handler.post(runnable);
        startStopwatchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause, getContext().getTheme()));
    }

    private void stopStopwatch() {
        handler.removeCallbacks(runnable);
        startStopwatchButton.setEnabled(false);
    }

    private void resetStopwatch() {
        state = STOPPED;
        handler.removeCallbacks(runnable);
        stopwatchTextView.setText(R.string.stopwatch_def);
        seconds = 0;
        startStopwatchButton.setEnabled(true);
        startStopwatchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_start, getContext().getTheme()));
    }

}