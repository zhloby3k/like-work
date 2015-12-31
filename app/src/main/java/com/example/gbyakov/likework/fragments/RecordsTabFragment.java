package com.example.gbyakov.likework.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gbyakov.likework.R;

public class RecordsTabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View x = inflater.inflate(R.layout.fragment_records_tab,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        viewPager.setAdapter(new RecordsPagerAdapter(getChildFragmentManager()));
        tabLayout.post(new Runnable() {

            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Запись");

        return x;

    }

    class RecordsPagerAdapter extends FragmentPagerAdapter{

        public RecordsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0 : return new RecordsListFragment();
                case 1 : return new RecordsListFragment();
                case 2 : return new RecordsListFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 : return "Сегодня";
                case 1 : return "Завтра";
                case 2 : return "Послезавтра";
            }
            return null;
        }
    }
}