package com.app.dharaneesh.support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dharaneesh.R;
import com.app.dharaneesh.adapter.ChatAdapter;
import com.app.dharaneesh.models.Messages;
import com.app.dharaneesh.util.BadgeUtil;
import com.app.dharaneesh.util.CloudMessagingNotificationsSender;
import com.app.dharaneesh.util.Constant;
import com.app.dharaneesh.util.GlobalVariables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class groupmessagescreen extends AppCompatActivity {

    String userId_receiver; // userId of other user who'll receive the text // Or the user id of profile currently opened
    String userId_sender, groupname, groupparent, topic;  // current user id
    ImageView profile;
    String senderchat, receiverchat, role, chattype, nmessage;
    RecyclerView chatrecycle;
    EditText enterchat;
    ImageView send;
    DatabaseReference uref, mref;
    ArrayList<Messages> messagesArrayList;
    //    private Ada
    private ImageView ivStatus;
    private TextView tvUsername;
    private ChatAdapter chatAdapter;
    private String currentUsername, currentImageURL;
    private CloudMessagingNotificationsSender.Data data;
    private NotificationManager notificationManager;
    private SharedPreferences sharedPreferences;
    RelativeLayout chatbox;
    Menu mymenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmessagescreen);

        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("firUserId", "No name defined");
        role = prefs.getString("role", "no");


        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        chatbox = findViewById(R.id.send_relative_layout);
        groupname = getIntent().getStringExtra("userid");
        groupparent = getIntent().getStringExtra("groupparent");
        topic = getIntent().getStringExtra("topic");
        long time = System.currentTimeMillis();

        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(groupmessagescreen.this, "welcome", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("tag", "checknotificai" + task.getException().getMessage());
                }
            }
        });


        if (role.matches("user")) {
            chatbox.setVisibility(View.GONE);
//            DatabaseReference sref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatstatus");
//            sref.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if(snapshot.exists()){
//                        String status = snapshot.child("status").getValue(String.class);
//                        if(status.matches("stopped")){
//                            chatbox.setVisibility(View.GONE);
//                        }else if(status.matches("started")){
//                            chatbox.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
        }


        initObjects();
        getCurrentUserData();
        listenToUserStatus();
        clearUnread();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        if (role.matches("admin")) {
            toolbar.inflateMenu(R.menu.groupmenu);
            mymenu = toolbar.getMenu();
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.addmember) {
                        addmember();
                    }
                    if (item.getItemId() == R.id.removemember) {
                        removemember();

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
                    if (item.getItemId() == R.id.deletegroup) {

//                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("groupslist").child(groupname).removeValue();
                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("admingroups").child(groupparent).removeValue();
//                                    Intent go = new Intent(groupmessagescreen.this, chatlistactivity.class);
//                                    startActivity(go);
                                    finish();
                                } else {
                                    Log.d("tag", "checkerror" + task.getException().getMessage());
                                }
                            }
                        });


                    }
                    if (item.getItemId() == R.id.deletechat) {
                        DatabaseReference chref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("groupchats").child(groupname);
                        chref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(groupmessagescreen.this, "Chat deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    return true;
                }
            });
        }

        messagesArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messagesArrayList);

        senderchat = userId_sender + userId_receiver;
        receiverchat = userId_receiver + userId_sender;
        chatrecycle = findViewById(R.id.recycler_view_messages_record);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatrecycle.setLayoutManager(linearLayoutManager);
        chatrecycle.setAdapter(chatAdapter);
        enterchat = findViewById(R.id.et_chat);
        send = findViewById(R.id.iv_send_button);
        profile = findViewById(R.id.iv_user_image);
        ivStatus = findViewById(R.id.iv_user_status_message_view);
        tvUsername = findViewById(R.id.tv_profile_user_name);
        tvUsername.setText(groupname);


        DatabaseReference chref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("groupchats").child(groupname).child("messages");


        FirebaseDatabase db = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emessage = enterchat.getText().toString();

                if (emessage.matches("")) {

                } else {
                    nmessage = enterchat.getText().toString();
                    Date date = new Date();
                    Messages messages = new Messages(emessage, userId_sender, date.getTime(), false);
                    db.getReference().child("groupchats").child(groupname).child("messages").push()
                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

    //                          unread count codes

                                    Log.i("senderid", userId_receiver + "," + groupname);

                                    DatabaseReference usersRef = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users");
                                    ValueEventListener eventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                Log.i("unreadc", ds.getValue() + "," + ds.child("groupname").getValue());
                                                if (ds.child("unreadCount").getValue() == null) {
                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(String.valueOf(ds.child("id").getValue())).child("unreadCount")
                                                            .setValue(1);
                                                } else {
                                                    int unreadCount = ((Long) ds.child("unreadCount").getValue()).intValue();
                                                    unreadCount = unreadCount + 1;
                                                    Log.i("unreadCount", String.valueOf(ds.child("unreadCount").getValue()) + FirebaseAuth.getInstance().getUid() + userId_receiver);

                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(String.valueOf(ds.child("id").getValue())).child("unreadCount")
                                                            .setValue(unreadCount);
                                                }
                                            }
                                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(FirebaseAuth.getInstance().getUid()).child("unreadCount")
                                                    .setValue(null);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    };
                                    usersRef.addListenerForSingleValueEvent(eventListener);
    //                          unread count codes

//                                if(role.matches("admin")) {
                                new Notify().execute();
//                                    sendnotification1(enterchat.getText().toString());
                                enterchat.setText("");

//                                }

//                                sendnotification(emessage);

//                                FirebaseDatabase db = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
//                                db.getReference().child("chats").child("groupchats").child(groupname).child("messages").push()
//                                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//
//                                            sendCloudNotification(emessage);
//
//                                            enterchat.setText("");

//                                            if(role.matches("admin")){
//                                                uref = db.getReference().child("Users").child(userId_receiver);
//                                            }else if(role.matches("user")){
//                                                uref = db.getReference().child("Users").child(userId_sender);
//                                            }

//                                                db.getReference().child("Users").child(userId_receiver)
//                                            uref.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.exists()) {
//                                                        String name = snapshot.child("username").getValue(String.class);
////                                                                String pp = snapshot.child("profilepic").getValue(String.class);
//                                                        String senderid = snapshot.child("id").getValue(String.class);
////                                                                Log.d("tag", "checkprfilepic"+pp);
//
//                                                        if(role.matches("admin")){
//                                                            mref = db.getReference().child("chatheads").child("adminchats").child(userId_receiver);
//                                                        }else if(role.matches("user")){
//                                                            mref = db.getReference().child("chatheads").child("adminchats").child(userId_sender);
//                                                        }
//
//                                                        mref.child("sendername").setValue(name);
//                                                        mref.child("chattype").setValue(getIntent().getStringExtra("chattype"));
//                                                        mref.child("senderid").setValue(senderid).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                                db.getReference().child("Users").child(userId_sender)
//                                                                        .addValueEventListener(new ValueEventListener() {
//                                                                            @Override
//                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                                if (snapshot.exists()) {
//                                                                                    String name = snapshot.child("name").getValue(String.class);
//                                                                                    String pp = snapshot.child("profilepic").getValue(String.class);
//                                                                                    String senderid = snapshot.child("current").getValue(String.class);
//                                                                                    Log.d("tag", "checkprfilepic" + pp);
//
//                                                                                    if (role != "admin") {
//
//
////                                                                                                    DatabaseReference mref = db.getReference().child("chatheads").child("adminchats").child(userId_sender);
////                                                                                                    mref.child("sendername").setValue(name);
//////                                                                                                mref.child("senderprofilepic").setValue(pp);
////                                                                                                    mref.child("senderid").setValue(senderid);
//                                                                                    }
//
//                                                                                }
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                            }
//                                                                        });
//
//
//                                                            }
//                                                        });
//
//                                                    }
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });

//
//
//                                        }
//                                    }
//                                });
                            }
                        }
                    });

                }

            }
        });

        chref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d("TAG", "child added: " + snapshot.getKey());

                if (snapshot.exists()) {
                    Messages messages;
                    if ((messages = snapshot.getValue(Messages.class)) != null) {

                        Log.d("TAG", "message added: " + messages.getMessage());

                        messagesArrayList.add(messages);
                        chatAdapter.notifyItemInserted(messagesArrayList.size());
                        chatrecycle.scrollToPosition(messagesArrayList.size() - 1);

                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        chref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot :snapshot.getChildren()){
//                    Messages messages = dataSnapshot.getValue(Messages.class);
//                    Log.d("TAG", "checkmessage"+messages);
//
////                    Log.d("TAG", "checkmessage"+messagesArrayList.get(1));
//                }
//                chatAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }


    public class Notify extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=AAAAC26x-OU:APA91bE6Bz5icB_nBV9djeawKnAruIeS9KQ8Qv9wFjMmzR1OlMh9b1LIqtUi83GjvdwKAtUj_bsj8G9uaO_M_dm_rKrD-4Bs39kplNBb1TU0BicvmvUgft3xNc3ZEO86possWwGKD06g");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();

                json.put("to", "/topics/" + topic);

                JSONObject info = new JSONObject();
                info.put("title", "message from:" + groupname);   // Notification title
                info.put("body", nmessage); // Notification body

                json.put("notification", info);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();

            } catch (Exception e) {
                Log.d("Error", "" + e.getMessage());
            }
            return null;
        }
    }


    private void addmember() {
        Intent go = new Intent(getApplicationContext(), addmember.class);
        go.putExtra("type", "group");
        go.putExtra("groupname", groupname);
        go.putExtra("type1", "add");
        go.putExtra("topic", topic);
        startActivity(go);


    }

    private void removemember() {

        Intent go = new Intent(getApplicationContext(), addmember.class);
        go.putExtra("type", "group");
        go.putExtra("type1", "remove");
        go.putExtra("groupname", groupname);
        startActivity(go);

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

    //                                          unread count codes
    private void clearUnread() {
            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(FirebaseAuth.getInstance().getUid()).child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                    } else {
                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(FirebaseAuth.getInstance().getUid()).child("unreadCount")
                                .setValue(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    }
    //                                          unread count codes


    private void initObjects() {

        sharedPreferences = getSharedPreferences("shardPref", Context.MODE_PRIVATE);

        final Intent intent = getIntent();

        if (intent.hasExtra("messagingBundle")) {

            final Bundle messagingBundle = intent.getBundleExtra("messagingBundle");

            userId_receiver = messagingBundle.getString("userid");

            if (Build.VERSION.SDK_INT < 26) {
                BadgeUtil.decrementBadgeNum(this);
            }

        } else {
            userId_receiver = intent.getStringExtra("userid");
        }

        sharedPreferences.edit()
                .putString("currentMessagingUserId", userId_receiver).apply();

        if (GlobalVariables.getMessagesNotificationMap() != null) {

            final String notificationIdentifier = userId_receiver +
                    CloudMessagingNotificationsSender.Data.TYPE_MESSAGE;

            if (GlobalVariables.getMessagesNotificationMap().containsKey(notificationIdentifier)) {
                Log.d("ttt", "removing: " + notificationIdentifier);

                if (notificationManager == null)
                    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.cancel(GlobalVariables.getMessagesNotificationMap()
                        .get(notificationIdentifier));

                GlobalVariables.getMessagesNotificationMap().remove(notificationIdentifier);
            }
        }


    }

    void getCurrentUserData() {

        userId_sender = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("user")
                .child(userId_sender).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                DataSnapshot dataSnapshot;
                if (task.isSuccessful() && (dataSnapshot = task.getResult()) != null) {
                    currentUsername = dataSnapshot.child("name").getValue(String.class);
                    currentImageURL = dataSnapshot.child("profilepic").getValue(String.class);
                }

            }
        });

    }

    private void listenToUserStatus() {

        DatabaseReference ref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("users").child(userId_receiver);

        ref.child("status").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ivStatus.setImageResource(snapshot.getValue(Boolean.class)
                            ? R.drawable.online_status : R.drawable.offline_status);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                DataSnapshot dataSnapshot;
                if (task.isSuccessful() && (dataSnapshot = task.getResult()) != null) {

                    if (dataSnapshot.hasChild("profilepic")) {
                        String imageUrl;
                        if ((imageUrl = dataSnapshot.child("profilepic").getValue(String.class)) != null && !imageUrl.isEmpty()) {
//                            Picasso.get().load(imageUrl).fit().centerCrop().into(profile);
                        } else {
//                            Picasso.get().load(R.drawable.dprofile).fit().centerCrop().into(profile);
                        }
                    }
                    if (dataSnapshot.hasChild("name")) {
                        String username;
                        if ((username = dataSnapshot.child("name").getValue(String.class)) != null) {

                        }
                    }

                }

            }
        });

    }

    void sendCloudNotification(String message) {
        Log.d("ttt", "sending cloud notificaiton");
        if (data == null) {
            data = new CloudMessagingNotificationsSender.Data(
                    userId_sender,
                    "New message from: " + (currentUsername != null ? currentUsername : " User"),
                    message,
                    userId_receiver,
                    false,
                    "",
                    CloudMessagingNotificationsSender.Data.TYPE_MESSAGE
            );

        } else {
            data.setBody(message);
        }

        CloudMessagingNotificationsSender.sendNotification(userId_receiver, data);

    }

    @Override
    protected void onPause() {
        super.onPause();

        sharedPreferences.edit().putBoolean("isPaused", true).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (userId_receiver != null) {
            sharedPreferences.edit()
                    .putString("currentMessagingUserId", userId_receiver).apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sharedPreferences.edit()
                .remove("isPaused")
                .remove("currentMessagingUserId").apply();


    }


}