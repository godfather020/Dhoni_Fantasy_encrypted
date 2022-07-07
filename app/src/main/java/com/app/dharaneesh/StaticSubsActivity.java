package com.app.dharaneesh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.app.dharaneesh.models.PackageDataModel;
import com.app.dharaneesh.util.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StaticSubsActivity extends AppCompatActivity {

    Button btnActivityStaticSubsSubmit, btnActivityStaticLogout, btnActivityLogin;
    ListView packageDetails;
    ArrayList<String> packageName;
    ArrayList<String> packageDes;
    ArrayList<String> packagePrice;
    ArrayList<String> packageOffer;
    //ArrayList<String> packageImg;
    ArrayList<String> packageValidity;
    ArrayList<String> packageId;
    ArrayList<PackageDataModel> packageDataModelArr;
    PackageListAdapterUser packageListAdapterUser;
    private int LOAD_GOOGLEPAY_PAYMENT_DATA_REQUEST_CODE = 991;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_subs);

        btnActivityStaticSubsSubmit = findViewById(R.id.btnActivityStaticSubsSubmit);
        btnActivityStaticLogout = findViewById(R.id.btnActivityStaticLogout);
        btnActivityLogin = findViewById(R.id.btnActivityLogin);
        packageDetails = findViewById(R.id.packageDetails);

        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("username", "");

        if (id == null || id.equals("")) {
            btnActivityStaticLogout.setVisibility(View.GONE);
            btnActivityLogin.setVisibility(View.VISIBLE);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null){

            btnActivityStaticLogout.setVisibility(View.GONE);
            btnActivityLogin.setVisibility(View.VISIBLE);
        }
        else {

            btnActivityStaticLogout.setVisibility(View.VISIBLE);
            btnActivityLogin.setVisibility(View.GONE);
        }

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

                packageListAdapterUser = new PackageListAdapterUser(StaticSubsActivity.this, packageName, packageDes, packageId, packageValidity, packagePrice, packageOffer);

                packageDetails.setAdapter(packageListAdapterUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        btnActivityLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaticSubsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnActivityStaticLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("My App", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(StaticSubsActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnActivityStaticSubsSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(StaticSubsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_telegram);
                dialog.show();

                Button btnContactTelegram = dialog.findViewById(R.id.btnContactTelegram);

                btnContactTelegram.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/AK55667"));
                            PackageManager pm = getPackageManager();
                            if (intent.resolveActivity(pm) != null) {
                                startActivity(intent);
                            } else {
                                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/AK55667"));
                                startActivity(intent1);
                                // Toast.makeText(StaticSubsActivity.this, "Error message", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_GOOGLEPAY_PAYMENT_DATA_REQUEST_CODE){

            packageListAdapterUser.onActivityResult(requestCode, resultCode, data);
        }
    }
}