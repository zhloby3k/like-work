package com.example.gbyakov.likework.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.data.LikeWorkContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecordsTabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3;
    public static ArrayList<String> pages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FillTabs();

        View x = inflater.inflate(R.layout.fragment_records_tab, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        viewPager.setAdapter(new RecordsPagerAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle("Запись");

        return x;

    }

    private void FillTabs() {

        pages = new ArrayList<>();

        long dateInms = 0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateInms = dateFormat.parse("2013-02-22").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String selection = LikeWorkContract.RecordEntry.TABLE_NAME + "." +
                LikeWorkContract.RecordEntry.COLUMN_DATE + " >= ?";
        String[] selectionArgs = new String[]{String.valueOf(dateInms)};

        Cursor cursor = getContext().getContentResolver().query(
                LikeWorkContract.RecordEntry.buildOrderDates(),
                null,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor != null) {
            int_items = cursor.getCount();
            while (cursor.moveToNext()) {
                pages.add(cursor.getString(cursor.getColumnIndex(LikeWorkContract.RecordEntry.COLUMN_DATE)));
            }
        }
    }

    private static String getFriendlyDayString(Context context, String date) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date mDate = new Date();
        try {
            mDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(mDate.getTime(), time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay < currentJulianDay + 7 && julianDay > currentJulianDay) {
            return getDayName(context, mDate.getTime());
        } else {
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE dd MMM");
            return shortenedDateFormat.format(mDate.getTime());
        }
    }

    private static String getDayName(Context context, long dateInMillis) {

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        } else {
            Time time = new Time();
            time.setToNow();
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    class RecordsPagerAdapter extends FragmentPagerAdapter{

        public RecordsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (!pages.isEmpty() && pages.size() >= position) {
                Bundle args = new Bundle();
                args.putString("date", pages.get(position));

                RecordsListFragment fRecord = new RecordsListFragment();
                fRecord.setArguments(args);
                return fRecord;
            }

            else
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

            if (!pages.isEmpty() && pages.size() >= position)
                return getFriendlyDayString(getContext(), pages.get(position));
            else
                switch (position){
                    case 0 : return "Сегодня";
                    case 1 : return "Завтра";
                    case 2 : return "Послезавтра";
                }
            return null;
        }
    }
}