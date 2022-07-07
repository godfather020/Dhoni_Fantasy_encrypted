package com.app.dharaneesh;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.dharaneesh.support.chatlistactivity;
import com.app.dharaneesh.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class AdminDashActivity extends AppCompatActivity {
    LinearLayout btnShowMatch, btnListUser, btnShowGlMatchAdmin, btnShowExpiredUsers, adminchatbtn,
            llAdminDashActivity,btnPaymentDetails ;
    Button btnAdminDashLogout;
    TextView tvAdminDashActivityPendingUserCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash);
        btnShowMatch = findViewById(R.id.btnShowMatchAdmin);
        btnListUser = findViewById(R.id.btnListUser);
        btnShowGlMatchAdmin = findViewById(R.id.btnShowGlMatchAdmin);
        btnShowExpiredUsers = findViewById(R.id.btnShowExpiredUsers);
        btnAdminDashLogout = findViewById(R.id.btnAdminDashLogout);
        tvAdminDashActivityPendingUserCount = findViewById(R.id.tvAdminDashActivityPendingUserCount);
        adminchatbtn = findViewById(R.id.adminchatbtn);
        llAdminDashActivity = findViewById(R.id.llAdminDashActivity);
        btnPaymentDetails = findViewById(R.id.btnPaymentDetails);


        getPendingUsersCount();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String t = task.getResult();
             FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                     .child("cloudMessagingToken").setValue(t);
            }
        });

        adminchatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(getApplicationContext(), chatlistactivity.class);
                startActivity(go);
//                finish();
            }
        });


        FirebaseMessaging.getInstance().subscribeToTopic("/topics/allDevices");

        btnShowMatch.setOnClickListener(v -> {
            Intent showIntent = new Intent(getApplicationContext(), ShowMatchAdminActivity.class);
            startActivity(showIntent);
        });

        btnListUser.setOnClickListener(v -> {
            Intent showIntent = new Intent(getApplicationContext(), UserListActivity.class);
            showIntent.putExtra("forExpireUsers", "no");
            startActivity(showIntent);
        });

        btnShowGlMatchAdmin.setOnClickListener(v -> {
            Intent showIntent = new Intent(getApplicationContext(), ShowGlMatchActivity.class);
            startActivity(showIntent);
        });

        btnPaymentDetails.setOnClickListener(v ->{

            Intent paymentDetails = new Intent(getApplicationContext(), PaymentPackageDetailsAdmin.class);
            startActivity(paymentDetails);
        });

        btnShowExpiredUsers.setOnClickListener(v -> {
            Intent showIntent = new Intent(getApplicationContext(), UserListActivity.class);
            showIntent.putExtra("forExpireUsers", "yes");
            startActivity(showIntent);
        });

        llAdminDashActivity.setOnClickListener(v -> {
            Intent showIntent = new Intent(getApplicationContext(), UserListActivity.class);
            showIntent.putExtra("forPending", "yes");
            startActivity(showIntent);
        });

        btnAdminDashLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("My App", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(AdminDashActivity.this, LoginActivity.class));
                finish();
            }
        });


        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
        String mailId = prefs.getString("username", "");

        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference userReference = datab.getReference();

        Query query = userReference
                .child("Users")
                .orderByChild("email")
                .equalTo(mailId.toLowerCase().trim().toLowerCase());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() <= 0) {
                    SharedPreferences.Editor editor = getSharedPreferences("My App", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                startActivity(new Intent(AdminDashActivity.this, LoginActivity.class));
               finish();
                    Toast.makeText(AdminDashActivity.this, "you are removed from admin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AdminDashActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPendingUsersCount() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference dbRef= database.getReference().child("PendingUsers");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snap: dataSnapshot.getChildren()) {
//                    Log.e(snap.getKey(),snap.getChildrenCount() + ""); // here it'll get the size
//                }
                tvAdminDashActivityPendingUserCount.setText(dataSnapshot.getChildrenCount()+"");
//                dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});
    }

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.abhi_app_icon);
        builder.setMessage("Do you Wish to exit from App?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}