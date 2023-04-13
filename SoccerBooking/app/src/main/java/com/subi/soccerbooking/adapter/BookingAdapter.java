package com.subi.soccerbooking.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.subi.soccerbooking.R;
import com.subi.soccerbooking.model.Booking;
import com.subi.soccerbooking.model.Field;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BookingAdapter extends BaseAdapter {
    Context context;
    ArrayList<Booking> list;
    private Field field;
    DecimalFormat fm = new DecimalFormat("#,###");

    public BookingAdapter(Context context, ArrayList<Booking> list, Field field) {
        this.context = context;
        this.list = list;
        this.field = field;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        final Booking x = list.get(i);
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.one_booking, null);
            holder.tencot1 = view.findViewById(R.id.tvTenCot1);
            holder.tencot2 = view.findViewById(R.id.tvTenCot2);
            holder.tencot3 = view.findViewById(R.id.tvTenCot3);
            holder.tencot4 = view.findViewById(R.id.tvTenCot4);
            holder.tencot5 = view.findViewById(R.id.tvTenCot5);
            holder.tencot6 = view.findViewById(R.id.tvTenCot6);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tencot1.setText(x.getName());
        holder.tencot2.setText(x.getPhone());
        holder.tencot3.setText(x.getDatetime());
        holder.tencot4.setText(field.getName());
        holder.tencot5.setText(fm.format(Integer.parseInt(field.getPrice())) + " VND");
        holder.tencot6.setText(x.getNote());
        return view;
    }

    class ViewHolder {
        TextView tencot1, tencot2, tencot3, tencot4, tencot5, tencot6;
    }
}
