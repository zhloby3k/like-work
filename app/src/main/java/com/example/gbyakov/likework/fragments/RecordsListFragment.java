package com.example.gbyakov.likework.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gbyakov.likework.R;

import java.util.ArrayList;
import java.util.List;

public class RecordsListFragment extends Fragment {

    private static final List<Record> records = new ArrayList<Record>();
    public static ListView list;

    static {
        records.add(new Record("08:00", "HIGHLANDER - Е 287 ТА 159", "Цыбина Елена Владимировна", "Диагностика"));
        records.add(new Record("09:00", "COROLLA - Е 571 ЕО 159", "Третьяков Алексей Юрьевич", "ТО - 20 000 км"));
        records.add(new Record("10:00", "HILUX - Е 347 РН 159", "Сулейманов Сахават Абасат Оглы", "ТО - 30 000 км"));
        records.add(new Record("11:00", "", "", ""));
        records.add(new Record("12:00", "RAV4 - В 210 СА 159", "Тюрин Владимир Анатольевич", "ТО - 1 мес."));
        records.add(new Record("13:00", "LC 200 - Е 610 ЕТ 159", "Харченко Александр Владимирович", "Диагностика"));
        records.add(new Record("14:00", "CAMRY - Т 223 КА 159", "Смирнов Юрий Владимирович", "МАСЛО МОТОРНОЕ И ФИЛЬТР - ЗАМЕНА"));
        records.add(new Record("15:00", "RAV4 - А 677 ОХ 159", "Глонина Ольга Леонидовна", "ТО - 80 000 км."));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View x = inflater.inflate(R.layout.fragment_records_list, null);
        list = (ListView) x.findViewById(R.id.records_listview);

        ArrayAdapter<Record> adapter = new RecordAdapter(getContext());
        list.setAdapter(adapter);

        return x;
    }

    private static class Record {
        public final String time;
        public final String car;
        public final String client;
        public final String details;

        public Record(String time, String car, String client, String details) {
            this.time   = time;
            this.car    = car;
            this.client = client;
            this.details= details;
        }
    }

    private class RecordAdapter extends ArrayAdapter<Record> {

        public RecordAdapter(Context context) {
            super(context, R.layout.list_item_records, records);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Record Record = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item_records, null);
            }
            ((TextView) convertView.findViewById(R.id.list_item_time))
                    .setText(Record.time);
            ((TextView) convertView.findViewById(R.id.list_item_car))
                    .setText(Record.car);
            ((TextView) convertView.findViewById(R.id.list_item_client))
                    .setText(Record.client);
            ((TextView) convertView.findViewById(R.id.list_item_details))
                    .setText(Record.details);
            return convertView;
        }
    }
}