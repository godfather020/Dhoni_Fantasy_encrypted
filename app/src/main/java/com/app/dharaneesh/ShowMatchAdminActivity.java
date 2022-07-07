package com.app.dharaneesh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.dharaneesh.util.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowMatchAdminActivity extends AppCompatActivity {
    ListView lstAdminSideMatch;
    Button btnAddNewMatch;
    ArrayList<String> dateTimeArr;
    ArrayList<String> titleArr;
    ArrayList<String> detailsArr;
    ArrayList<String> idArr;
    ArrayList<String> imgArr, matchTypeArr;
    ArrayList<String> isScheduled;
    ArrayList<String> scheduledTime;
    ArrayList<MatchDataModel> matchDataModelsArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_match_admin);
        lstAdminSideMatch = findViewById(R.id.lstAdminMatch);
        btnAddNewMatch = findViewById(R.id.btnAddNewMatch);

        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("Match");
        
        Query postQuery = myRedrence.orderByChild("postLeagueType").equalTo("SL");

        postQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchDataModel matchDataModel = null;
                dateTimeArr = new ArrayList<>();
                titleArr = new ArrayList<>();
                idArr = new ArrayList<>();
                imgArr = new ArrayList<>();
                matchTypeArr = new ArrayList<>();
                detailsArr = new ArrayList<>();
                matchDataModelsArr = new ArrayList<>();
                isScheduled = new ArrayList<>();
                scheduledTime = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    idArr.add(dataSnapshot.getKey());
                    matchDataModel = dataSnapshot.getValue(MatchDataModel.class);
                    matchDataModelsArr.add(matchDataModel);

                }
                for (int l = 0; l < matchDataModelsArr.size(); l++) {
                    MatchDataModel us = matchDataModelsArr.get(l);
                    if (us.getPostLeagueType() != null && us.getPostLeagueType().equalsIgnoreCase("SL")) {
                        dateTimeArr.add(us.getPostdatetime());
                        titleArr.add(us.getPosttitle());
                        detailsArr.add(us.getPostdetails());
                        imgArr.add(us.getPostimg());
                        matchTypeArr.add(us.getPostTeamType());
                        isScheduled.add(us.getIsScheduled());
                        scheduledTime.add(us.getScheduledTime());
                    }
                }
                lstAdminSideMatch.setAdapter(new CostumAdapterAdmin(ShowMatchAdminActivity.this, titleArr, dateTimeArr, idArr, "Match", detailsArr, imgArr, matchTypeArr, isScheduled, scheduledTime));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnAddNewMatch.setOnClickListener(v -> {
            Intent addIntent = new Intent(getApplicationContext(), PostUploadActivity.class);
            startActivity(addIntent);
            finish();
        });
    }
}