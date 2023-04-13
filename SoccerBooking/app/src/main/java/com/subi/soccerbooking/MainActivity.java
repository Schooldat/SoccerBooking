package com.subi.soccerbooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.subi.soccerbooking.adapter.ListFieldAdapter;
import com.subi.soccerbooking.adapter.ShowDialog;
import com.subi.soccerbooking.database.FieldDao;
import com.subi.soccerbooking.model.Field;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Lấy email đã lưu
    private SharedPreferences pref;
    public static String email;
    private String TAG = "MainActivityLOG";
    private FloatingActionButton flAdd;
    private String[] cameraPermission;
    private String[] storagePermission;
    private String image_uri = "";
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private ImageView imageView;
    private ShowDialog showDialog;
    private TextView image_picked;
    private CardView cardImage;
    private FieldDao fieldDao;
    private RecyclerView recyclerView;
    private ListFieldAdapter adapter;
    private ArrayList<Field> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Cài đặt tiêu đề
        setTitle("Danh sách sân");
        //Khởi tạo các id
        init();

        //Lấy toàn bộ list sân theo email của chủ sân
        list.clear();
        list.addAll(fieldDao.getAllByEmail(email));

        //Set adapter danh sách sân hiện có
        LinearLayoutManager layoutManager
                = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        //Set list vào adapter
        adapter = new ListFieldAdapter(this, list);
        recyclerView.setAdapter(adapter);
        //Set sự kiện click
        adapter.setItemClickListener(new ListFieldAdapter.ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Field field = list.get(position);
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        MainActivity.this, R.style.BottomSheetDialogTheme
                );

                View bottomSheetView = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.bottom_sheet_dialog,
                        (LinearLayout) bottomSheetDialog.findViewById(R.id.bottomSheetContainer)
                );

                //Khi chọn xem, chuyển qua màn hình booking
                bottomSheetView.findViewById(R.id.tv_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, BookingActivity.class);
                        intent.putExtra("field", field);
                        MainActivity.this.startActivity(intent);
                    }
                });

                //Chỉnh sửa sân
                bottomSheetView.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.edit_field);
                        dialog.setCancelable(true);
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if (dialog != null && dialog.getWindow() != null) {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                        //Khai báo biến
                        TextInputEditText name = dialog.findViewById(R.id.edt_name);
                        TextInputEditText price = dialog.findViewById(R.id.edt_price);
                        image_picked = dialog.findViewById(R.id.tv_pick_image);
                        imageView = dialog.findViewById(R.id.iv_image);
                        cardImage = dialog.findViewById(R.id.cv_image);
                        Button add = dialog.findViewById(R.id.btnThem);
                        Button cancle = dialog.findViewById(R.id.btnHuy);
                        if (!field.getImage().isEmpty()) {
                            cardImage.setVisibility(View.VISIBLE);
                            image_picked.setVisibility(View.GONE);
                        } else {
                            cardImage.setVisibility(View.GONE);
                            image_picked.setVisibility(View.VISIBLE);
                        }

                        //Set text
                        name.setText(field.getName());
                        price.setText(field.getPrice());
                        Glide.with(MainActivity.this).load(field.getImage()).into(imageView);
                        //Chọn ảnh
                        image_picked.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showImagePickDialog();
                            }
                        });

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showImagePickDialog();
                            }
                        });

                        //Khi ấn thêm
                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String image = field.getImage();
                                String nameText = name.getText().toString();
                                String priceText = price.getText().toString();
                                if (nameText.isEmpty() || priceText.isEmpty()) {
                                    showDialog.show("Không được để trống!");
                                } else {
                                    Field fieldx = new Field(field.getId(), nameText, priceText, image_uri, MainActivity.email);
                                    if (fieldDao.sua(fieldx)) {
                                        dialog.dismiss();
                                        showDialog.show("Sửa sân thành công!");
                                        bottomSheetDialog.dismiss();
                                        list.clear();
                                        list.addAll(fieldDao.getAllByEmail(MainActivity.email));
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        showDialog.show("Sửa sân thất bại!");
                                        bottomSheetDialog.dismiss();
                                    }
                                }
                            }
                        });

                        //Khi ấn nút hủy
                        cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                bottomSheetView.findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage("Bạn có chắc chắn xóa " + field.getName() + " ?")
                                .setCancelable(false)
                                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (fieldDao.xoa(field)) {
                                            showDialog.show("Xóa thành công!");
                                            list.clear();
                                            list.addAll(fieldDao.getAllByEmail(MainActivity.email));
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.getWindow().getAttributes().windowAnimations = R.style.up_down;
                        alertDialog.show();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
        //Khi ấn nút thêm
        flAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_field);
                dialog.setCancelable(true);
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (dialog != null && dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                //Khai báo biến
                TextInputEditText name = dialog.findViewById(R.id.edt_name);
                TextInputEditText price = dialog.findViewById(R.id.edt_price);
                image_picked = dialog.findViewById(R.id.tv_pick_image);
                imageView = dialog.findViewById(R.id.iv_image);
                cardImage = dialog.findViewById(R.id.cv_image);
                Button add = dialog.findViewById(R.id.btnThem);
                Button cancle = dialog.findViewById(R.id.btnHuy);

                //Chọn ảnh
                image_picked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImagePickDialog();
                    }
                });

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImagePickDialog();
                    }
                });

                //Khi ấn thêm
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nameText = name.getText().toString();
                        String priceText = price.getText().toString();
                        if (nameText.isEmpty() || priceText.isEmpty()) {
                            showDialog.show("Không được để trống!");
                        } else {
                            Field field = new Field(0, nameText, priceText, image_uri, email);
                            Log.d(TAG, "Add: " + field.toString());
                            if (fieldDao.them(field)) {
                                dialog.dismiss();
                                showDialog.show("Thêm sân thành công!");
                                list.clear();
                                list.addAll(fieldDao.getAllByEmail(email));
                                adapter.notifyDataSetChanged();
                            } else {
                                showDialog.show("Thêm sân thất bại!");
                            }
                        }
                    }
                });

                //Khi ấn nút hủy
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    private void init() {
        pref = getSharedPreferences("account", MODE_PRIVATE);
        email = pref.getString("tk", "");
        flAdd = findViewById(R.id.fl_add);
        showDialog = new ShowDialog(this);
        Log.d(TAG, "email: " + email);
        //Khai báo xin quyền
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        fieldDao = new FieldDao(this);
        recyclerView = findViewById(R.id.rc_booking);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        menu.findItem(R.id.logout).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                //xóa hết dữ liệu đã lưu tk, mk
                SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.history:
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).toString();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    public void showImagePickDialog() {
        String option[] = {"Camera", "Thư viện ảnh", "Xóa ảnh"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Mời bạn chọn");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                }
                if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragetPermission();
                    } else {
                        pickFromGallery();
                    }
                }
                //Xóa ảnh
                if (which == 2) {
                    image_uri = "";
                    cardImage.setVisibility(View.GONE);
                    image_picked.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.create().show();
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragetPermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccept && storageAccept) {
                        pickFromCamera();
                    } else {
                        showDialog.show("Không truy cập được vào camera!");
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccpted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccpted) {
                        pickFromGallery();
                    } else {
                        showDialog.show("Vui lòng bật quyền thư viện");
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData().toString();
                Glide.with(this).load(Uri.parse(image_uri)).into(imageView);
                cardImage.setVisibility(View.VISIBLE);
                Log.d(TAG, image_uri.toString());
                image_picked.setVisibility(View.GONE);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                Glide.with(this).load(Uri.parse(image_uri)).into(imageView);
                cardImage.setVisibility(View.VISIBLE);
                image_picked.setVisibility(View.GONE);
                Log.d(TAG, image_uri.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

    }
}