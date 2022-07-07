package com.app.dharaneesh;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.app.dharaneesh.util.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class checkPhoneLogin {
    public static String re = "";
    ArrayList<checkLoginModel> checkLoginModelsArr = new ArrayList<>();

    public String checkStatus(String mobId, String userId, Context context) {
        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("Login").child(userId);

        myRedrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                checkLoginModel checkLogin = null;
                checkLoginModelsArr = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (mobId.equals(dataSnapshot.getValue())) {
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle("Already login")
                                .setMessage("Warning:Another login detect with the same Id. if you try the same one more id will be automatically blocked")
                                .setCancelable(false)

                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences.Editor editor = context.getSharedPreferences("My App", MODE_PRIVATE).edit();
                                        editor.clear();
                                        editor.apply();
                                        context.startActivity(new Intent(context, LoginActivity.class));
                                        ((Activity) context).finish();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return re;
    }
}
