package com.subi.soccerbooking.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.subi.soccerbooking.R;
import com.subi.soccerbooking.database.HistoryDao;
import com.subi.soccerbooking.model.History;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<History> list;
    private DecimalFormat fm = new DecimalFormat("#,###");

    public HistoryAdapter() {
    }

    public HistoryAdapter(Context context, ArrayList<History> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Set layout cho adapter để hiển thị lên list
        View view = LayoutInflater.from(context).inflate(R.layout.one_history, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        //Trả về 1 item tại vị trí position(vị trí hiện tại theo list)
        History history = list.get(position);
        holder.tencot1.setText(history.getNameuser());
        holder.tencot2.setText(history.getPhone());
        holder.tencot3.setText(history.getDate());
        holder.tencot4.setText(history.getName());
        holder.tencot5.setText(fm.format(Integer.parseInt(history.getPrice())) + " VNĐ");
        holder.tencot6.setText(history.getHour()+" giờ");
        holder.tencot7.setText(fm.format(Integer.parseInt(history.getPrice()) * Integer.parseInt(history.getHour())) + " VNĐ");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tencot1, tencot2, tencot3, tencot4, tencot5, tencot6, tencot7;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Khai báo id theo itemView
            tencot1 = itemView.findViewById(R.id.tvTenCot1);
            tencot2 = itemView.findViewById(R.id.tvTenCot2);
            tencot3 = itemView.findViewById(R.id.tvTenCot3);
            tencot4 = itemView.findViewById(R.id.tvTenCot4);
            tencot5 = itemView.findViewById(R.id.tvTenCot5);
            tencot6 = itemView.findViewById(R.id.tvTenCot6);
            tencot7 = itemView.findViewById(R.id.tvTenCot7);
            view = itemView;
        }
    }
}
