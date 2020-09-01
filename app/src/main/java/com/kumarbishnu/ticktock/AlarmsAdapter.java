package com.kumarbishnu.ticktock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Alarm> alarms;

    private SQLiteStatement updateAlarmStateStatement;

    AlarmsAdapter(Context context, ArrayList<Alarm> alarms) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.alarms = alarms;
        updateAlarmStateStatement = MainActivity.database.compileStatement("UPDATE test SET state = ? WHERE id = ?");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =layoutInflater.inflate(R.layout.layout_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlarmsAdapter.ViewHolder holder, final int position) {
        final Alarm alarm = alarms.get(position);

        holder.alarmTimeTextView.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
        holder.alarmPeriodTextView.setText(alarm.getPeriodName());

        if (alarm.isActive()) {
            holder.alarmStateSwitch.setChecked(true);
            holder.alarmTimeTextView.setTextAppearance(R.style.Time);
            holder.alarmPeriodTextView.setTextAppearance(R.style.Time_Period);
        }


        holder.daysLinearLayout.setVisibility(View.INVISIBLE);
        holder.repeatTextView.setVisibility(View.VISIBLE);

        if (alarm.getRepeatCount() == 0) {
            holder.repeatTextView.setText(R.string.single);
        } else if (alarm.getRepeatCount() == 7) {
            holder.repeatTextView.setText(R.string.everyday);
        } else {
            boolean[] b = alarm.getRepeat();
            holder.repeatTextView.setVisibility(View.INVISIBLE);
            holder.daysLinearLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < b.length; i++) {
                TextView t = (TextView) holder.daysLinearLayout.getChildAt(i);
                if (b[i]) {
                    t.setTextAppearance(R.style.Alarm_Repeat);
                } else {
                    t.setTextAppearance(R.style.Alarm_Repeat_Disabled);
                }
            }
        }

        holder.alarmConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditAlarmActivity.class);
                intent.putExtra("alarm", alarm);
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });

        holder.alarmStateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    updateAlarmStateStatement.bindString(1, Integer.toString(1));
                    holder.alarmTimeTextView.setTextAppearance(R.style.Time);
                    holder.alarmPeriodTextView.setTextAppearance(R.style.Time_Period);
                } else {
                    updateAlarmStateStatement.bindString(1, Integer.toString(0));
                    holder.alarmTimeTextView.setTextAppearance(R.style.Time_Disabled);
                    holder.alarmPeriodTextView.setTextAppearance(R.style.Time_Period_Disabled);
                }
                updateAlarmStateStatement.bindString(2, Integer.toString(alarms.get(position).getId()));
                updateAlarmStateStatement.execute();
                AlarmFragment.alarms = alarms;

                Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmService.class);
                PendingIntent alarmPendingIntent = PendingIntent.getService(context.getApplicationContext(), alarm.getId(), alarmIntent, 0);

                AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(alarmPendingIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView alarmTimeTextView, alarmPeriodTextView, repeatTextView;
        LinearLayout daysLinearLayout;
        SwitchMaterial alarmStateSwitch;
        ConstraintLayout alarmConstraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmTimeTextView = itemView.findViewById(R.id.alarmTimeTextView);
            alarmPeriodTextView = itemView.findViewById(R.id.alarmPeriodTextView);
            repeatTextView = itemView.findViewById(R.id.repeatTextView);
            daysLinearLayout = itemView.findViewById(R.id.daysLinearLayout);
            alarmConstraintLayout = itemView.findViewById(R.id.alarmConstraintLayout);
            alarmStateSwitch = itemView.findViewById(R.id.alarmSateSwitch);
        }
    }
}
