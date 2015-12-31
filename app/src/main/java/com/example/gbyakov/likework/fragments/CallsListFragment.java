package com.example.gbyakov.likework.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gbyakov.likework.R;

import java.util.ArrayList;
import java.util.List;

public class CallsListFragment extends Fragment {

    private static final List<Call> calls = new ArrayList<Call>();
    public static ListView list;

    static {
        calls.add(new Call("08.12.15", "HIGHLANDER - Е 287 ТА 159", "Цыбина Елена Владимировна", "бампер передний"));
        calls.add(new Call("18.12.15", "COROLLA - Е 571 ЕО 159", "Третьяков Алексей Юрьевич", "работы по установке подкрылка"));
        calls.add(new Call("04.12.15", "HILUX - Е 347 РН 159", "Сулейманов Сахават Абасат Оглы", "ДЕФЕКТВОКА С ЭКСПЕРТОМ"));
        calls.add(new Call("09.12.15", "RAV4 - В 210 СА 159", "Тюрин Владимир Анатольевич", "полная мойка"));
        calls.add(new Call("11.11.15", "LC 200 - Е 610 ЕТ 159", "Харченко Александр Владимирович", "работы по установке подкрылка"));
        calls.add(new Call("28.10.15", "CAMRY - Т 223 КА 159", "Смирнов Юрий Владимирович", "полная мойка полирвока 4 дверей"));
        calls.add(new Call("02.11.15", "RAV4 - А 677 ОХ 159", "Глонина Ольга Леонидовна", "протект 4 деталей"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View x = inflater.inflate(R.layout.fragment_calls_list, null);
        list = (ListView) x.findViewById(R.id.calls_listview);

        ArrayAdapter<Call> adapter = new CallAdapter(getContext());
        list.setAdapter(adapter);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Звонки заботы");

        return x;
    }

    private static class Call {
        public final String date;
        public final String car;
        public final String client;
        public final String detail;

        public Call(String date, String car, String client, String detail) {
            this.date = date;
            this.car = car;
            this.client = client;
            this.detail = detail;
        }
    }

    private class CallAdapter extends ArrayAdapter<Call> {

        public CallAdapter(Context context) {
            super(context, R.layout.list_item_calls, calls);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Call Call = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item_calls, null);
            }

            ((TextView) convertView.findViewById(R.id.list_item_call_date))
                    .setText(Call.date);
            ((TextView) convertView.findViewById(R.id.list_item_call_car))
                    .setText(Call.car);
            ((TextView) convertView.findViewById(R.id.list_item_call_client))
                    .setText(Call.client);
            ((TextView) convertView.findViewById(R.id.list_item_call_detail))
                    .setText(Call.detail);

            return convertView;
        }
    }

}