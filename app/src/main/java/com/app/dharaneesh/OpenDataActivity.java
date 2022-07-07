package com.app.dharaneesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.dharaneesh.adapter.TabLayoutAdapter;
import com.app.dharaneesh.interfaces.TabLayoutInterface;
import com.app.dharaneesh.util.Constant;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OpenDataActivity extends AppCompatActivity {

    ListView lstUser;
    ArrayList<MatchDataModel> matchDataModelsArr;
    String tabTitleMain = "all";
    TabLayoutInterface tabLayoutInterface;
    TabLayoutAdapter tabLayoutAdapter;
    RecyclerView rvTabLayoutTitle;
    ArrayList<String> tabTitle;
    ArrayList<String> slist;
    Button btnUpdatePremium, btnLoginOpenData;
    ShimmerFrameLayout shimmerLayout;
    ImageView ivError404;
    MatchDataModel matchDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_data);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_500));

        lstUser = findViewById(R.id.lstUsermatch);
        rvTabLayoutTitle = findViewById(R.id.rvTabLayoutTitle);
        btnUpdatePremium = findViewById(R.id.btnUpdatePremium);
        btnLoginOpenData = findViewById(R.id.btnLoginOpenData);
        shimmerLayout = findViewById(R.id.shimmerLayout);
        ivError404 = findViewById(R.id.ivError404);
        slist = new ArrayList<>();



        //connect your phone and run


        shimmerLayout.startShimmer();

        btnUpdatePremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    Intent intent = new Intent(OpenDataActivity.this, StaticSubsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {

                    Toast.makeText(getApplicationContext(), "Please Login First", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnLoginOpenData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenDataActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);

        String id = prefs.getString("firUserId", "No name defined");
        String role = prefs.getString("role", "no");
        String mailId = prefs.getString("username", "no");
        String isGoogleLogin = prefs.getString("isGoogleLogin", "false");

        Log.d("user", id +" "+ role+" "+mailId+" "+ isGoogleLogin);

        if (isGoogleLogin != null && isGoogleLogin.equalsIgnoreCase("true")) {
            //Intent userIntent = new Intent(this, PendingActivity.class);
            Intent userIntent = new Intent(this, StaticSubsActivity.class);
            startActivity(userIntent);
            finish();
            return;
        }

        if (id.equalsIgnoreCase("No name defined")) {
        } else {
            if (mailId != null) {
                if (!mailId.trim().equals("")) {
                    if (role.equalsIgnoreCase("admin")) {
                        Intent userIntent = new Intent(getApplicationContext(), AdminDashActivity.class);
//                        Intent userIntent = new Intent(getApplicationContext(), mainsupportactivity.class);
                        startActivity(userIntent);
                        finish();
                    } else if (role.equalsIgnoreCase("user")) {
//                        Intent userIntent = new Intent(getApplicationContext(), mainsupportactivity.class);
                        Intent userIntent = new Intent(getApplicationContext(), UserDashboardActivity.class);
                        startActivity(userIntent);
                        finish();
                    } else if (role.equalsIgnoreCase("pending")) {
//                        Intent userIntent = new Intent(getApplicationContext(), mainsupportactivity.class);
                        //Intent userIntent = new Intent(getApplicationContext(), PendingActivity.class);
                        Intent userIntent = new Intent(getApplicationContext(), StaticSubsActivity.class);
                        startActivity(userIntent);
                        finish();
                    }
                }
            }
        }

        tabTitle = new ArrayList<>();

        tabLayoutInterface = new TabLayoutInterface() {
            @Override
            public void onClick(String tabTitle) {
                tabTitleMain = tabTitle;
//                if (swShowGLTeam.isChecked()) {
//                    fetchData("GLMatch");
//                } else {
                fetchData();
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

        fetchData();

    }

    public void fetchData() {
        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("Match");
        matchDataModelsArr = new ArrayList<>();

        myRedrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);

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
                lstUser.setAdapter(new CostumAdapter(getApplicationContext(), matchDataModelsArr, "Match", "Open"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OpenDataActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(OpenDataActivity.this);
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