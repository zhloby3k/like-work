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

public class OrdersListFragment extends Fragment {

    private static final List<Order> orders = new ArrayList<Order>();
    public static ListView list;

    static {
        orders.add(new Order("08.12.15", "HIGHLANDER - Е 287 ТА 159", "Цыбина Елена Владимировна", "Диагностика", "64 000,00"));
        orders.add(new Order("18.12.15", "COROLLA - Е 571 ЕО 159", "Третьяков Алексей Юрьевич", "ТО - 20 000 км", "13 504,00"));
        orders.add(new Order("04.12.15", "HILUX - Е 347 РН 159", "Сулейманов Сахават Абасат Оглы", "ТО - 30 000 км", "107 776,23"));
        orders.add(new Order("09.12.15", "RAV4 - В 210 СА 159", "Тюрин Владимир Анатольевич", "ТО - 1 мес.", "81 709,84"));
        orders.add(new Order("11.11.15", "LC 200 - Е 610 ЕТ 159", "Харченко Александр Владимирович", "Диагностика", "126 936,00"));
        orders.add(new Order("28.10.15", "CAMRY - Т 223 КА 159", "Смирнов Юрий Владимирович", "МАСЛО МОТОРНОЕ И ФИЛЬТР - ЗАМЕНА", "15 830,00"));
        orders.add(new Order("02.11.15", "RAV4 - А 677 ОХ 159", "Глонина Ольга Леонидовна", "ТО - 80 000 км.", "81 709,84"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View x = inflater.inflate(R.layout.fragment_records_list, null);
        list = (ListView) x.findViewById(R.id.records_listview);

        ArrayAdapter<Order> adapter = new OrderAdapter(getContext());
        list.setAdapter(adapter);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Реестр заказ-нарядов");

        return x;
    }

    private static class Order {
        public final String date;
        public final String car;
        public final String client;
        public final String details;
        public final String sum;

        public Order(String date, String car, String client, String details, String sum) {
            this.date   = date;
            this.car    = car;
            this.client = client;
            this.details= details;
            this.sum    = sum;

        }
    }

    private class OrderAdapter extends ArrayAdapter<Order> {

        public OrderAdapter(Context context) {
            super(context, R.layout.list_item_orders, orders);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Order Order = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item_orders, null);
            }
            ((TextView) convertView.findViewById(R.id.list_item_order_date))
                    .setText(Order.date);
            ((TextView) convertView.findViewById(R.id.list_item_order_car))
                    .setText(Order.car);
            ((TextView) convertView.findViewById(R.id.list_item_order_client))
                    .setText(Order.client);
            ((TextView) convertView.findViewById(R.id.list_item_order_details))
                    .setText(Order.details);
            ((TextView) convertView.findViewById(R.id.list_item_order_sum))
                    .setText(Order.sum);
            return convertView;
        }
    }

}