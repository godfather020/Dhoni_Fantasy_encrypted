package com.app.dharaneesh;


import android.app.ActivityManager;
        import android.app.AlertDialog;
        import android.app.DatePickerDialog;
        import android.app.ProgressDialog;
        import android.app.TimePickerDialog;
        import android.content.ContentResolver;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.View;
        import android.webkit.MimeTypeMap;
        import android.widget.Button;
        import android.widget.CompoundButton;
        import android.widget.DatePicker;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.TimePicker;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AppCompatActivity;

        import com.app.dharaneesh.futureUploadService.MyService;
import com.app.dharaneesh.util.Constant;
import com.app.dharaneesh.util.postnotification;
        import com.google.android.gms.tasks.Continuation;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.StorageTask;
        import com.google.firebase.storage.UploadTask;

        import org.json.JSONObject;

        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.OutputStreamWriter;
        import java.net.HttpURLConnection;
        import java.net.URL;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {
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
    StorageTask uploadTask;
    String scheduleTime;
    String postDateTime = "null";
    Uri profile_uri;
    //    TextView tvSchedulePost;
    private static final int imgReq = 1;
    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;

    int mHour;
    int mMinute;
    String scheduleDateAndTime, child, st;
    String dateAndTime;

    //
    TextView vtime;
    LinearLayout vschudle;
    String title;
    Switch uploadswitch;
    AlertDialog.Builder dialog;
    Button simage;
    AlertDialog alertDialog = null;
    boolean schedule = false;
    private postnotification.Data data;
    long t = 0;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        btnChooseImage = findViewById(R.id.btnChooseImg);
        btnUpdate = findViewById(R.id.btnUploadImage);
        imgUpdate = findViewById(R.id.imgUpdateImage);
        uploadswitch = findViewById(R.id.uploadswitch);
        simage = findViewById(R.id.simage);
        dialog = new AlertDialog.Builder(UpdateActivity.this);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        vtime = findViewById(R.id.vtime);
        vschudle = findViewById(R.id.vschudle);
//        tvSchedulePost = findViewById(R.id.tvSchedulePost);

        userId = getIntent().getStringExtra("nam");
        child = getIntent().getStringExtra("name");
        title = getIntent().getStringExtra("title");
        scheduleTime = getIntent().getStringExtra("time");



        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        vtime.setText(currentDateandTime);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        btnChooseImage.setOnClickListener(v -> {
            openImage();
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
                            new DatePickerDialog(UpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    date.set(year, monthOfYear, dayOfMonth);
                                    new TimePickerDialog(UpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            date.set(Calendar.MINUTE, minute);
                                            date.clear(Calendar.SECOND);

                                            //Log.d("time", dateAndTime);
                                            t = date.getTimeInMillis();
                                            st = String.valueOf(date.getTimeInMillis());



//                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:MM:ss");
                                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");// HH:MM:ss");
                                            SimpleDateFormat f = new SimpleDateFormat("hh:mm aa");
                                            String dateString = formatter.format(new Date(date.getTimeInMillis()));
                                            String t = f.format(date.getTimeInMillis());
                                            dateAndTime = String.valueOf(date.getTimeInMillis());
                                            Log.v("TAG", "The choosen one1 "+t+"  "+dateString);
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
//            if (scheduleDateAndTime != null) {
//                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
//                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
//                String currentDateandTime = sdf.format(new Date());
//
//                try {
//                    Date date1 = df.parse(scheduleDateAndTime);
//                    Date date2 = df.parse(currentDateandTime);
//
//                    long diff = date1.getTime() - date2.getTime();
//                    long seconds = diff / 1000;
//                    long minutes = seconds / 60;
//                    long hours = minutes / 60;
//                    long days = hours / 24;
//
//
//                    if (!isMyServiceRunning()){
//                        Intent serviceIntent = new Intent(this, MyService.class);
//                        startService(serviceIntent);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
            uploadProfile();
        });

//        tvSchedulePost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                datePicker();
//            }
//        });

    }

    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d", h,m);
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] per, @NonNull int[] gra) {
        super.onRequestPermissionsResult(req, per, gra);
        if (req == camerCod) {
            if (gra[0] == PackageManager.PERMISSION_GRANTED) {
                openImage();
            } else {
                Toast.makeText(getApplicationContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);
        Format formatter;
        formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(tme);
    }


    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void openImage() {
        Intent imgIntent = new Intent();
        imgIntent.setType("image/*");
        imgIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imgIntent, imgReq);
    }

    private String getFileExte(Uri uri) {
        ContentResolver cont = getApplicationContext().getContentResolver();
        MimeTypeMap min = MimeTypeMap.getSingleton();
        return min.getExtensionFromMimeType(cont.getType(uri));
    }

    private void uploadProfile() {
        pD = new ProgressDialog(UpdateActivity.this);
        pD.setMessage("Please wait...");
        pD.setIndeterminate(true);
        pD.setCancelable(false);
        pD.show();
        if (profile_uri != null) {
            final StorageReference fileRefernce = storageReference.child(System.currentTimeMillis() + "." + getFileExte(profile_uri));
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), profile_uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            uploadTask = fileRefernce.putBytes(data);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(Task<UploadTask.TaskSnapshot> tas) throws Exception {
                    if (!tas.isSuccessful()) {
                        throw tas.getException();
                    }
                    return fileRefernce.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> tas) {
                    if (tas.isSuccessful()) {
                        Uri downloadUri = tas.getResult();
                        String mUri = downloadUri.toString();
                        if(schedule){
                            reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("schedule").child(st);
                            reference1 = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Match").child(userId);
                            reference1.child("isScheduled").setValue("true");
                            reference1.child("scheduledTime").setValue(dateAndTime);
                            reference1.child("postimg").setValue(mUri);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("postimg", mUri);
                            map.put("keys", userId);
                            reference.updateChildren(map);
                            sendnotification(title, "", st);
                        }else {
                            reference1 = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Match").child(userId);
                            //reference1 = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Match").child(userId);
                            reference1.child("isScheduled").setValue("false");
                            reference1.child("scheduledTime").setValue(String.valueOf(System.currentTimeMillis()));
                            reference1.child("postimg").setValue(mUri);
                            /*HashMap<String, Object> map = new HashMap<>();
                            map.put("postimg", mUri);
                            map.put("keys", userId);
                            reference1.updateChildren(map);*/
                            new Notify().execute();
                        }
                        pD.dismiss();
                        showTo("Photo Uploaded successfully");
                        finish();

                    } else {
                        showTo("Failed");
                        pD.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showTo("Failed");
                    pD.dismiss();
                }
            });
        } else {
            pD.dismiss();
            showTo("No Image Selected");
        }
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

    @Override
    protected void onActivityResult(int reqCode, int result, Intent da) {
        super.onActivityResult(reqCode, result, da);
        if (reqCode == imgReq && result == RESULT_OK && da != null && da.getData() != null) {
            profile_uri = da.getData();
            imgUpdate.setImageURI(null);
            imgUpdate.setImageURI(da.getData());
        }
    }

    public void showTo(String mess) {
        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_LONG).show();
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

    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        //*************Call Time Picker Here ********************
                        tiemPicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;

                        scheduleDateAndTime = date_time + " " + hourOfDay + ":" + minute;

//                        tvSchedulePost.setText("Schedule this post for: " + date_time + " " + hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

}

