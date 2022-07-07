package com.app.dharaneesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.dharaneesh.models.PackageDataModel;
import com.app.dharaneesh.util.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PaymentPackageDetailsAdmin extends AppCompatActivity {

    Button btnAddNewMatch;
    ListView lstPackage;
    TextView noPakTxt;
    ImageView noPakImg;
    ArrayList<String> packageName;
    ArrayList<String> packageDes;
    ArrayList<String> packagePrice;
    ArrayList<String> packageOffer;
    //ArrayList<String> packageImg;
    ArrayList<String> packageValidity;
    ArrayList<String> packageId;
    ArrayList<PackageDataModel> packageDataModelArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_package_details_admin);

        btnAddNewMatch = findViewById(R.id.btnAddNewMatch);
        lstPackage = findViewById(R.id.lstPackage);
        noPakImg = findViewById(R.id.noPakImg);
        noPakTxt = findViewById(R.id.noPacTxt);

        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("Package");

        myRedrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PackageDataModel packageDataModel = null;
                packageName = new ArrayList<>();
                packageDes = new ArrayList<>();
                //packageImg = new ArrayList<>();
                packageOffer = new ArrayList<>();
                packagePrice = new ArrayList<>();
                packageValidity = new ArrayList<>();
                packageId = new ArrayList<>();
                packageDataModelArr = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    packageId.add(dataSnapshot.getKey());
                    packageDataModel = dataSnapshot.getValue(PackageDataModel.class);
                    packageDataModelArr.add(packageDataModel);

                }
                for (int l = 0; l < packageDataModelArr.size(); l++) {
                    PackageDataModel us = packageDataModelArr.get(l);
                    if (us.getPackageValidity() != null) {
                        packageName.add(us.getPackageName());
                        packageDes.add(us.getPackageDetails());
                        packagePrice.add(us.getPackagePrice());
                        //packageImg.add(us.getPackageimg());
                        //Log.d("pakImg", us.getPackageimg());
                        packageValidity.add(us.getPackageValidity());
                        packageOffer.add(us.getPackageOfferPrice());
                    }
                }

                lstPackage.setAdapter(new PackageListAdapter(PaymentPackageDetailsAdmin.this, packageName, packageDes, packageId, packageValidity, packagePrice, packageOffer));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnAddNewMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent paymentDetails = new Intent(getApplicationContext(), PaymentDetailsAdmin.class);
                startActivity(paymentDetails);
            }
        });
    }

}