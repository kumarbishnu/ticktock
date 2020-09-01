package com.kumarbishnu.ticktock;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class WorldClockAdapter extends FragmentStateAdapter {

    ArrayList<String> timezones;

    public WorldClockAdapter(@NonNull Fragment fragment, ArrayList<String> timezones) {
        super(fragment);
        this.timezones = timezones;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("timezone", timezones.get(position));

        WorldClockFragment fragment = new WorldClockFragment();
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public int getItemCount() {
        return timezones.size();
    }
}
