package com.app.dharaneesh.notification;



import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.app.dharaneesh.MatchDataModel;
import com.app.dharaneesh.OpenDataActivity;
import com.app.dharaneesh.R;
import com.app.dharaneesh.util.AlarmReceiver;
import com.app.dharaneesh.util.CloudMessagingNotificationsSender;
import com.app.dharaneesh.util.Constant;
import com.app.dharaneesh.util.postnotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private String currentUID;
    private NotificationManager notificationManager;
    private int notificationNum;
    private SharedPreferences sharedPreferences;
    private static final String CHANNEL_ID = "dhoni";
    DatabaseReference reference;
    public ArrayList<Long> alarmTimeArr = new ArrayList<>();
    public ArrayList<String> alarmBody = new ArrayList<>();
    public ArrayList<String> alarmTitle = new ArrayList<>();
    public ArrayList<Long> scheduledTimeL = new ArrayList<>();
    public ArrayList<String> scheduleTitle = new ArrayList<>();
    ArrayList<String> dateTimeArr;
    ArrayList<String> titleArr;
    ArrayList<String> detailsArr;
    ArrayList<String> idArr;
    ArrayList<String> imgArr;
    ArrayList<String> matchTypeArr;
    ArrayList<String> isScheduled;
    ArrayList<String> scheduledTime;
    ArrayList<MatchDataModel> matchDataModelsArr;
    MatchDataModel matchDataModel;
    Long setTime = 0L;
    Long setTime1 = 0L;
    Long setTime2 = 0L;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("cloudMessagingToken").setValue(s);
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ttt", "Firebase Messaging serivice created");

    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        dateTimeArr = new ArrayList<>();
        titleArr = new ArrayList<>();
        idArr = new ArrayList<>();
        imgArr = new ArrayList<>();
        matchTypeArr = new ArrayList<>();
        detailsArr = new ArrayList<>();
        matchDataModelsArr = new ArrayList<>();
        isScheduled = new ArrayList<>();
        scheduledTime = new ArrayList<>();

        Log.d("ttt", "message reciceived");



        if (remoteMessage.getNotification() != null) {
            Log.d("tag", "checknot1");
            sendNotification1(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else {
            Log.d("tag", "checknot2");
            final postnotification.Data data = new postnotification.Data(remoteMessage.getData());
            if (data.isIsschedule()) {
                Log.d("tag", "checknot3");
                setAlarm();
                //scheduleAlarm(data.getTime(), data.getTitle(), data.getBody());
            } else {
                Log.d("tag", "checknot4");
                final CloudMessagingNotificationsSender.Data data1 = new CloudMessagingNotificationsSender.Data(remoteMessage.getData());
                if (data1.getSenderID().matches(FirebaseAuth.getInstance().getUid())) {
                    sendNotification1(data1.getTitle(), data1.getBody());
                }


            }
        }


//        if (remoteMessage.getData() != null || !remoteMessage.getData().equals("") || remoteMessage.getData().size()!= 0) {
////            Log.d("TAG", "checkingnotificiation" + remoteMessage.getNotification().getBody() + "  " + remoteMessage.getNotification().getTitle());
//
//            if(remoteMessage.getNotification()!=null) {
//                Log.d("tag", "check001"+remoteMessage.getNotification().getTitle() + remoteMessage.getNotification().getBody());
//                sendNotification1(remoteMessage.getNotification().getTitle(), "remoteMessage.getNotification().getBody()");
//            }
//            if(remoteMessage.getData() != null || !remoteMessage.getData().equals("")){
//                final postnotification.Data data = new postnotification.Data(remoteMessage.getData());
//                Log.d("TAG", "checkingnotificiation111" + data.getTitle() + " " + data.getBody());
//                if (data.isIsschedule()) {
//                    scheduleAlarm(data.getTime(), data.getTitle(), data.getBody());
//                } else {
//                    sendNotification1(data.getTitle(), data.getBody());
//                }
//            }
//
//
//        } else if(remoteMessage.getNotification()!=null || !remoteMessage.getNotification().equals("")) {
//            Log.d("tag", "checking00");
//            final CloudMessagingNotificationsSender.Data data = new CloudMessagingNotificationsSender.Data(remoteMessage.getData());
//            sendNotification1(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
////            if(data.getSenderID())
//            Log.d("TAG", "checknid" + data.isIsschedule());
//            if (data.getSenderID().matches(FirebaseAuth.getInstance().getUid())) {
//                sendNotification1(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
//            }
//        }
        if (currentUID == null) {
            currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        if (notificationManager == null) {
            notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences("shardPref", Context.MODE_PRIVATE);
        }


    }

    public static int minIndex (ArrayList<Long> list) {
        return list.indexOf (Collections.min(list)); }

    public void setAlarm(){

       Log.d("alarm", "Alarm");

        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("Match");

        myRedrence.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                matchDataModelsArr = new ArrayList<>();
                scheduledTimeL = new ArrayList<>();
                scheduleTitle = new ArrayList<>();

                matchDataModelsArr.clear();
                scheduledTimeL.clear();
                scheduleTitle.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    matchDataModel = dataSnapshot.getValue(MatchDataModel.class);
                    if (matchDataModel.getPosttitle() != null) {
                        matchDataModel.setId(dataSnapshot.getKey());
                        matchDataModelsArr.add(matchDataModel);
                        Long time;
                        Long currentTime = System.currentTimeMillis();
                        Long timeL = 0L;

                        if (matchDataModel.getIsScheduled().equals("true")) {

                            if (!matchDataModel.getScheduledTime().equals("not Scheduled")) {

                                time = Long.parseLong(matchDataModel.getScheduledTime());

                                if (time >= System.currentTimeMillis()) {

                                    scheduledTimeL.add(Long.parseLong(matchDataModel.getScheduledTime()));
                                    scheduleTitle.add(matchDataModel.getPosttitle());
                                    Log.d("itit", matchDataModel.getPosttitle());
                                }
                            }

                        }

                    }

                }

                Log.d("sssize", String.valueOf(scheduledTimeL.size()));

                int minI = 0;

                if (scheduledTimeL.size() != 0) {

                   minI = minIndex(scheduledTimeL);
                }

                for (int i = 0; i < scheduledTimeL.size(); i++) {
                    Log.d("sssetTime", String.valueOf(scheduledTimeL.get(i)));
                }

                if (scheduledTimeL.size() != 0) {

                    Log.d("sssetTimeMin", String.valueOf(System.currentTimeMillis()));

                    Collections.sort(scheduledTimeL);
                    //Arrays.sort(new ArrayList[]{scheduledTimeL});
                    /*scheduledTimeL.stream().sorted(new Comparator<Long>() {
                        @Override
                        public int compare(Long aLong, Long t1) {
                            return (int) (aLong-t1);
                        }
                    });*/

                    scheduleAlarm(scheduledTimeL, scheduleTitle, "");

                    Log.d("sssetTimeMin", String.valueOf(scheduledTimeL.get(minI)) + " " + scheduleTitle.get(minI));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void scheduleAlarm(ArrayList<Long> time, ArrayList<String> title, String body) {

       /* alarmTimeArr.add(t);
        alarmBody.add(body);
        alarmTitle.add(title);
*/
       /* int minValueIndex = minIndex(alarmTimeArr);*/

        Log.d("schdule", "scheduled");

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

        int min = 0;

        for(int i = 0; i < time.size(); i++)
        {

            //min = minIndex(time);

            long t = time.get(i);

            Log.d("i", String.valueOf(i));
            Log.d("isize", String.valueOf(time.size()));
            Log.d("isize", String.valueOf(time.get(i)));
            Log.d("itsize", String.valueOf(title.size()));
            Log.d("itsize", title.get(i));

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("title", "Admin added a photo");
            intent.putExtra("body", body);
            // Loop counter `i` is used as a `requestCode`
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);
            // Single alarms in 1, 2, ..., 10 minutes (in `i` minutes)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, t, pendingIntent);
            intentArray.add(pendingIntent);

            //time.remove(min);
            //title.remove(min);
            //Log.d("i", "remove");

            /*if (time.size() > 0){

                time.remove(min);
                title.remove(min);
                Log.d("i", "remove");
            }*/

        }


        /*Intent notificationIntent = new Intent(this, AlarmReceiver.class);
        notificationIntent.putExtra("title", title);
        notificationIntent.putExtra("body", body);
        notificationIntent.putExtra("time", scheduledTimeL.get(0));
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_MUTABLE);
*/


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);
        //Log.d("tag", "checktime" + t + "   00   " + cal.getTimeInMillis());
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, t, broadcast);


//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//
//          Intent alarami = new Intent(getApplicationContext(), AlarmReceiver.class);
//          alarami.putExtra("NOTIFICATION_TITLE", title);
//          alarami.putExtra("NOTIFICATION_MESSAGE", body);
//
//        PendingIntent alarmIntent=  PendingIntent.getBroadcast(getApplicationContext(), 0, alarami, 0);
//
//        Date scheduledTime = null;
//        try {
//            scheduledTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(time);
//            alarmManager.set(AlarmManager.RTC_WAKEUP,
//                    scheduledTime.getTime(),
//                    alarmIntent);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


    }

    private RemoteViews getCustomDesign(String title,
                                        String message) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,
                R.mipmap.gff);
        return remoteViews;
    }

    private void sendNotification1(String title, String body) {
        Log.d("tag", "check002" + title + "  0   " + body);

        Intent notificationIntent = new Intent(getApplicationContext(), OpenDataActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(OpenDataActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_MUTABLE);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        Notification notification = builder.setContentTitle(title)
                .setContentText(body)
                .setTicker("New Message Alert!")
                .setSmallIcon(R.mipmap.abhi_icon)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notification);


//        Intent intent
//                = new Intent(this, chatlistactivity.class);
//        // Assign channel ID
//        String channel_id = "notification_channel";
//        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
//        // the activities present in the activity stack,
//        // on the top of the Activity that is to be launched
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        // Pass the intent to PendingIntent to start the
//        // next Activity
//        PendingIntent pendingIntent
//                = PendingIntent.getActivity(
//                this, 0, intent,
//                PendingIntent.FLAG_MUTABLE);
//
//        // Create a Builder object using NotificationCompat
//        // class. This will allow control over all the flags
//        NotificationCompat.Builder builder
//                = new NotificationCompat
//                .Builder(getApplicationContext(),
//                channel_id)
//                .setSmallIcon(R.mipmap.gff)
//                .setAutoCancel(true)
//                .setVibrate(new long[]{1000, 1000, 1000,
//                        1000, 1000})
//                .setOnlyAlertOnce(true)
//                .setContentIntent(pendingIntent);
//
//        // A customized design for the notification can be
//        // set only for Android versions 4.1 and above. Thus
//        // condition for the same is checked here.
//        if (Build.VERSION.SDK_INT
//                >= Build.VERSION_CODES.JELLY_BEAN) {
//            builder = builder.setContent(
//                    getCustomDesign(title, body));
//        } // If Android Version is lower than Jelly Beans,
//        // customized layout cannot be used and thus the
//        // layout is set as follows
//        else {
//            builder = builder.setContentTitle(title)
//                    .setContentText(body)
//                    .setSmallIcon(R.mipmap.gff);
//        }
//        // Create an object of NotificationManager class to
//        // notify the
//        // user of events that happen in the background.
//        NotificationManager notificationManager
//                = (NotificationManager) getSystemService(
//                Context.NOTIFICATION_SERVICE);
//        // Check if the Android Version is greater than Oreo
//        if (Build.VERSION.SDK_INT
//                >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel
//                    = new NotificationChannel(
//                    channel_id, "admin",
//                    NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(
//                    notificationChannel);
//        }
//
//        notificationManager.notify(0, builder.build());
    }
}
