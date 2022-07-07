package com.app.dharaneesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.dharaneesh.models.UserModel;
import com.app.dharaneesh.util.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UserListActivity extends AppCompatActivity {

    ArrayList<UserModelClass> myListData;
//    ArrayList<UserModel> userModelArrayList;
    String forExpireUsers, forPending;
    Button btnActivityUserListAddNew;
    TextView tvActivityUserList;
    EditText etActivityUserList;
    MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        myListData = new ArrayList<>();
//        userModelArrayList = new ArrayList<>();
        forExpireUsers = getIntent().getStringExtra("forExpireUsers");
        forPending = getIntent().getStringExtra("forPending");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String getCurrentDateTime = sdf.format(c.getTime());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvUserList);
        btnActivityUserListAddNew = findViewById(R.id.btnActivityUserListAddNew);
        tvActivityUserList = findViewById(R.id.tvActivityUserList);
        etActivityUserList = findViewById(R.id.etActivityUserList);
        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("Users");
        DatabaseReference pendingRef = datab.getReference("PendingUsers");

        adapter = new MyListAdapter(myListData, this, forPending);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
        recyclerView.setAdapter(adapter);

        etActivityUserList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnActivityUserListAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showIntent = new Intent(getApplicationContext(), UserCreateActivity.class);
                startActivity(showIntent);
            }
        });

        if (forPending != null && forPending.equalsIgnoreCase("yes")) {
            btnActivityUserListAddNew.setVisibility(View.GONE);
            tvActivityUserList.setText("Pending Users List");
            pendingRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (myListData != null) {
                        myListData.clear();
                    }
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserModelClass userModelClass = dataSnapshot.getValue(UserModelClass.class);
                        myListData.add(userModelClass);
                    }
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            myRedrence.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (myListData != null) {
                        myListData.clear();
                    }
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserModelClass userModelClass = dataSnapshot.getValue(UserModelClass.class);
                        if (forExpireUsers.equals("no")) {
                            myListData.add(userModelClass);
                        } else {
                            if (userModelClass.getExpirationDate() != null) {
                                String getMyTime = userModelClass.getExpirationDate();

                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    Date strDate = sdf.parse(getMyTime);
                                    if (new Date().after(strDate)) {
                                        myListData.add(userModelClass);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        if (forExpireUsers != null && forExpireUsers.equals("yes")) {
            tvActivityUserList.setText("Expired Users List");
            btnActivityUserListAddNew.setVisibility(View.GONE);
        }

    }

    private void filter(String text) {
        ArrayList<UserModelClass> filteredList = new ArrayList<>();

        for (UserModelClass item : myListData) {
            if (item.getEmail().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }

}