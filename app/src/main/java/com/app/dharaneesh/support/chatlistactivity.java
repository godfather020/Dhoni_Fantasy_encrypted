package com.app.dharaneesh.support;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.app.dharaneesh.R;
import com.app.dharaneesh.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class chatlistactivity extends AppCompatActivity {
    private Toolbar m;
    private ViewPager myviewpager;
    private TabLayout t;
    private Tsbsaccessor tabs;
    private FirebaseUser currentuser;
    private FirebaseAuth mauth;
    private DatabaseReference ref;
    String role;
    Menu mymenu;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlistactivity);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        builder = new AlertDialog.Builder(this);


        mauth = FirebaseAuth.getInstance();
        currentuser = mauth.getCurrentUser();
        ref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();
        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("firUserId", "No name defined");
        role = prefs.getString("role", "no");

        m = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(m);
        getSupportActionBar().setTitle("Chat Window");

        myviewpager = (ViewPager) findViewById(R.id.main_tabs_pager);
        tabs = new Tsbsaccessor(getSupportFragmentManager());
        myviewpager.setAdapter(tabs);

        t = (TabLayout) findViewById(R.id.main_tabs);
        t.setupWithViewPager(myviewpager);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (currentuser == null) {
            loginactivity();
        } else {
            VerifyUserExistence();
        }
    }

    private void VerifyUserExistence() {
        String UserId = mauth.getCurrentUser().getUid();
        ref.child("Users").child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())) {
                    Toast.makeText(chatlistactivity.this, "Welcome!!!", Toast.LENGTH_LONG).show();     //User is previous user
                }
//               else
//               {
//                   settingsactivity();//new user
//               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (role.matches("admin")) {
            getMenuInflater().inflate(R.menu.options_menu, menu);
            mymenu = menu;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.creategroup) {
            Requestnewgroup();
        }
        if (item.getItemId() == R.id.turnoffchat) {
            MenuItem i = mymenu.findItem(R.id.turnoffchat);
            MenuItem j = mymenu.findItem(R.id.turnonchat);
            stopchat(i, j);
        }
        if (item.getItemId() == R.id.turnonchat) {
            MenuItem i = mymenu.findItem(R.id.turnoffchat);
            MenuItem j = mymenu.findItem(R.id.turnonchat);
            startchat(i, j);
        }
        if (item.getItemId() == R.id.addchat) {
            Intent go = new Intent(getApplicationContext(), addmember.class);
            go.putExtra("type", "addadmin");
            go.putExtra("type1", "add");
            startActivity(go);
        }
        if (item.getItemId() == R.id.removechat) {
            Intent go = new Intent(getApplicationContext(), addmember.class);
            go.putExtra("type", "addadmin");
            go.putExtra("type1", "remove");
            startActivity(go);
        }
        if (item.getItemId() == R.id.removeAllChat) {
            builder.setTitle("Are you sure you want to delete?")
                    .setMessage("All chat will be deleted after clicking yes")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);

                            database.getReference("chatheads").child("adminchats")
                                    .removeValue();
                            database.getReference("chatheads").child("groups")
                                    .removeValue();
                            database.getReference("chats")
                                    .removeValue();
                            database.getReference("groupchats")
                                    .removeValue();

                            Toast.makeText(chatlistactivity.this, "Chats Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();

        }
        return true;
    }

    private void startchat(MenuItem i, MenuItem j) {
        DatabaseReference sref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatstatus").child("status");
        sref.setValue("started").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                i.setVisible(true);
                j.setVisible(false);
            }
        });
    }

    private void stopchat(MenuItem i, MenuItem j) {
        DatabaseReference sref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatstatus").child("status");
        sref.setValue("stopped").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                i.setVisible(false);
                j.setVisible(true);
            }
        });
    }

    private void Requestnewgroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(chatlistactivity.this, R.style.Widget_AppCompat_ActionBar);
        builder.setTitle("Enter Group name:-");
        final EditText groupnamefield = new EditText(chatlistactivity.this);
        builder.setView(groupnamefield);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupname = groupnamefield.getText().toString();
                if (TextUtils.isEmpty(groupname)) {
                    Toast.makeText(chatlistactivity.this, "Please write group name", Toast.LENGTH_LONG).show();
                } else {
                    createnewgroup(groupname);
                }
            }


        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();


    }

    private void createnewgroup(final String groupname) {
        long time = System.currentTimeMillis();
        HashMap<String, String> data = new HashMap<>();
        data.put("groupname", groupname);
        data.put("chattype", "group");
        data.put("topic", String.valueOf(time));
        ref.child("chatheads").child("groups").child("admingroups").push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(chatlistactivity.this, groupname + "group is created successfully", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loginactivity() {

//        Intent LoginIntent=new Intent(chatlistactivity.this,Login.class);
//        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(LoginIntent);
//        finish();
    }

    private void settingsactivity() {

//        Intent sIntent=new Intent(MainActivity.this,settings.class);
//        sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(sIntent);
//        finish();
    }

}
