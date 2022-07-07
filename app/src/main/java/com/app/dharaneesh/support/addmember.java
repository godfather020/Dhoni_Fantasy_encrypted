package com.app.dharaneesh.support;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.app.dharaneesh.R;
import com.app.dharaneesh.UserModelClass;
import com.app.dharaneesh.adapter.addmemberadapter;
import com.app.dharaneesh.models.mainchat;
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
import java.util.HashMap;

public class addmember extends AppCompatActivity {
    String type, type1, groupname, topic;
    ArrayList<UserModelClass> myListData;

    DatabaseReference db = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();
    addmemberadapter adapter;
    EditText etActivityUserList;
    CheckBox selectall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmember);

        type = getIntent().getStringExtra("type");
        type1 = getIntent().getStringExtra("type1");
        if(type.matches("group")){

            groupname = getIntent().getStringExtra("groupname");
            topic = getIntent().getStringExtra("topic");
        }
        selectall = findViewById(R.id.selectall);
        myListData = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvUserList);
        etActivityUserList = findViewById(R.id.etActivityUserList);

        adapter = new addmemberadapter(myListData, type, type1, groupname,topic, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(addmember.this));
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





        if(type.matches("addadmin")) {
            if (type1.matches("add")) {
                DatabaseReference reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();
                Query query = reference.child("Users").orderByChild("role").equalTo("admin");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (myListData != null) {
                            myListData.clear();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserModelClass userModelClass = dataSnapshot.getValue(UserModelClass.class);
                            if (userModelClass.getIsEligibleForChat()!=null){
                                if (userModelClass.getIsEligibleForChat().matches("false")) {
                                    myListData.add(userModelClass);
                                    Log.d("tag", "checkingdata " + myListData.get(0).getUsername());

//                                    selectall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                        @Override
//                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                            if(isChecked){
//
//                                            }else{
//
//                                            }
//                                        }
//                                    });
                                }
                            } else {
                                myListData.add(userModelClass);
                            }
                        }

                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else if(type1.matches("remove")){

                DatabaseReference reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();
                Query query = reference.child("Users").orderByChild("role").equalTo("admin");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (myListData != null) {
                            myListData.clear();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserModelClass userModelClass = dataSnapshot.getValue(UserModelClass.class);
                            if (userModelClass.getIsEligibleForChat().matches("true")) {
                                myListData.add(userModelClass);
                                Log.d("tag", "checkingdata " + myListData.get(0).getUsername());

                                selectall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if(isChecked){

                                        }else{

                                        }
                                    }
                                });
                            }
                        }

                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//                DatabaseReference aref = db.child("chatheads").child("chats");
//                aref.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (myListData != null) {
//                            myListData.clear();
//                        }
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
////                            UserModelClass userModelClass = dataSnapshot.getValue(UserModelClass.class);
////                            if (userModelClass.getRole().matches("admin")) {
//                            UserModelClass userModelClass = new UserModelClass();//dataSnapshot.getValue(UserModelClass.class);
//                            userModelClass.setUsername(dataSnapshot.child("sendername").getValue(String.class));
//                            userModelClass.setId(dataSnapshot.child("senderid").getValue(String.class));
//                                myListData.add(userModelClass);
//                                Log.d("tag", "checkingdata " + myListData.get(0).getUsername());
//                            HashMap<String, String> main = new HashMap<>();
//                            main.put("sendername", dataSnapshot.child("sendername").getValue(String.class));
//                            main.put("senderid", dataSnapshot.child("senderid").getValue(String.class));
//
//                            selectall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                @Override
//                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                    if(isChecked){
////                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(dataSnapshot.child("senderid").getValue(String.class)).setValue(main);
//                                    }else{
//
//                                    }
//                                }
//                            });
//
////                            }
//
//
//                        }
//
//                        recyclerView.setAdapter(adapter);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

            }
        }else if(type.matches("group")){
            Log.d("tag", "checkpoint"+ type1+type+groupname);
            if(type1.matches("add")){
                DatabaseReference aref = db.child("Users");
                aref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (myListData != null) {
                            myListData.clear();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserModelClass userModelClass = dataSnapshot.getValue(UserModelClass.class);
//                            if (userModelClass.getRole().matches("admin")) {
                                myListData.add(userModelClass);
//                                Log.d("tag", "checkingdata " + myListData.get(0).getUsername());
                            mainchat main1 = new mainchat();
                            main1.setSenderid(dataSnapshot.child("id").getValue(String.class));
                            main1.setSendername(dataSnapshot.child("username").getValue(String.class));


                            selectall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if(isChecked){


                                        HashMap<String, String> data1 = new HashMap<>();
                                        data1.put("groupname", groupname);
                                        data1.put("topic", topic);
//                                        data.put("email", mylist.getEmail());
                                        for(int i = 0; i<myListData.size(); i++){
                                            int finalI = i;
                                            HashMap<String, String> data = new HashMap<>();
                                            data.put("username", myListData.get(i).getUsername());
                                            data.put("id", myListData.get(i).getId());
                                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(myListData.get(finalI).getId()).setValue(data);

                                                    }else{
                                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).setValue(data1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(myListData.get(finalI).getId()).setValue(data);

//                                                                Toast.makeText(context, mylist.getUsername()+"added to "+ groupname, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

//                                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("groupname").setValue(groupname).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(myListData.get(finalI).getId()).setValue(data);
//                                                }
//                                            });
                                        }
                                        Toast.makeText(addmember.this, "all users added successfully", Toast.LENGTH_SHORT).show();



                                    }else{
                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("groupslist").child(groupname)
//                                                        .child(main1.getSenderid()).removeValue();
                                                Toast.makeText(addmember.this, "all user removed from "+ groupname, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
                            });

//                            }

                        }

                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }else if(type1.matches("remove")){

                DatabaseReference aref = db.child("chatheads").child("groups").child("usergroups").child(groupname).child("users");
                aref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (myListData != null) {
                            myListData.clear();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String t = dataSnapshot.child("username").getValue(String.class);
                            UserModelClass userModelClass = dataSnapshot.getValue(UserModelClass.class);
//                            if (userModelClass.getRole().matches("admin")) {
                                myListData.add(userModelClass);


                            selectall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if(isChecked){
                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(addmember.this, "removed all users from "+ groupname, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else{

                                    }
                                }
                            });


//                            }


                        }

                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
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