package com.app.dharaneesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.app.dharaneesh.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowGlMatchActivity extends AppCompatActivity {

    ListView lstAdminSideMatch;
    Button btnAddNewMatch;
    ArrayList<String> dateTimeArr;
    ArrayList<String> titleArr;
    ArrayList<String> detailsArr;
    ArrayList<String> idArr;
    ArrayList<String> imgArr;
    ArrayList<String> teamTypeArr;
    ArrayList<String> isScheduled;
    ArrayList<MatchDataModel> matchDataModelsArr;
    ArrayList<String> scheduledTime;
    ArrayList<String> slist;
    Button livebtn, schedulebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gl_match);
        lstAdminSideMatch = findViewById(R.id.lstAdminMatch);
        btnAddNewMatch = findViewById(R.id.btnAddNewMatch);
        slist = new ArrayList<>();
        livebtn = findViewById(R.id.livebtn);
        schedulebtn = findViewById(R.id.schudlebtn);


        //mycode
        /*FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("schedule")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        slist.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){

                                //MatchDataModel matchDataModel = snapshot1.getValue(MatchDataModel.class);
                                String key = snapshot1.getKey();
                                slist.add(key);

                            }
                            Log.d("tag", "checkmatch"+slist.size());
                            for(int i = 0; i<slist.size(); i++){
                                int j = i;
                                if(System.currentTimeMillis()> Long.parseLong(slist.get(j))){

                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("schedule").child(slist.get(j))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    MatchDataModel model = snapshot.getValue(MatchDataModel.class);

                                                    String keyy = snapshot.child("keys").getValue(String.class);
                                                    String img = snapshot.child("postimg").getValue(String.class);

                                                    Log.d("tag", "checkmatch1"+img);
                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("Match").child(keyy).child("postimg").setValue(img)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference()
                                                                            .child("schedule").child(slist.get(j)).removeValue();
                                                                }
                                                            });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }




                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("Match");

        Query postQuery = myRedrence.orderByChild("postLeagueType").equalTo("GL");

        postQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchDataModel matchDataModel = null;
                dateTimeArr = new ArrayList<>();
                titleArr = new ArrayList<>();
                idArr = new ArrayList<>();
                imgArr = new ArrayList<>();
                teamTypeArr = new ArrayList<>();
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
                    if (us.getPostLeagueType() != null && us.getPostLeagueType().equalsIgnoreCase("GL")) {
                        dateTimeArr.add(us.getPostdatetime());
                        titleArr.add(us.getPosttitle());
                        detailsArr.add(us.getPostdetails());
                        imgArr.add(us.getPostimg());
                        teamTypeArr.add(us.getPostTeamType());
                        isScheduled.add(us.getIsScheduled());
                        scheduledTime.add(us.getScheduledTime());
                    }
                }
                lstAdminSideMatch.setAdapter(new CostumAdapterAdmin(ShowGlMatchActivity.this, titleArr, dateTimeArr, idArr, "GLMatch", detailsArr, imgArr, teamTypeArr, isScheduled, scheduledTime));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



//        schedulebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
//                DatabaseReference myRedrence = datab.getReference("schedule");
//                myRedrence.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        MatchDataModel matchDataModel = null;
//                        dateTimeArr = new ArrayList<>();
//                        titleArr = new ArrayList<>();
//                        idArr = new ArrayList<>();
//                        imgArr = new ArrayList<>();
//                        teamTypeArr = new ArrayList<>();
//                        detailsArr = new ArrayList<>();
//                        matchDataModelsArr = new ArrayList<>();
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            idArr.add(dataSnapshot.getKey());
//                            matchDataModel = dataSnapshot.getValue(MatchDataModel.class);
//                            matchDataModelsArr.add(matchDataModel);
//                        }
//                        for (int l = 0; l < matchDataModelsArr.size(); l++) {
//                            MatchDataModel us = matchDataModelsArr.get(l);
//                            if (us.getPostLeagueType() != null && us.getPostLeagueType().equalsIgnoreCase("GL")) {
//                                dateTimeArr.add(us.getPostdatetime());
//                                titleArr.add(us.getPosttitle());
//                                detailsArr.add(us.getPostdetails());
//                                imgArr.add(us.getPostimg());
//                                teamTypeArr.add(us.getPostTeamType());
//                            }
//                        }
//                        lstAdminSideMatch.setAdapter(new CostumAdapterAdmin(ShowGlMatchActivity.this, titleArr, dateTimeArr, idArr, "admin", detailsArr, imgArr, teamTypeArr));
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//            }
//        });



        btnAddNewMatch.setOnClickListener(v -> {
            Intent addIntent = new Intent(getApplicationContext(), GLUploadActivity.class);
            startActivity(addIntent);
            finish();
        });
    }
}