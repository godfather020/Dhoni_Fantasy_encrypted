package com.app.dharaneesh.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new AppLifeCycleTracker());
    }


    class AppLifeCycleTracker implements ActivityLifecycleCallbacks {

        private int numStarted = 0;
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            if(numStarted == 0){
                updateUserStatus(true);
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            numStarted--;
            if(numStarted == 0){
                updateUserStatus(false);
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }

    private void updateUserStatus(boolean isOnline){


        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {

            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("users")
                    .child(currentUser.getUid()).child("status").setValue(isOnline);
        }


    }


}
