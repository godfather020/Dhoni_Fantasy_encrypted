package com.app.dharaneesh.support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
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

import java.util.ArrayList;
import java.util.Date;

public class messagescreen extends AppCompatActivity {
    String userId_receiver; // userId of other user who'll receive the text // Or the user id of profile currently opened
    String userId_sender;  // current user id
    ImageView profile;
    String senderchat, receiverchat, role, name1, messageparent, chattype;
    RecyclerView chatrecycle;
    EditText enterchat;
    ImageView send;
    DatabaseReference uref, mref;
    ArrayList<Messages> messagesArrayList;
    ImageView ivDelChat;
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
    ImageView ivGoBack;
    String username;
    long TIME = 1 * 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagescreen);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("firUserId", "No name defined");
        role = prefs.getString("role", "no");

        chatbox = findViewById(R.id.send_relative_layout);
        ivGoBack = findViewById(R.id.ivGoBack);
        messageparent = getIntent().getStringExtra("messageparent");


        if (role.matches("user")) {
            DatabaseReference sref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatstatus");
            sref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String status = snapshot.child("status").getValue(String.class);
                        if (status.matches("stopped")) {
                            chatbox.setVisibility(View.GONE);
                        } else if (status.matches("started")) {
                            chatbox.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        initObjects();
        getCurrentUserData();
        listenToUserStatus();
        clearUnread();

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setNavigationOnClickListener(v->finish());

        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        messagesArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messagesArrayList);

        senderchat = FirebaseAuth.getInstance().getUid() + userId_receiver;
        receiverchat = userId_receiver + FirebaseAuth.getInstance().getUid();
        Log.d("TAG", "checkid2  " + userId_receiver + "  " + FirebaseAuth.getInstance().getUid());

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
        ivDelChat = findViewById(R.id.ivDelChat);


        if (role.matches("admin")) {
//            toolbar.inflateMenu(R.menu.messageoption);
//            mymenu = toolbar.getMenu();
            ivDelChat.setVisibility(View.VISIBLE);

            ivDelChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference chref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chats").child(senderchat);
                    chref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DatabaseReference chref1 = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chats").child(receiverchat);
                            chref1.removeValue();
//                                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(userId_receiver).removeValue();
                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("adminchats").child(userId_sender).child(messageparent).removeValue();
                            Toast.makeText(messagescreen.this, "Chat deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });

//            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//
//                    if(item.getItemId() == R.id.deletechat){
//
//                    }
//
//                    return true;
//                }
//            });
        }


        DatabaseReference chrefm = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chats").child(senderchat).child("messages");


        FirebaseDatabase db = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setEnabled(false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                }, TIME);

                String emessage = enterchat.getText().toString();

                if (emessage.matches("")) {

                } else {
                    Date date = new Date();
                    Messages messages = new Messages(emessage, userId_sender, date.getTime(), false);
                    db.getReference().child("chats").child(senderchat).child("messages").push()
                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseDatabase db = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
                                db.getReference().child("chats").child(receiverchat).child("messages").push()
                                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            enterchat.setText("");

                                            //                                          unread count codes
                                            Log.i("senderid", userId_receiver);
                                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("Users").child(userId_receiver).child("role").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    Log.i("role", String.valueOf(snapshot.getValue()));
                                                    String role = (String) snapshot.getValue();
                                                    if (role.matches("admin")) {
                                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("adminchats").child(userId_receiver).child(FirebaseAuth.getInstance().getUid()).child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot snapshot) {
                                                                if (snapshot.getValue() == null) {
                                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("adminchats").child(userId_receiver).child(FirebaseAuth.getInstance().getUid()).child("unreadCount")
                                                                            .setValue(1);
                                                                } else {
                                                                    int unreadCount = ((Long) snapshot.getValue()).intValue();
                                                                    unreadCount = unreadCount + 1;
                                                                    Log.i("unreadCount", String.valueOf(snapshot.getValue()) + FirebaseAuth.getInstance().getUid() + userId_receiver);

                                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("adminchats").child(userId_receiver).child(FirebaseAuth.getInstance().getUid()).child("unreadCount")
                                                                            .setValue(unreadCount);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                            }
                                                        });
                                                    } else {
                                                        username = "";
                                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {
                                                                            String name = snapshot.child("username").getValue(String.class);
                                                                            username = name;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(userId_receiver).child(FirebaseAuth.getInstance().getUid()).child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot snapshot) {
                                                                if (snapshot.getValue() == null) {
                                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(userId_receiver).child(FirebaseAuth.getInstance().getUid()).child("unreadCount")
                                                                            .setValue(1);
                                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(userId_receiver).child(FirebaseAuth.getInstance().getUid()).child("senderid")
                                                                            .setValue(FirebaseAuth.getInstance().getUid());
                                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(userId_receiver).child(FirebaseAuth.getInstance().getUid()).child("sendername")
                                                                            .setValue(username);
                                                                } else {
                                                                    int unreadCount = ((Long) snapshot.getValue()).intValue();
                                                                    unreadCount = unreadCount + 1;
                                                                    Log.i("unreadCount", String.valueOf(snapshot.getValue()) + ',' + FirebaseAuth.getInstance().getUid() + ',' + userId_receiver);

                                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(userId_receiver).child(FirebaseAuth.getInstance().getUid()).child("unreadCount")
                                                                            .setValue(unreadCount);
                                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(userId_receiver).child(FirebaseAuth.getInstance().getUid()).child("senderid")
                                                                            .setValue(FirebaseAuth.getInstance().getUid());
                                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(userId_receiver).child(FirebaseAuth.getInstance().getUid()).child("sendername")
                                                                            .setValue(username);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
//                                          unread count codes

                                            sendCloudNotification(emessage);


                                            if (role.matches("admin")) {
                                                uref = db.getReference().child("Users").child(userId_receiver);
                                            } else if (role.matches("user")) {
                                                uref = db.getReference().child("Users").child(userId_sender);
                                            }
//                                                db.getReference().child("Users").child(userId_receiver)
                                            uref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        name1 = snapshot.child("username").getValue(String.class);
//                                                                String pp = snapshot.child("profilepic").getValue(String.class);
                                                        String senderid = snapshot.child("id").getValue(String.class);
                                                        Log.d("TAG", "checksender" + name1);
//                                                                Log.d("tag", "checkprfilepic"+pp);

                                                        if (role.matches("admin")) {
                                                            mref = db.getReference().child("chatheads").child("adminchats").child(userId_sender).child(userId_receiver);
                                                        }
                                                        if (role.matches("user")) {
                                                            mref = db.getReference().child("chatheads").child("adminchats").child(userId_receiver).child(userId_sender);
                                                            Log.d("TAG", "checksender2" + mref);
                                                        }


                                                        mref.child("sendername").setValue(name1);
                                                        mref.child("chattype").setValue(getIntent().getStringExtra("chattype"));
                                                        mref.child("senderid").setValue(senderid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()) {
                                                                    Log.d("TAG", "checksender1" + task.getException().getMessage());
                                                                } else {

                                                                }

                                                                db.getReference().child("Users").child(userId_sender)
                                                                        .addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()) {
                                                                                    String name = snapshot.child("name").getValue(String.class);
                                                                                    String pp = snapshot.child("profilepic").getValue(String.class);
                                                                                    String senderid = snapshot.child("current").getValue(String.class);
                                                                                    Log.d("tag", "checkprfilepic" + pp);

                                                                                    if (role != "admin") {


//                                                                                                    DatabaseReference mref = db.getReference().child("chatheads").child("adminchats").child(userId_sender);
//                                                                                                    mref.child("sendername").setValue(name);
////                                                                                                mref.child("senderprofilepic").setValue(pp);
//                                                                                                    mref.child("senderid").setValue(senderid);
                                                                                    }

                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });


                                                            }
                                                        });

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                        }
                                    }
                                });
                            }
                        }
                    });

                }

            }
        });

        chrefm.addChildEventListener(new ChildEventListener() {
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

    //                                          unread count codes
    private void clearUnread() {
        if (role.matches("admin")) {
            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("adminchats").child(FirebaseAuth.getInstance().getUid()).child(userId_receiver).child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                    } else {
                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("adminchats").child(FirebaseAuth.getInstance().getUid()).child(userId_receiver).child("unreadCount")
                                .setValue(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(FirebaseAuth.getInstance().getUid()).child(userId_receiver).child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                    } else {
                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(FirebaseAuth.getInstance().getUid()).child(userId_receiver).child("unreadCount")
                                .setValue(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    //                                          unread count codes
    private void initObjects() {

        sharedPreferences = getSharedPreferences("shardPref", Context.MODE_PRIVATE);

        final Intent intent = getIntent();

        if (intent.hasExtra("messagingBundle")) {

            final Bundle messagingBundle = intent.getBundleExtra("messagingBundle");

            userId_receiver = messagingBundle.getString("userid");
            Log.d("TAG", "checkids" + userId_receiver);


            if (Build.VERSION.SDK_INT < 26) {
                BadgeUtil.decrementBadgeNum(this);
            }

        } else {
            userId_receiver = intent.getStringExtra("userid");
            Log.d("TAG", "checkids1" + userId_receiver);

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

        userId_sender = FirebaseAuth.getInstance().getUid();//FirebaseAuth.getInstance().getCurrentUser().getUid();

//        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("User")
        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("Users")
                .child(userId_sender).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                DataSnapshot dataSnapshot;
                if (task.isSuccessful() && (dataSnapshot = task.getResult()) != null) {
                    currentUsername = dataSnapshot.child("username").getValue(String.class);
//                    Toast.makeText(messagescreen.this, "checkname"+currentUsername, Toast.LENGTH_SHORT).show();
                    name1 = dataSnapshot.child("username").getValue(String.class);
//                    currentImageURL = dataSnapshot.child("profilepic").getValue(String.class);
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
        DatabaseReference ref1 = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("Users").child(userId_receiver);
        ref1.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                    if (dataSnapshot.hasChild("username")) {
                        String username;
                        if ((username = dataSnapshot.child("username").getValue(String.class)) != null) {
                            tvUsername.setText(username);
                        }
                    }

                }

            }
        });

    }

    void sendCloudNotification(String message) {
        Log.d("ttt", "sending cloud notificaiton");
        String name = null;
        if (data == null) {
            if (role.matches("admin")) {
                name = "admin";
            } else if (role.matches("user")) {
                name = name1;//name1;
            }
            Log.d("TAG", "checkingmessage" + name);
            data = new CloudMessagingNotificationsSender.Data(
                    userId_receiver,
                    "New message from: " + name,
                    message,
                    userId_receiver,
                    false,
                    "",
                    CloudMessagingNotificationsSender.Data.TYPE_MESSAGE);

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