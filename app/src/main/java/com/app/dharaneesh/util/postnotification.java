package com.app.dharaneesh.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class postnotification {
    private static final DatabaseReference usersRef =
            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().getRef().child("users");
    private static final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final APIService apiService =
            Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


    public static void sendNotification(String userId, Data data) {
//        final String token;
        if (data != null) {
            String token = null;
//            if (userId == "") {
                token = "/topics/allDevices";
//
//                Log.d("ttt", "sending to token: " + token);
//                Sender sender = new Sender(data, token);
//
//                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
//                    @Override
//                    public void onResponse(@NonNull Call<MyResponse> call,
//                                           @NonNull retrofit2.Response<MyResponse> response) {
//                        Log.d("ttt", "notification send: " + response.message());
//                    }
//                });
//                }
                Log.d("data", data.getBody()+data.time+data.title+data.isIsschedule());
                Log.d("ttt", "sending to token: " + token);
                Sender sender = new Sender(data, token);

                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MyResponse> call,
                                           @NonNull retrofit2.Response<MyResponse> response) {
                        Log.d("ttt", "notification send: " + response.message());
                    }

                    @Override
                    public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                        Log.d("ttt", "notification send error: " + t.getMessage());
                    }
                });
//            }

        }

    }

    public interface APIService {
        @Headers(
                {
                        "Content-Type:application/json",
                        "Authorization:key=AAAAC26x-OU:APA91bE6Bz5icB_nBV9djeawKnAruIeS9KQ8Qv9wFjMmzR1OlMh9b1LIqtUi83GjvdwKAtUj_bsj8G9uaO_M_dm_rKrD-4Bs39kplNBb1TU0BicvmvUgft3xNc3ZEO86possWwGKD06g"
                }
        )
        @POST("fcm/send")
        Call<MyResponse> sendNotification(@Body Sender body);
    }

    public static class Client {
        private static Retrofit retrofit = null;

        public static Retrofit getClient(String url) {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
            }
            return retrofit;
        }
    }

    private static class Sender {
        private final Data data;
        private final String to;

        Sender(Data data, String to) {
            this.data = data;
            this.to = to;
        }

        public Data getData() {
            return data;
        }

        public String getTo() {
            return to;
        }
    }


    public static class MyResponse {
        public int success;
    }

    public static class Data {

        public static final int TYPE_MESSAGE = 1;

        private String senderID;
        private String title;
        private String body;
        private String imageUrl;
        private String destinationID;
        private int type;
        private boolean isschedule;
        private String time;

        public Data(Map<String, String> dataMap) {

            if (dataMap.containsKey("senderID")) {
                this.senderID = dataMap.get("senderID");
            }
            if (dataMap.containsKey("title")) {
                this.title = dataMap.get("title");
            }
            if (dataMap.containsKey("body")) {
                this.body = dataMap.get("body");
            }
            if (dataMap.containsKey("imageURL")) {
                this.imageUrl = dataMap.get("imageURL");
            }

            if (dataMap.containsKey("destinationID")) {
                this.destinationID = dataMap.get("destinationID");
            }

            if (dataMap.containsKey("type")) {
                this.type = Integer.parseInt(dataMap.get("type"));
            }
            if (dataMap.containsKey("isschedule")) {
                this.isschedule = Boolean.parseBoolean(dataMap.get("isschedule"));
            }
            if (dataMap.containsKey("time")) {
                this.time = dataMap.get("time");
            }

        }

        public Data(String senderID, String title, String body, String imageUrl, boolean isschedule, String destinationID, String time, int type) {
            this.senderID = senderID;
            this.title = title;
            this.body = body;
            this.imageUrl = imageUrl;
            this.destinationID = destinationID;
            this.time = time;
            this.isschedule = isschedule;
            this.type = type;
        }


        public String getSenderID() {
            return senderID;
        }

        public void setSenderID(String senderID) {
            this.senderID = senderID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getDestinationID() {
            return destinationID;
        }

        public void setDestinationID(String destinationID) {
            this.destinationID = destinationID;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public static int getTypeMessage() {
            return TYPE_MESSAGE;
        }

        public boolean isIsschedule() {
            return isschedule;
        }

        public void setIsschedule(boolean isschedule) {
            this.isschedule = isschedule;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}

