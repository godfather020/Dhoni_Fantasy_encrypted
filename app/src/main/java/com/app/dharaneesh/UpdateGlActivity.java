package com.app.dharaneesh;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.dharaneesh.util.Constant;
import com.app.dharaneesh.util.postnotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateGlActivity extends AppCompatActivity {
    ImageView imgUpdate;
    Button btnUpdate, btnChooseImage;
    StorageReference storageReference;
    static final int camReq = 1888;
    static final int camerCod = 101;
    String userId = "";
    ProgressDialog pD;
    DatabaseReference reference;
    DatabaseReference reference1;
    FirebaseUser fuser;
    String dateAndTime;
    StorageTask uploadTask;
    Uri profile_uri;
    private static final int imgReq = 1;
    private static final int READ_PERMISSION_CODE = 1;
    private static final int PICK_FILE_REQUEST = 2;
    List<ImagesModel> imagesList;
    ClipData clipData;
    int i;
    LinearLayout llSelectData;
    ImagesAdapter adapter;
    FirebaseStorage storage;
    int counter;
    List<String> savedImagesUri;
    RecyclerView recyclerViewSelectedImage;

    //
    TextView vtime;
    LinearLayout vschudle;
    Switch uploadswitch;
    AlertDialog.Builder dialog;
    Button simage;
    String st, title;
    AlertDialog alertDialog = null;
    boolean schedule = false;
    private postnotification.Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_gl);
        imagesList = new ArrayList<>();
        savedImagesUri = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        btnChooseImage = findViewById(R.id.btnChooseImg);
        btnUpdate = findViewById(R.id.btnUploadImage);
        imgUpdate = findViewById(R.id.imgUpdateImage);
        llSelectData = findViewById(R.id.llSelectData);
        recyclerViewSelectedImage = (RecyclerView) findViewById(R.id.selectedImageRecyclerView);
        uploadswitch = findViewById(R.id.uploadswitch);
        simage = findViewById(R.id.simage);
        dialog = new AlertDialog.Builder(UpdateGlActivity.this);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        vtime = findViewById(R.id.vtime);
        vschudle = findViewById(R.id.vschudle);


        adapter = new ImagesAdapter(this, imagesList);
        recyclerViewSelectedImage.setHasFixedSize(true);
        recyclerViewSelectedImage.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerViewSelectedImage.setAdapter(adapter);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        vtime.setText(currentDateandTime);

        userId = getIntent().getStringExtra("nam");
        title = getIntent().getStringExtra("title");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        btnChooseImage.setOnClickListener(v -> {
            verifyPermissionAndPickFile();
        });
        uploadswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    schedule = true;

                    dialog.setMessage("Schedule post");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            simage.setVisibility(View.GONE);
                            vschudle.setVisibility(View.VISIBLE);
                            Calendar date;
                            final Calendar currentDate = Calendar.getInstance();
                            date = Calendar.getInstance();
                            new DatePickerDialog(UpdateGlActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    date.set(year, monthOfYear, dayOfMonth);
                                    new TimePickerDialog(UpdateGlActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            date.set(Calendar.MINUTE, minute);
                                            date.clear(Calendar.SECOND);
                                            st = String.valueOf(date.getTimeInMillis());


//                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:MM:ss");
                                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");// HH:MM:ss");
                                            SimpleDateFormat f = new SimpleDateFormat("hh:mm aa");
                                            String dateString = formatter.format(new Date(date.getTimeInMillis()));
                                            String time = getTime(hourOfDay, minute);
                                            String t = f.format(date.getTimeInMillis());
                                            Log.d("time", time);
                                            dateAndTime = String.valueOf(date.getTimeInMillis());
                                            Log.v("TAG", "The choosen one1 "+"  "+dateString);
                                            vtime.setText(dateString+" "+t);
//                                            Date convertedDate = new Date();
//                                            try {
//                                                convertedDate = dateFormat.parse(String.valueOf(date.getTime()));
//                                                stime = String.valueOf(convertedDate);
//
//
//                                            } catch (ParseException e) {
//                                                e.printStackTrace();
//                                            }


//                                    Log.v("TAG", "The choosen one "+t+"  "+convertedDate+"   0" + String.valueOf(date.getTime()));
                                        }
                                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
                                }
                            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();


                        }
                    });
                    dialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            simage.setVisibility(View.GONE);
                            vschudle.setVisibility(View.GONE);
                            alertDialog.dismiss();
                            uploadswitch.setChecked(false);
                            schedule = false;
                        }
                    });

                    alertDialog = dialog.create();
                    alertDialog.show();



                } else {
                    schedule = false;
                    st = "";

                }

            }
        });

        btnUpdate.setOnClickListener(v -> {
            uploadHomework();
        });
    }

    private void uploadHomework() {
        final KProgressHUD progressDialog = KProgressHUD.create(UpdateGlActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Uploaded 0/" + imagesList.size())
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        if (imagesList.size() != 0) {
            final StorageReference storageReference = storage.getReference();
            for (int i = 0; i < imagesList.size(); i++) {
                final int finalI = i;
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imagesList.get(i).getImageUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data = baos.toByteArray();
                storageReference.child("GlMatch/").child(imagesList.get(i).getImageName()).putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            storageReference.child("GlMatch/").child(imagesList.get(finalI).getImageName()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    counter++;
                                    progressDialog.setLabel("Uploaded " + counter + "/" + imagesList.size());
                                    if (task.isSuccessful()) {
                                        savedImagesUri.add(task.getResult().toString());
                                    } else {
                                        storageReference.child("GlMatch/").child(imagesList.get(finalI).getImageName()).delete();
                                        Toast.makeText(UpdateGlActivity.this, "Couldn't save " + imagesList.get(finalI).getImageName(), Toast.LENGTH_SHORT).show();
                                        counter = 0;
                                        savedImagesUri = null;
                                        imagesList.clear();
                                        progressDialog.dismiss();
                                    }
                                    if (counter == imagesList.size()) {
                                        saveImageDataToDatabase(progressDialog);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(UpdateGlActivity.this, "Couldn't upload " + imagesList.get(finalI).getImageName(), Toast.LENGTH_SHORT).show();
                            counter = 0;
                            savedImagesUri = null;
                            imagesList.clear();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageDataToDatabase(final KProgressHUD progressDialog) {
        progressDialog.setLabel("Saving uploaded images...");
        Map<String, String> dataMap = new HashMap<>();

        for (int i = 0; i < savedImagesUri.size(); i++) {
            dataMap.put("image" + i, savedImagesUri.get(i));

        }

        String attachmenteString = dataMap.values().toString().replace("[", "").replace("]", "");
        Log.d("IMAGE_LINK", attachmenteString);

        if(schedule){
            reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("schedule").child(st);
            reference1 = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Match").child(userId);
            reference1.child("isScheduled").setValue("true");
            reference1.child("scheduledTime").setValue(dateAndTime);
            reference1.child("postimg").setValue(attachmenteString);
            HashMap<String, Object> map = new HashMap<>();
            map.put("postimg", attachmenteString);
            map.put("keys", userId);
            reference.updateChildren(map);
            sendnotification(title, "", st);
        }else {
            reference1 = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Match").child(userId);
            //reference1 = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Match").child(userId);
            reference1.child("isScheduled").setValue("false");
            reference1.child("scheduledTime").setValue(String.valueOf(System.currentTimeMillis()));
            reference1.child("postimg").setValue(attachmenteString);
                            /*HashMap<String, Object> map = new HashMap<>();
                            map.put("postimg", mUri);
                            map.put("keys", userId);
                            reference1.updateChildren(map);*/
            new Notify().execute();
        }

        /*if(schedule){
            reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("schedule").child(st);
            sendnotification(title, "", st);
        }else {
            reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Match").child(userId);
            new Notify().execute();
        }

        reference1 = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Match").child(userId);
        reference1.child("isScheduled").setValue("true");
        reference1.child("scheduledTime").setValue(dateAndTime);
        reference1.child("postimg").setValue(attachmenteString);
       *//* HashMap<String, Object> map = new HashMap<>();
        map.put("postimg", attachmenteString);
        map.put("keys", userId);*//*
        //reference.updateChildren(map);*/

        llSelectData.setVisibility(View.GONE);
        attachmenteString.equals(null);
        counter = 0;
        imagesList.clear();
        savedImagesUri.clear();
        progressDialog.dismiss();
        finish();
    }

    private void sendnotification(String title, String body, String time) {
        Log.d("tag", "checktimegl"+time);

        data = new postnotification.Data(
                "userId_sender",
                title,
                body,
                "true",
                true,
                "2022-05-23 02:28:00",
                time,
                postnotification.Data.TYPE_MESSAGE);
//                    }
        postnotification.sendNotification("", data);
    }

    private void verifyPermissionAndPickFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(UpdateGlActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickFile();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
            }
        } else {
            pickFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFile();
                } else {
                    Toast.makeText(UpdateGlActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void pickFile() {
        imagesList.clear();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FILE_REQUEST:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    clipData = data.getClipData();

                    if (clipData != null) {
                        for (i = 0; i < clipData.getItemCount(); i++) {
                            llSelectData.setVisibility(View.VISIBLE);
                            Uri uri = clipData.getItemAt(i).getUri();
                            imagesList.add(new ImagesModel(getFileNameFromUri(uri), uri));
                            adapter.notifyDataSetChanged();
                            Log.d("IMAGE_LOG", "On activity result when image != null");
                        }
                    } else {
                        llSelectData.setVisibility(View.GONE);
                        Uri uri = data.getData();
                        imagesList.add(new ImagesModel(getFileNameFromUri(uri), uri));
                        adapter.notifyDataSetChanged();
                        Log.d("IMAGE_LOG", "On activity image when image == null");
                    }
                }
                break;
        }
    }

    public class Notify extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=AAAAC26x-OU:APA91bE6Bz5icB_nBV9djeawKnAruIeS9KQ8Qv9wFjMmzR1OlMh9b1LIqtUi83GjvdwKAtUj_bsj8G9uaO_M_dm_rKrD-4Bs39kplNBb1TU0BicvmvUgft3xNc3ZEO86possWwGKD06g");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();

                json.put("to", "/topics/allDevices");

                JSONObject info = new JSONObject();
                info.put("title", "New photo added in match.");   // Notification title
                info.put("text", ""); // Notification body

                json.put("notification", info);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();

            } catch (Exception e) {
                Log.d("Error", "" + e);
            }
            return null;
        }
    }

    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);
        Format formatter;
        formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(tme);
    }
}