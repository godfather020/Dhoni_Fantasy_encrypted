package com.app.dharaneesh;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dharaneesh.util.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class PostUploadActivity extends AppCompatActivity {
    Button btnSetDate, btnSetTime, btnUploadPost;
    TextView txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText txtPostTitle, txtPostDetails;
    DatabaseReference reference;
    private FirebaseAuth mAuth;
    FirebaseUser fuser;
    Spinner spTypeOfMatch, spTeamType;
    String[] matchType = {"Cricket", "Football", "Basketball", "kabaddi", "Baseball", "Volleyball", "Hockey"};
    String[] teamType = {"Prime", "Open"};
    String  t = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);
        btnSetDate = findViewById(R.id.btnSetDate);
        btnSetTime = findViewById(R.id.btnSetTime);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        btnUploadPost = findViewById(R.id.btnUpload);
        txtPostDetails = findViewById(R.id.txtPostDetails);
        txtPostTitle = findViewById(R.id.txtPostTitle);
        spTypeOfMatch = findViewById(R.id.spTypeOfMatch);
        spTeamType = findViewById(R.id.spTeamType);

        mAuth = FirebaseAuth.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime()));
        txtTime.setText(new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime()));


        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, matchType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypeOfMatch.setAdapter(aa);
        spTypeOfMatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(PostUploadActivity.this, matchType[position], Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(PostUploadActivity.this, teamType[position], Toast.LENGTH_SHORT).show();
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

        btnUploadPost.setOnClickListener(v -> {
            if (txtPostTitle.getText().toString().trim().length() > 0 && txtPostDetails.getText().toString().trim().length() > 0) {
                try {
                    FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
                    DatabaseReference myRef = database.getReference("Match").push();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("posttitle", txtPostTitle.getText().toString().trim());
                    hashMap.put("postdetails", txtPostDetails.getText().toString().trim());
                    hashMap.put("postMatchType", spTypeOfMatch.getSelectedItem().toString());
                    hashMap.put("postTeamType", spTeamType.getSelectedItem().toString());
                    hashMap.put("postLeagueType", "SL");
                    hashMap.put("postdatetime", txtDate.getText().toString().trim() + " " + txtTime.getText().toString().trim());
                    hashMap.put("postimg", "default");
                    hashMap.put("isScheduled", "false");
                    hashMap.put("scheduledTime", t);
                    myRef.setValue(hashMap);
                    new Notify().execute();
                    Toast.makeText(getApplicationContext(), "Post Upload Successfully", Toast.LENGTH_LONG).show();
                    finish();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please fill all required field's", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);
        Format formatter;
        formatter = new SimpleDateFormat("hh:mm a");
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