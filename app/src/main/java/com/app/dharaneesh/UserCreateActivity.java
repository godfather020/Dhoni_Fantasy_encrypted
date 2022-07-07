package com.app.dharaneesh;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.dharaneesh.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class UserCreateActivity extends AppCompatActivity {
    EditText txtUsername, txtUserMail, txtUserPassword;
    Spinner comUserType;
    //    Spinner comUserExpiryDate;
    TextView tvDialogUpdatePaymentExpDate;
    Button btnSubmit;
    RadioGroup gorupPayment;
    DatabaseReference reference;
    private FirebaseAuth mAuth;
    String email, password;
    String pay = "";
    FirebaseUser fuser;
    String userId = "";
    String[] usType = {"user", "admin"};
    String[] expireTime = {"15 Days", "30 Days", "60 Days", "90 Days"};
    RadioButton radioYes, radioNo;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create);
        txtUserMail = findViewById(R.id.txtusermail);
        txtUsername = findViewById(R.id.txtUserName);
        txtUserPassword = findViewById(R.id.txtUserpassword);
        btnSubmit = findViewById(R.id.btnSubmitNewUser);
        gorupPayment = findViewById(R.id.radioPayment);
        radioNo = findViewById(R.id.radioNo);
        radioYes = findViewById(R.id.radioYes);
        comUserType = findViewById(R.id.comUserType);
        tvDialogUpdatePaymentExpDate = findViewById(R.id.tvDialogUpdatePaymentExpDate);
//        comUserExpiryDate = findViewById(R.id.comUserExpiryDate);

        DatePickerDialog.OnDateSetListener datem = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel(tvDialogUpdatePaymentExpDate);
            }
        };

        tvDialogUpdatePaymentExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UserCreateActivity.this, datem, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, usType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comUserType.setAdapter(aa);

//        ArrayAdapter aaExpire = new ArrayAdapter(this, android.R.layout.simple_spinner_item, expireTime);
//        aaExpire.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        comUserExpiryDate.setAdapter(aaExpire);

        mAuth = FirebaseAuth.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        comUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
//                    if (radioYes.isChecked()){
                        tvDialogUpdatePaymentExpDate.setVisibility(View.VISIBLE);
//                    }
                } else {
                    tvDialogUpdatePaymentExpDate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        radioNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvDialogUpdatePaymentExpDate.setVisibility(View.GONE);
                } else {
                    if (comUserType.getSelectedItem().toString().equals("user")) {
                        tvDialogUpdatePaymentExpDate.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        btnSubmit.setOnClickListener(v -> {
            String type = comUserType.getSelectedItem().toString();
//            String expireDays = comUserExpiryDate.getSelectedItem().toString();
            String date = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy", new java.util.Date()));
//            String expirationDate = addDays(date, expireDays);
            String expirationDate = tvDialogUpdatePaymentExpDate.getText().toString();
            if (txtUserPassword.getText().toString().trim().length() > 5) {
                if (txtUserMail.getText().toString().trim().length() > 0 && txtUsername.getText().toString().trim().length() > 0) {
                    String userName = txtUsername.getText().toString().trim();

                    if (radioYes.isChecked()) {
                        pay = "Yes";
                    } else {
                        pay = "No";
                    }
                    email = txtUserMail.getText().toString().trim().toLowerCase();
                    password = txtUserPassword.getText().toString().trim();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(UserCreateActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> ta) {
                                    if (ta.isSuccessful()) {
                                        FirebaseUser firUser = mAuth.getCurrentUser();
                                        assert firUser != null;
                                        userId = firUser.getUid();

//                                          Initialize admin chat
                                        Log.i("chatinituserid", userId);
                                        DatabaseReference usersRef = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("Users");
                                        ValueEventListener eventListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    Log.i("chatinit", ds.getValue() + "," + ds.child("role").getValue());
                                                    String userrole = String.valueOf(ds.child("role").getValue());
                                                    if (userrole.matches("admin")) {
//                                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(userId).child(String.valueOf(ds.child("id").getValue())).child("senderid")
//                                                                .setValue(String.valueOf(ds.child("id").getValue()));
//                                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(userId).child(String.valueOf(ds.child("id").getValue())).child("sendername")
//                                                                .setValue(String.valueOf(ds.child("username").getValue()));
                                                    }
//                                                    if (ds.child("unreadCount").getValue() == null) {
//                                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(String.valueOf(ds.child("id").getValue())).child("unreadCount")
//                                                                .setValue(1);
//                                                    } else {
//                                                        int unreadCount = ((Long) ds.child("unreadCount").getValue()).intValue();
//                                                        unreadCount = unreadCount + 1;
//                                                        Log.i("unreadCount", String.valueOf(ds.child("unreadCount").getValue()) + FirebaseAuth.getInstance().getUid() + userId_receiver);
//
//                                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(String.valueOf(ds.child("id").getValue())).child("unreadCount")
//                                                                .setValue(unreadCount);
//                                                    }
                                                }
//                                                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(FirebaseAuth.getInstance().getUid()).child("unreadCount")
//                                                        .setValue(null);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        };
                                        usersRef.addListenerForSingleValueEvent(eventListener);

//                                          Initialize admin chat

                                        reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Users").child(userId);
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("id", userId);
                                        hashMap.put("username", userName);
                                        hashMap.put("payment", pay);
                                        hashMap.put("email", email);
                                        hashMap.put("role", type);

                                        if (type.equals("user")) {
//                                            hashMap.put("expireDays", expireDays);
                                            hashMap.put("creationDate", date);
                                            hashMap.put("expirationDate", expirationDate);
                                            hashMap.put("isEligibleForChat", "true");
                                        } else{
                                            hashMap.put("isEligibleForChat", "false");
                                        }

                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> UserRegtask) {
                                                if (UserRegtask.isSuccessful()) {
                                                    Intent mainIntent = new Intent(getApplicationContext(), AdminDashActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(mainIntent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "You can't register woth this email or password", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "createUserWithEmail:failure", Toast.LENGTH_LONG).show();
                                        Log.w("TAG", "createUserWithEmail:failure", ta.getException());
                                    }
                                }
                            });

                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all required field's", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please set password length 6 and upper", Toast.LENGTH_LONG).show();
            }
        });
    }

    public String addDays(String date, String days) {
        String dt = date;  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, Integer.parseInt(days.replace(" Days", "")));  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        String output = sdf1.format(c.getTime());
        return output;
    }

    private void updateLabel(TextView textView) {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        textView.setText(dateFormat.format(myCalendar.getTime()));
    }
}