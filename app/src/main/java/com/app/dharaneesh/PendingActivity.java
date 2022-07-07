package com.app.dharaneesh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.app.dharaneesh.models.UserModel;
import com.app.dharaneesh.util.Constant;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PendingActivity extends AppCompatActivity {

    AppCompatButton contactUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        contactUs = findViewById(R.id.btnContactTelegram);

        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("id", "");

        DatabaseReference reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();

        Query query = reference.child("Users").orderByChild("id").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        UserModel userModelClass = snap.getValue(UserModel.class);
                        SharedPreferences.Editor editor = getSharedPreferences("My App", MODE_PRIVATE).edit();
                        editor.putString("firUserId", id);
                        editor.putString("role", "user");
                        editor.putString("username", userModelClass.getEmail());
                        editor.putString("isGoogleLogin", "false");
                        DatabaseReference uniqueKeyRef = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Login").child(id);
                        HashMap<String, String> hashMap = new HashMap<>();
                        String id1 = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        hashMap.put("mobileId", id1);
                        uniqueKeyRef.setValue(hashMap);
//                                                    Intent userIntent = new Intent(getApplicationContext(), mainsupportactivity.class);
                        Intent userIntent = new Intent(getApplicationContext(), UserDashboardActivity.class);
                        startActivity(userIntent);
                        finish();
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Mastermaking"));
                    PackageManager pm = getPackageManager();
                    if (intent.resolveActivity(pm) != null) {
                        startActivity(intent);
                    } else {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Mastermaking"));
                        startActivity(intent1);
                        // Toast.makeText(StaticSubsActivity.this, "Error message", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }
}