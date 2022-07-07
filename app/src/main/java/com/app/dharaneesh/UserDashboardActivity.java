package com.app.dharaneesh;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dharaneesh.adapter.TabLayoutAdapter;
import com.app.dharaneesh.interfaces.TabLayoutInterface;
import com.app.dharaneesh.support.chatlistactivity;
import com.app.dharaneesh.util.Constant;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserDashboardActivity extends AppCompatActivity {
    ListView lstUser;
    ArrayList<MatchDataModel> matchDataModelsArr;
    Switch swShowGLTeam;
    FloatingActionButton supportbtn;
    String currentDate;
    ImageView btnLogoutUser;
    String version;
    Date c = Calendar.getInstance().getTime();
    TabLayoutAdapter tabLayoutAdapter;
    RecyclerView rvTabLayoutTitle;
    ArrayList<String> tabTitle;
    TabLayoutInterface tabLayoutInterface;
    String tabTitleMain = "all";
    TextView tvUserName, tvExpireIn;
    ShimmerFrameLayout shimmerLayout;
    ImageView ivError404;
    ArrayList<String> slist = new ArrayList<>();
    MatchDataModel matchDataModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        lstUser = findViewById(R.id.lstUsermatch);
        swShowGLTeam = findViewById(R.id.swShowGLTeam);
        btnLogoutUser = findViewById(R.id.btnLogoutUser);
        supportbtn = findViewById(R.id.supportbtn);
        rvTabLayoutTitle = findViewById(R.id.rvTabLayoutTitle);
        tvUserName = findViewById(R.id.tvUserName);
        tvExpireIn = findViewById(R.id.tvExpireIn);
        shimmerLayout = findViewById(R.id.shimmerLayout);
        ivError404 = findViewById(R.id.ivError404);
        tabTitle = new ArrayList<>();

        shimmerLayout.startShimmer();


        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));

        tabLayoutInterface = new TabLayoutInterface() {
            @Override
            public void onClick(String tabTitle) {
                tabTitleMain = tabTitle;
//                if (swShowGLTeam.isChecked()) {
//                    fetchData("GLMatch");
//                } else {
                fetchData("Match");
//                }
            }
        };

        tabTitle.add("All");
        tabTitle.add("Cricket");
        tabTitle.add("Football");
        tabTitle.add("Basketball");
        tabTitle.add("Kabaddi");
        tabTitle.add("Baseball");
        tabTitle.add("Volleyball");
        tabTitle.add("Hockey");

        tabLayoutAdapter = new TabLayoutAdapter(tabTitle, this, tabLayoutInterface);
        rvTabLayoutTitle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTabLayoutTitle.setHasFixedSize(true);
        rvTabLayoutTitle.setAdapter(tabLayoutAdapter);


        FirebaseMessaging.getInstance().subscribeToTopic("/topics/allDevices");

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String t = task.getResult();
                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                        .child("cloudMessagingToken").setValue(t);
            }
        });


        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("firUserId", "No name defined");
        String mailId = prefs.getString("username", "");

        if (mailId == null) {
            finishAffinity();
        } else if (mailId.trim().equals("")) {
            finishAffinity();
        }

        supportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(getApplicationContext(), chatlistactivity.class);
                startActivity(go);
//                finish();
            }
        });

        fetchAppVersion();
//
//
        if (id.equalsIgnoreCase("No name defined")) {
            checkPhoneLogin checkPhone = new checkPhoneLogin();
            String mob = checkPhone.checkStatus(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), LoginActivity.userLoginId, this);
        } else {
            checkPhoneLogin checkPhone = new checkPhoneLogin();
            String mob = checkPhone.checkStatus(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), id, this);
        }

        fetchData("Match");
        swShowGLTeam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fetchData("GLMatch");
                } else {
                    fetchData("Match");
                }
            }
        });

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
                    startActivity(new Intent(UserDashboardActivity.this, LoginActivity.class));
                    finish();
                    Toast.makeText(UserDashboardActivity.this, "you are removed from admin", Toast.LENGTH_SHORT).show();
                } else {
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    currentDate = df.format(c);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (!snapshot.child("payment").getValue(String.class).equals("No")) {
                            String expirationDate = snapshot.child("expirationDate").getValue(String.class);
                            String username = snapshot.child("username").getValue(String.class);

                            tvUserName.setText(username);

                            long diff = getDateFromString(expirationDate).getTime() - getDateFromString(currentDate).getTime();
                            long seconds = diff / 1000;
                            long minutes = seconds / 60;
                            long hours = minutes / 60;
                            long days = hours / 24;

                            Log.e("thisIsDay", days + "");


                            tvExpireIn.setText("Expiring in " + days + " Days");

                            if (days <= 0) {
                                //Intent for subscription activity
                                Intent intent = new Intent(UserDashboardActivity.this, StaticSubsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Intent intent = new Intent(UserDashboardActivity.this, StaticSubsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserDashboardActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        btnLogoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserDashboardActivity.this)
                        .setTitle("Logout?")
                        .setMessage("Are you sure you want to logout?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = getSharedPreferences("My App", MODE_PRIVATE).edit();
                                editor.clear();
                                editor.apply();
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(UserDashboardActivity.this, LoginActivity.class));
                                finish();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

    }

    public void fetchData(String whichTeam) {
        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference(whichTeam);

        matchDataModelsArr = new ArrayList<>();

        myRedrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                matchDataModelsArr.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    matchDataModel = dataSnapshot.getValue(MatchDataModel.class);
                    if (matchDataModel.getPosttitle() != null) {
                        matchDataModel.setId(dataSnapshot.getKey());
                        if (tabTitleMain.equalsIgnoreCase("all")) {
                            matchDataModelsArr.add(matchDataModel);
                        } else if (matchDataModel.getPostMatchType().equalsIgnoreCase(tabTitleMain)) {
                            matchDataModelsArr.add(matchDataModel);
                        }
                    }
                }

                if (matchDataModelsArr.size() <= 0) {
                    ivError404.setVisibility(View.VISIBLE);
                } else {
                    ivError404.setVisibility(View.GONE);
                }
                lstUser.setAdapter(new CostumAdapter(getApplicationContext(), matchDataModelsArr, whichTeam, "Prime"));

//                lstUser.setAdapter(new CostumAdapter(getApplicationContext(), matchTypeArr, titleArr, dateTimeArr, idArr, detailsArr, imgPathArr, whichTeam, leagueType, "Prime", teamTypeArr));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserDashboardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Date getDateFromString(String date) {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date finalDate = formatter.parse(date);
            return finalDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void fetchAppVersion() {
        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("AppDetails");

        myRedrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AppDetailsModel matchDataModel = snapshot.getValue(AppDetailsModel.class);

                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = String.valueOf(pInfo.versionCode);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (!version.equals(matchDataModel.getVersionName())) {
                    Toast.makeText(UserDashboardActivity.this, "Update Available", Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(UserDashboardActivity.this)
                            .setCancelable(false)
                            .setTitle("Update Available")
                            .setMessage("Please update app to continue")

                            .setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(matchDataModel.getAppLink())));
                                    finish();
                                }
                            })
                            .show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserDashboardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserDashboardActivity.this);
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