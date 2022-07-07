package com.app.dharaneesh.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

import androidx.annotation.NonNull;

import com.app.dharaneesh.MatchDataModel;
import com.app.dharaneesh.OpenDataActivity;
import com.app.dharaneesh.R;
import com.app.dharaneesh.notification.MyFirebaseMessaging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "ganesh";
    String body, title;
    ArrayList<String> slist;
    public ArrayList<Long> scheduledTimeL = new ArrayList<>();
    public ArrayList<String> scheduleTitle = new ArrayList<>();
    ArrayList<MatchDataModel> matchDataModelsArr;
    MatchDataModel matchDataModel;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, OpenDataActivity.class);

        Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show();

        //MyFirebaseMessaging myFirebaseMessaging = new MyFirebaseMessaging();

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(OpenDataActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        title = intent.getStringExtra("title");
        Log.d("title", title);
        body = intent.getStringExtra("body");
        //Long time = intent.getLongExtra("time", 0);
        slist = new ArrayList<>();

        //myFirebaseMessaging.scheduleAlarm(String.valueOf(time) , title, "");

        /*FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("schedule")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        slist.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                                MatchDataModel matchDataModel = snapshot1.getValue(MatchDataModel.class);
                                String key = snapshot1.getKey();
                                slist.add(key);

                            }
                            Log.d("tag", "checkmatch" + slist.size());
                            for (int i = 0; i < slist.size(); i++) {
                                int j = i;
                                Log.e("thisIsTheMain", slist.get(j) + "");
                                if (System.currentTimeMillis() > Long.parseLong(slist.get(j))) {

                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("schedule").child(slist.get(j))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    MatchDataModel model = snapshot.getValue(MatchDataModel.class);

                                                    String keyy = snapshot.child("keys").getValue(String.class);
                                                    String img = snapshot.child("postimg").getValue(String.class);

                                                    Log.d("tag", "checkmatch1" + img);
                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("Match").child(keyy).child("postimg").setValue(img)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference()
                                                                            .child("schedule").child(slist.get(j)).removeValue();

                                                                    //Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show();

                                                                    //myFirebaseMessaging.scheduleAlarm(String.valueOf(myFirebaseMessaging.alarmTimeArr.get(j)), myFirebaseMessaging.alarmTitle.get(j), myFirebaseMessaging.alarmBody.get(j));
                                                                }
                                                            });*/

                                               /* }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });*/
                               // }


                           // }

                        //}
                   /* }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_MUTABLE);

        Notification.Builder builder = new Notification.Builder(context);

        Notification notification = builder.setContentTitle("Admin added a team photo")
                .setContentText(body)
                .setTicker("New Message Alert!")
                .setSmallIcon(R.mipmap.abhi_icon)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notification);

    }

}
