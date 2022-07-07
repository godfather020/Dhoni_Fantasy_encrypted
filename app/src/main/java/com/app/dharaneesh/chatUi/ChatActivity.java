package com.app.dharaneesh.chatUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.dharaneesh.R;
import com.app.dharaneesh.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView sendbtn;
    EditText msgedittext;
    String senderid = "", Receiverid = "", senderName = "";
    String Receiver_name;
    DatabaseReference rootref;
    private DatabaseReference Adduser_to_inbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("firUserId", "No name defined");
        String name = prefs.getString("username", "No name defined");

        senderid = id;
        senderName = name;
        Adduser_to_inbox = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();

        if (getIntent().getExtras() != null) {
            Receiverid = getIntent().getStringExtra("userid");
            Receiver_name = getIntent().getStringExtra("name");
        }


        rootref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();
        msgedittext = findViewById(R.id.msgedittext);
        sendbtn = findViewById(R.id.sendbtn);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendbtn) {
            if (!TextUtils.isEmpty(msgedittext.getText().toString())) {
                SendMessage(msgedittext.getText().toString());
                SendSeeeMessage();
                msgedittext.setText("");
            } else {
                Toast.makeText(ChatActivity.this, "Message are empty!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void SendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Constant.df.format(c);
        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;

        DatabaseReference reference = rootref.child("chat").child(senderid + "-" + Receiverid).push();
        final String pushid = reference.getKey();

        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", Receiverid);
        message_user_map.put("sender_id", senderid);
        message_user_map.put("chat_id", pushid);
        message_user_map.put("text", message);
        message_user_map.put("type", "text");
        message_user_map.put("status", "0");
        message_user_map.put("time", formattedDate);

        message_user_map.put("sender_name", senderName);

        message_user_map.put("timestamp", formattedDate);


        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);
        user_map.put(chat_user_ref + "/" + pushid, message_user_map);

        Log.d("message_user_map", message_user_map.toString());
        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
               // sendPushNotification("Sent a message");
                //if first message then set the visibility of whoops layout gone
                String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
                String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;

                HashMap sendermap = new HashMap<>();
                sendermap.put("rid", senderid);
                sendermap.put("name", senderName);
                sendermap.put("msg", message);
                sendermap.put("status", "0");
                sendermap.put("type", "0");
                sendermap.put("timestamp", -1 * System.currentTimeMillis());
                sendermap.put("date", formattedDate);
                sendermap.put("token_sender", "N/A");
                sendermap.put("token_reciever", "N/A");

                Log.d("sendermap", sendermap.toString());

                HashMap receivermap = new HashMap<>();
                receivermap.put("rid", Receiverid);
                receivermap.put("name", Receiver_name);
                receivermap.put("msg", message);
                receivermap.put("status", "1");
                receivermap.put("token_sender", "N/A");
                receivermap.put("token_reciever", "N/A");
                receivermap.put("timestamp", -1 * System.currentTimeMillis());
                receivermap.put("date", formattedDate);
                Log.d("sendermap", receivermap.toString());
                HashMap both_user_map = new HashMap<>();
                both_user_map.put(inbox_sender_ref, receivermap);
                both_user_map.put(inbox_receiver_ref, sendermap);
                Adduser_to_inbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
            }
        });
    }

    public void SendSeeeMessage() {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Constant.df.format(c);
        final String current_user_ref = "Seen" + "/" + Receiverid;
        DatabaseReference reference = rootref.child("Seen").child(Receiverid).push();
        final String pushid = reference.getKey();

        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("status", "0");

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);

        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }

}