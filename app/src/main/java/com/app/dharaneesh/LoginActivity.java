package com.app.dharaneesh;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.app.dharaneesh.models.UserModel;
import com.app.dharaneesh.util.Constant;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    EditText txtMail, txtPassword;
    Button btnSubmit;
    private FirebaseAuth mAuth;
    String email, password;
    ProgressDialog pDialog;
    ArrayList<String> emailArr;
    ArrayList<String> userArr;
    public static String userLoginId = "";
    String userRole = "";
    ArrayList<UserModelClass> userListArr;
    Context context;
    String version;
    KProgressHUD kProgressHUD;
    TextView tvResetPasswordNow;
    LinearLayout llLoginActivityGoogle;
    private GoogleApiClient googleApiClient;
    public GoogleSignInClient getClient;
    private static final int RC_SIGN_IN = 9001;
    String name;
    String idToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
                    // User is signed in
                    // you could place other firebase code
                    //logic to save the user details to Firebase
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        getClient = GoogleSignIn.getClient(this, gso);

        txtMail = findViewById(R.id.txtLoginMail);
        txtPassword = findViewById(R.id.txtLoginPassword);
        btnSubmit = findViewById(R.id.btnLoginSubmit);
        tvResetPasswordNow = findViewById(R.id.tvResetPasswordNow);
        llLoginActivityGoogle = findViewById(R.id.llLoginActivityGoogle);

        kProgressHUD = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("firUserId", "No name defined");
        String role = prefs.getString("role", "no");
        String mailId = prefs.getString("username", "no");

        Log.d("TAG", "checkingpackage" + getApplicationContext().getPackageName());

        /*if (!getApplicationContext().getPackageName().equals("com.app.dharaneesh")) {
            // close the app or do whatever

            Toast.makeText(LoginActivity.this, "You are not allowed", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "checkingpackage" + getApplicationContext().getPackageName());
            finish();
        }*/

        int a = getCertificateValue(getApplicationContext());
        if (a != 2129967131) {
//           finish();
        }

        fetchAppVersion();
        Log.d("TAG", "checkmyid" + a);//1743328043//1743328043


        tvResetPasswordNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_reset_email);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                EditText txtusermail = dialog.findViewById(R.id.txtusermail);
                Button btnSubmitNewUser = dialog.findViewById(R.id.btnSubmitNewUser);

                btnSubmitNewUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String emailaddress = txtusermail.getText().toString();
                        auth.sendPasswordResetEmail(emailaddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "Check Your Email", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
            }
        });

        llLoginActivityGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                Intent intent = getClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        if (id.equalsIgnoreCase("No name defined")) {
        } else {
            if (mailId != null) {
                if (!mailId.trim().equals("")) {
                    if (role.equalsIgnoreCase("admin")) {
                        Intent userIntent = new Intent(getApplicationContext(), AdminDashActivity.class);
//                        Intent userIntent = new Intent(getApplicationContext(), mainsupportactivity.class);
                        startActivity(userIntent);
                        finish();
                    } else if (role.equalsIgnoreCase("user")) {
//                        Intent userIntent = new Intent(getApplicationContext(), mainsupportactivity.class);
                        Intent userIntent = new Intent(getApplicationContext(), UserDashboardActivity.class);
                        startActivity(userIntent);
                        finish();
                    } else if (role.equalsIgnoreCase("pending")) {
//                        Intent userIntent = new Intent(getApplicationContext(), mainsupportactivity.class);
                        //Intent userIntent = new Intent(getApplicationContext(), PendingActivity.class);
                        Intent userIntent = new Intent(getApplicationContext(), StaticSubsActivity.class);
                        startActivity(userIntent);
                        finish();
                    }
                }
            }
        }

        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);

        btnSubmit.setOnClickListener(v -> {

            if (txtPassword.getText().toString().trim().length() > 5 && txtMail.getText().toString().trim().length() > 0) {
                email = txtMail.getText().toString().trim().toLowerCase();
                password = txtPassword.getText().toString().trim();
                kProgressHUD.show();
                mAuth.signInWithEmailAndPassword(email, password)

                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override

                            public void onComplete(@NonNull Task<AuthResult> ta) {
                                if (ta.isSuccessful()) {
                                    myRedrence.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserModelClass user = null;
                                            emailArr = new ArrayList<>();
                                            userArr = new ArrayList<>();
                                            userListArr = new ArrayList<>();
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                user = dataSnapshot.getValue(UserModelClass.class);
                                                userListArr.add(user);
                                            }
                                            for (int l = 0; l < userListArr.size(); l++) {
                                                UserModelClass us = userListArr.get(l);
                                                emailArr.add(us.getEmail());
                                                if (emailArr.get(l).equalsIgnoreCase(txtMail.getText().toString().trim())) {
                                                    emailArr.add(us.getEmail());
                                                    userArr.add(us.getRole());
                                                }
                                            }
                                            kProgressHUD.dismiss();
                                            try {
                                                if (userArr.get(0).equalsIgnoreCase("admin")) {
                                                    FirebaseUser firUser = mAuth.getCurrentUser();
                                                    assert firUser != null;
                                                    String uniqueKey = firUser.getUid();
                                                    SharedPreferences.Editor editor = getSharedPreferences("My App", MODE_PRIVATE).edit();
                                                    editor.putString("firUserId", uniqueKey);
                                                    editor.putString("role", "admin");
                                                    editor.putString("username", txtMail.getText().toString().trim());
                                                    editor.apply();
//                                                    Intent adminIntent = new Intent(getApplicationContext(), mainsupportactivity.class);
                                                    Intent adminIntent = new Intent(getApplicationContext(), AdminDashActivity.class);
                                                    startActivity(adminIntent);
                                                    finish();
                                                } else {
                                                    FirebaseUser firUser = mAuth.getCurrentUser();
                                                    assert firUser != null;
                                                    String uniqueKey = firUser.getUid();
                                                    SharedPreferences.Editor editor = getSharedPreferences("My App", MODE_PRIVATE).edit();
                                                    editor.putString("firUserId", uniqueKey);
                                                    editor.putString("role", "user");
                                                    editor.putString("username", txtMail.getText().toString().trim());
                                                    userLoginId = firUser.getUid();
                                                    DatabaseReference uniqueKeyRef = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Login").child(uniqueKey);
                                                    HashMap<String, String> hashMap = new HashMap<>();
                                                    String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                                    hashMap.put("mobileId", id);
                                                    uniqueKeyRef.setValue(hashMap);
//                                                    Intent userIntent = new Intent(getApplicationContext(), mainsupportactivity.class);
                                                    Intent userIntent = new Intent(getApplicationContext(), UserDashboardActivity.class);
                                                    startActivity(userIntent);
                                                    finish();
                                                    editor.apply();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            kProgressHUD.dismiss();
                                        }
                                    });
                                    Toast.makeText(getApplicationContext(), "Authentication Successfully.", Toast.LENGTH_LONG).show();

                                } else {
                                    kProgressHUD.dismiss();
                                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Please fill all required field's", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("PackageManagerGetSignatures")
    public static int getCertificateValue(Context ctx) {
        try {
            android.content.pm.Signature[] signatures = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                try {
                    signatures = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES).signingInfo.getApkContentsSigners();
                } catch (Throwable ignored) {
                    ignored.printStackTrace();
                }
            }
            if (signatures == null) {
                signatures = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            }
            int value = 1;

            for (Signature signature : signatures) {
                value *= signature.hashCode();
            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean checkCertificate(Context ctx, int trustedValue) {
        return getCertificateValue(ctx) == trustedValue;
    }

    public void fetchAppVersion() {
        FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference myRedrence = datab.getReference("AppDetails");

        myRedrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AppDetailsModel matchDataModel = snapshot.getValue(AppDetailsModel.class);

                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = String.valueOf(pInfo.versionCode);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (!version.equals(matchDataModel.getVersionName())) {
                    Toast.makeText(LoginActivity.this, "Update Available", Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(LoginActivity.this)
                            .setCancelable(false)
                            .setTitle("Update Available")
                            .setMessage("Please update app to continue")

                            .setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(matchDataModel.getAppLink())));
                                    finish();
                                }
                            })
                            .show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.abhi_app_icon);
        builder.setMessage("Do you Wish to exit from App?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("reqCode", String.valueOf(resultCode));

        if (requestCode == RC_SIGN_IN) {
            //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //handleSignInResult(result);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            idToken = account.getIdToken();
            name = account.getDisplayName();
            email = account.getEmail();
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
            // Signed in successfully, show authenticated UI.
            // updateUI(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    /*private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();
            name = account.getDisplayName();
            email = account.getEmail();
            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
        } else {
            Log.e("thisIsTheResultStatus", result.getStatus().getStatusCode() + "");
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void firebaseAuthWithGoogle(AuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            uploadDataToDatabase(task.getResult().getUser().getDisplayName(),
                                    task.getResult().getUser().getPhoneNumber(),
                                    String.valueOf(task.getResult().getUser().getPhotoUrl()),
                                    task.getResult().getUser().getEmail());
                        } else {
                            task.getException().printStackTrace();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().signOut();
        }
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void uploadDataToDatabase(String name, String number, String photoUrl, String mailId) {


        FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        final DatabaseReference usersTable = database.getReference();
        Query query = usersTable
                .child("Users")
                .orderByChild("email")
                .equalTo(mailId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        saveSharedData(mailId);
                    }
                } else {
                    checkAndCreateEntry(mailId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkAndCreateEntry(String mailId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        final DatabaseReference usersTable = database.getReference();
        Query query = usersTable
                .child("PendingUsers")
                .orderByChild("email")
                .equalTo(mailId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        saveSharedDataAndSendOnPending(userModel.getId());
                    }
                } else{
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
                    String currentDateAndTime = sdf.format(new Date());

                    DatabaseReference table_user = database.getReference("PendingUsers").push();
                    UserModel userModel = new UserModel(
                            table_user.getKey(), name, mailId, "user",
                            "", currentDateAndTime, "true", "no");
                    table_user.setValue(userModel);
                    saveSharedDataAndSendOnPending(table_user.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void saveSharedDataAndSendOnPending(String key) {
        SharedPreferences.Editor editor = getSharedPreferences("My App", MODE_PRIVATE).edit();
        editor.putString("isGoogleLogin", "true");
        editor.putString("id", key);

        //Intent userIntent = new Intent(getApplicationContext(), PendingActivity.class);
        Intent userIntent = new Intent(getApplicationContext(), StaticSubsActivity.class);
        startActivity(userIntent);
        finish();
        editor.apply();
    }

    private void saveSharedData(String mailId) {
        FirebaseUser firUser = mAuth.getCurrentUser();
        assert firUser != null;
        String uniqueKey = firUser.getUid();
        SharedPreferences.Editor editor = getSharedPreferences("My App", MODE_PRIVATE).edit();
        editor.putString("firUserId", uniqueKey);
        editor.putString("role", "user");
        editor.putString("username", mailId);
        userLoginId = firUser.getUid();
        DatabaseReference uniqueKeyRef = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("Login").child(uniqueKey);
        HashMap<String, String> hashMap = new HashMap<>();
        String id1 = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        hashMap.put("mobileId", id1);
        uniqueKeyRef.setValue(hashMap);
//                                                    Intent userIntent = new Intent(getApplicationContext(), mainsupportactivity.class);
        Intent userIntent = new Intent(getApplicationContext(), UserDashboardActivity.class);
        startActivity(userIntent);
        finish();
        editor.apply();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}