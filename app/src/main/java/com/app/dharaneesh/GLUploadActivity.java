package com.app.dharaneesh;


import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AppCompatActivity;

        import android.app.AlertDialog;
        import android.app.DatePickerDialog;
        import android.app.ProgressDialog;
        import android.app.TimePickerDialog;
        import android.content.ContentResolver;
        import android.content.DialogInterface;
        import android.content.Intent;
import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Bundle;
import android.util.Log;
        import android.view.View;
        import android.webkit.MimeTypeMap;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.CompoundButton;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.Spinner;
        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.TimePicker;
        import android.widget.Toast;

import com.app.dharaneesh.util.Constant;
import com.app.dharaneesh.util.postnotification;
import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.StorageTask;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.sql.Time;
        import java.text.Format;
import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;

public class GLUploadActivity extends AppCompatActivity {
    Button btnSetDate, btnSetTime, btnUploadPost;
    TextView txtDate, txtTime;
    boolean schedule = false;
    String st, stime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText txtPostTitle, txtPostDetails;
    DatabaseReference reference;
    private postnotification.Data data;
    private FirebaseAuth mAuth;
    FirebaseUser fuser;
    Switch uploadswitch;
    AlertDialog alertDialog = null;
    int t1hour, t1minutes;
    AlertDialog.Builder dialog;
    Button simage;
    Spinner spTypeOfMatch, spTeamType;
    String[] matchType = {"Cricket", "Football", "Basketball", "Kabaddi", "Baseball", "Volleyball", "Hockey"};
    String[] teamType = {"Prime", "Open"};
    String t = "0";


    private static final int imgReq = 1;
    Uri profile_uri;
    ImageView imgUpdate;
    ProgressDialog pD;
    StorageReference storageReference;
    StorageTask uploadTask;
    TextView vtime;
    LinearLayout vschudle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glupload);

        btnSetDate = findViewById(R.id.btnSetDate);
        btnSetTime = findViewById(R.id.btnSetTime);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        btnUploadPost = findViewById(R.id.btnUpload);
        txtPostDetails = findViewById(R.id.txtPostDetails);
        txtPostTitle = findViewById(R.id.txtPostTitle);
        spTypeOfMatch = findViewById(R.id.spTypeOfMatch);
        spTeamType = findViewById(R.id.spTeamType);
        uploadswitch = findViewById(R.id.uploadswitch);
        simage = findViewById(R.id.simage);
        dialog = new AlertDialog.Builder(GLUploadActivity.this);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        vtime = findViewById(R.id.vtime);
        vschudle = findViewById(R.id.vschudle);

        mAuth = FirebaseAuth.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime()));
        txtTime.setText(new SimpleDateFormat("h:mm a").format(Calendar.getInstance().getTime()));

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, matchType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypeOfMatch.setAdapter(aa);
        spTypeOfMatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(GLUploadActivity.this, matchType[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter aaTeamType = new ArrayAdapter(this, android.R.layout.simple_spinner_item, teamType);
        aaTeamType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTeamType.setAdapter(aaTeamType);
        spTeamType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(GLUploadActivity.this, teamType[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnSetDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        btnSetTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            Calendar date;
            date = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            txtTime.setText(getTime(hourOfDay, minute));

                            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            date.set(Calendar.MINUTE, minute);
                            date.clear(Calendar.SECOND);

                            //Log.d("time", dateAndTime);
                            t = String.valueOf(date.getTimeInMillis());
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

        //mycode


        simage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
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
                            simage.setVisibility(View.GONE);
                            vschudle.setVisibility(View.VISIBLE);
                            Calendar date;
                            final Calendar currentDate = Calendar.getInstance();
                            date = Calendar.getInstance();
                            new DatePickerDialog(GLUploadActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    date.set(year, monthOfYear, dayOfMonth);
                                    new TimePickerDialog(GLUploadActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            date.set(Calendar.MINUTE, minute);
                                            st = String.valueOf(date.getTimeInMillis());


//                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:MM:ss");
                                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");// HH:MM:ss");
                                            String dateString = formatter.format(new Date(date.getTimeInMillis()));
                                            Log.v("TAG", "The choosen one1 "+"  "+dateString);
                                            vtime.setText(dateString);
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
                            simage.setVisibility(View.GONE);
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

        btnUploadPost.setOnClickListener(v -> {

//            pD = new ProgressDialog(GLUploadActivity.this);
//            pD.setMessage("Please wait...");
//            pD.setIndeterminate(true);
//            pD.setCancelable(false);
//            pD.show();
//            if (profile_uri != null) {
//                final StorageReference fileRefernce = storageReference.child(System.currentTimeMillis() + "." + getFileExte(profile_uri));
//                Bitmap bmp = null;
//                try {
//                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), profile_uri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
//                byte[] data = baos.toByteArray();
//
//                uploadTask = fileRefernce.putBytes(data);
//                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(Task<UploadTask.TaskSnapshot> tas) throws Exception {
//                        if (!tas.isSuccessful()) {
//                            throw tas.getException();
//                        }
//                        return fileRefernce.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> tas) {
//                        if (tas.isSuccessful()) {
//                            Uri downloadUri = tas.getResult();
//                            String mUri = downloadUri.toString();
//                            reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("schedule").child(st);
//                            HashMap<String, Object> map = new HashMap<>();
//                            map.put("postimg", mUri);
//                            reference.updateChildren(map);
//                            pD.dismiss();
//                            showTo("Photo Uploaded successfully");
//                            finish();
////                            new UpdateActivity.Notify().execute();
//                        } else {
//                            showTo("Failed");
//                            pD.dismiss();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        showTo("Failed");
//                        pD.dismiss();
//                    }
//                });
//            } else {
//                pD.dismiss();
//                showTo("No Image Selected");
//            }

            if (txtPostTitle.getText().toString().trim().length() > 0 && txtPostDetails.getText().toString().trim().length() > 0) {
//                pD = new ProgressDialog(GLUploadActivity.this);
//                pD.setMessage("Please wait...");
//                pD.setIndeterminate(true);
//                pD.setCancelable(false);
//                pD.show();
//
//
//                final StorageReference fileRefernce = storageReference.child(System.currentTimeMillis() + "." + getFileExte(profile_uri));
//                Bitmap bmp = null;
//                try {
//                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), profile_uri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
//                byte[] data = baos.toByteArray();
//
//                uploadTask = fileRefernce.putBytes(data);
//                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(Task<UploadTask.TaskSnapshot> tas) throws Exception {
//                        if (!tas.isSuccessful()) {
//                            throw tas.getException();
//                        }
//                        return fileRefernce.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> tas) {
//                        if (tas.isSuccessful()) {
//                            Uri downloadUri = tas.getResult();
//                            String mUri = downloadUri.toString();
//                            reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("schedule").child(st);
//                            HashMap<String, Object> map = new HashMap<>();
//                            map.put("postimg", mUri);
//                            reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//
//
//
//                                    }else{
//                                        Toast.makeText(GLUploadActivity.this, "something went wrong please upload again", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                            pD.dismiss();
//                            showTo("Photo Uploaded successfully");
//                            finish();
////                            new UpdateActivity.Notify().execute();
//                        } else {
//                            showTo("Failed");
//                            pD.dismiss();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        showTo("Failed");
//                        pD.dismiss();
//                    }
//                });


                try {
                    FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
                    DatabaseReference myRef;
                    Log.d("tag", "checking10"+schedule +" "+st);
                    if (schedule) {
                        if (st == null || st.matches("")) {
                            Toast.makeText(GLUploadActivity.this, "please choose sechdule date and time", Toast.LENGTH_SHORT).show();
                        } else {
                            myRef = database.getReference("schedule").child(st);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("posttitle", txtPostTitle.getText().toString().trim());
                            hashMap.put("postdetails", txtPostDetails.getText().toString().trim());
                            hashMap.put("postMatchType", spTypeOfMatch.getSelectedItem().toString());
                            hashMap.put("postLeagueType", "GL");
                            hashMap.put("postTeamType", spTeamType.getSelectedItem().toString());
                            hashMap.put("postdatetime", txtDate.getText().toString().trim() + " " + txtTime.getText().toString().trim());
                            hashMap.put("postimg", "default");
                            hashMap.put("isScheduled", "false");
                            hashMap.put("scheduledTime", t);
                            myRef.setValue(hashMap);
                            sendnotification(txtPostTitle.getText().toString().trim(), txtPostDetails.getText().toString().trim(), st);
                            Toast.makeText(getApplicationContext(), "schedule Post Upload Successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        myRef = database.getReference("Match").push();
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("posttitle", txtPostTitle.getText().toString().trim());
                        hashMap.put("postdetails", txtPostDetails.getText().toString().trim());
                        hashMap.put("postMatchType", spTypeOfMatch.getSelectedItem().toString());
                        hashMap.put("postLeagueType", "GL");
                        hashMap.put("postTeamType", spTeamType.getSelectedItem().toString());
                        hashMap.put("postdatetime", txtDate.getText().toString().trim() + " " + txtTime.getText().toString().trim());
                        hashMap.put("postimg", "default");
                        hashMap.put("isScheduled", "false");
                        hashMap.put("scheduledTime", t);
                        myRef.setValue(hashMap);
                        new Notify().execute();
                        Toast.makeText(getApplicationContext(), "Post Upload Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }


            } else {
                Toast.makeText(getApplicationContext(), "Please fill all required field's", Toast.LENGTH_LONG).show();
            }
        });
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
    public void showTo(String mess) {
        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int reqCode, int result, Intent da) {
        super.onActivityResult(reqCode, result, da);
        if (reqCode == imgReq && result == RESULT_OK && da != null && da.getData() != null) {
            profile_uri = da.getData();
//            imgUpdate.setImageURI(null);
//            imgUpdate.setImageURI(da.getData());
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

    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(tme);
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
                info.put("title", txtPostTitle.getText().toString().trim());   // Notification title
                info.put("text", txtPostTitle.getText().toString().trim()); // Notification body


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

}