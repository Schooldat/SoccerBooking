package com.subi.soccerbooking.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.subi.soccerbooking.BookingActivity;
import com.subi.soccerbooking.MainActivity;
import com.subi.soccerbooking.R;
import com.subi.soccerbooking.database.FieldDao;
import com.subi.soccerbooking.model.Field;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListFieldAdapter extends RecyclerView.Adapter<ListFieldAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Field> list;
    private DecimalFormat fm = new DecimalFormat("#,###");
    private FieldDao fieldDao;
    private ShowDialog showDialog;
    private ImageView imageView;
    private TextView image_picked;
    private CardView cardImage;
    private String[] cameraPermission;
    private String[] storagePermission;
    private String image_uri = "";
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private ItemClickListener itemClickListener;

    public ListFieldAdapter() {
    }

    public ListFieldAdapter(Context context, ArrayList<Field> list) {
        this.context = context;
        this.list = list;
        fieldDao = new FieldDao(context);
        showDialog = new ShowDialog((Activity) context);
    }

    @NonNull
    @Override
    public ListFieldAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Set layout cho adapter để hiển thị lên list
        View view = LayoutInflater.from(context).inflate(R.layout.one_field, parent, false);
        return new ViewHolder(view);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ListFieldAdapter.ViewHolder holder, int position) {
        //Trả về 1 item tại vị trí position(vị trí hiện tại theo list)
        Field field = list.get(position);
        //Set tiêu đề
        holder.name.setText(field.getName());
        try {
            holder.price.setText(fm.format(Double.parseDouble(field.getPrice())) + " VND");
        }catch (Exception ex)
        {
            Toast.makeText(context, "Kiểm tra lại số tiền có bị ảo k", Toast.LENGTH_SHORT).show();
        }


        //Kiểm tra ảnh trống hay có ảnh
        if (field.getImage().isEmpty()) {
            holder.imageView.setImageResource(R.drawable.field);
        } else {
            Glide.with(context).load(field.getImage()).into(holder.imageView);
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView imageView;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Khai báo id theo itemView
            name = itemView.findViewById(R.id.tv_name_field);
            price = itemView.findViewById(R.id.tv_price_field);
            imageView = itemView.findViewById(R.id.iv_image_field);
            view = itemView;
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }
}
