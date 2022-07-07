package com.app.dharaneesh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btnUser, btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdmin = findViewById(R.id.btnAdmin);
        btnUser = findViewById(R.id.btnUser);

        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("firUserId", "No name defined");
        String role = prefs.getString("role", "no");

        if (id.equalsIgnoreCase("No name defined")) {
        } else {
            if (role.equalsIgnoreCase("admin")) {
                Intent userIntent = new Intent(getApplicationContext(), AdminDashActivity.class);
                startActivity(userIntent);
                finish();
            } else if (role.equalsIgnoreCase("user")) {
                Intent userIntent = new Intent(getApplicationContext(), UserDashboardActivity.class);
                startActivity(userIntent);
                finish();
            }
        }


        btnUser.setOnClickListener(v -> {
            Intent userIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(userIntent);
            finish();
        });

        btnAdmin.setOnClickListener(v -> {
            Intent userIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(userIntent);
            finish();
        });
    }
}