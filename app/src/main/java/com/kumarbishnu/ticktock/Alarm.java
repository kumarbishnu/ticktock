package com.kumarbishnu.ticktock;

import java.io.Serializable;

public class Alarm implements Serializable {

    private int id;
    private int hour;
    private int minute;
    private int period;
    private boolean[] repeat;
    private int sound;
    private int snooze;
    private boolean active;

    public Alarm(int id, int hour, int minute, int period, boolean[] repeat, int sound, int snooze, boolean active) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.period = period;
        this.repeat = repeat;
        this.sound = sound;
        this.snooze = snooze;
        this.active = active;
    }

    public int getId() {return this.id;}

    public void setHour(int hour) {this.hour = hour;}
    public int getHour() {return this.hour;}

    public void setMinute(int minute) {this.minute = minute;}
    public int getMinute() {return this.minute;}

    public void setPeriod(int period) {this.period = period;}
    public int getPeriod() {return this.period;}

    public String getPeriodName() {
        if (this.period == 0) {
            return "AM";
        } else {
            return "PM";
        }
    }

    public void setSound(int sound) {this.sound = sound;}
    public int getSound() {return this.sound;}

    public void setSnooze(int snooze) {this.snooze = snooze;}
    public int getSnooze() {return this.snooze;}

    public void setActive(boolean active) {this.active = active;}
    public boolean isActive() {return this.active;}

    public void setRepeat(boolean[] repeat) {this.repeat = repeat;}
    public boolean[] getRepeat() {return this.repeat;}

    public int getRepeatCount() {
        int count = 0;
        for (boolean b: repeat) {
            if (b) {count += 1;}
        }
        return count;
    }
}
