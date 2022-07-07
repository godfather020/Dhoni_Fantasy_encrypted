package com.app.dharaneesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.app.dharaneesh.util.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubscriptionActivity extends AppCompatActivity {

    TextView tvActivitySubscriptionPlanName1, tvActivitySubscriptionPlanAmount1, tvActivitySubscriptionPlanDays1,
            tvActivitySubscriptionPlanName2, tvActivitySubscriptionPlanAmount2, tvActivitySubscriptionPlanDays2,
            tvActivitySubscriptionPlanName3, tvActivitySubscriptionPlanAmount3, tvActivitySubscriptionPlanDays3;

    ArrayList<String> amountArrayList;
    ArrayList<String> durationArrayList;
    ArrayList<String> nameArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        amountArrayList = new ArrayList<>();
        durationArrayList = new ArrayList<>();
        nameArrayList = new ArrayList<>();

        tvActivitySubscriptionPlanName1 = findViewById(R.id.tvActivitySubscriptionPlanName1);
        tvActivitySubscriptionPlanAmount1 = findViewById(R.id.tvActivitySubscriptionPlanAmount1);
        tvActivitySubscriptionPlanDays1 = findViewById(R.id.tvActivitySubscriptionPlanDays1);

        tvActivitySubscriptionPlanName2 = findViewById(R.id.tvActivitySubscriptionPlanName2);
        tvActivitySubscriptionPlanAmount2 = findViewById(R.id.tvActivitySubscriptionPlanAmount2);
        tvActivitySubscriptionPlanDays2 = findViewById(R.id.tvActivitySubscriptionPlanDays2);

        tvActivitySubscriptionPlanName3 = findViewById(R.id.tvActivitySubscriptionPlanName3);
        tvActivitySubscriptionPlanAmount3 = findViewById(R.id.tvActivitySubscriptionPlanAmount3);
        tvActivitySubscriptionPlanDays3 = findViewById(R.id.tvActivitySubscriptionPlanDays3);

        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("Plans");

        myRedrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    amountArrayList.add(dataSnapshot.child("Amount").getValue(String.class));
                    durationArrayList.add(dataSnapshot.child("Duration").getValue(String.class));
                    nameArrayList.add(dataSnapshot.child("Name").getValue(String.class));
                }
                tvActivitySubscriptionPlanAmount1.setText("₹"+amountArrayList.get(0));
                tvActivitySubscriptionPlanAmount2.setText("₹"+amountArrayList.get(1));
                tvActivitySubscriptionPlanAmount3.setText("₹"+amountArrayList.get(2));

                tvActivitySubscriptionPlanDays1.setText(durationArrayList.get(0)+"\nDays");
                tvActivitySubscriptionPlanDays2.setText(durationArrayList.get(1)+"\nDays");
                tvActivitySubscriptionPlanDays3.setText(durationArrayList.get(2)+"\nDays");

                tvActivitySubscriptionPlanName1.setText(nameArrayList.get(0));
                tvActivitySubscriptionPlanName2.setText(nameArrayList.get(1));
                tvActivitySubscriptionPlanName3.setText(nameArrayList.get(2));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}