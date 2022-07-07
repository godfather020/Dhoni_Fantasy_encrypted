package com.app.dharaneesh;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.dharaneesh.util.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PackageListAdapterUser extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public static int clientIdNu;
    ArrayList<UserModelClass> myListData = new ArrayList<>();
    ArrayList<String> packageName;
    ArrayList<String> packageValidity;
    ArrayList<String> packagePrice;
    Context cont;
    PackageListAdapterUser adapter;
    ArrayList<String> packageDes, packageOfferPrice, packageId;
    DatabaseReference reference;
    private int LOAD_GOOGLEPAY_PAYMENT_DATA_REQUEST_CODE = 991;
    AppCompatActivity appCompatActivity;
    Dialog dialog;
    String uid = null;
    String validity;
    FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
    DatabaseReference users = datab.getReference("Users");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Query pendingRef;

    String userName, userEmail, userCreation, userId, userPayment, userEligiableChat, userRole, userExp;

    public PackageListAdapterUser(Context incomeFragment, ArrayList<String> packageName, ArrayList<String> packageDes,
                              ArrayList<String> packageId, ArrayList<String> packageValidity,
                              ArrayList<String> packagePrice, ArrayList<String> packageOfferPrice) {
        cont = incomeFragment;
        this.packageName = packageName;
        this.packageDes = packageDes;
        this.packageId = packageId;
        inflater = (LayoutInflater) cont.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.adapter = this;
        this.packageValidity = packageValidity;
        this.packagePrice = packagePrice;
        this.packageOfferPrice = packageOfferPrice;
        appCompatActivity = (AppCompatActivity) cont;
        if (firebaseAuth.getCurrentUser() != null){

            uid = firebaseAuth.getCurrentUser().getUid();
        }
    }

    @Override
    public int getCount() {
        return packageName.size();
    }

    @Override
    public Object getItem(int po) {
        return null;
    }

    @Override
    public long getItemId(int po) {
        return 0;
    }

    @Override
    public View getView(final int pos, View con, ViewGroup parent) {
        con = inflater.inflate(R.layout.package_list_adapter_user, null);
        PackageListAdapterUser.Holder holder = new PackageListAdapterUser.Holder();
        holder.packageName = con.findViewById(R.id.packageName);
        holder.packagePrice = con.findViewById(R.id.orgPrice);
        holder.packageValidity = con.findViewById(R.id.packageValidity);
        holder.packageOfferPrice = con.findViewById(R.id.offerPrice);
        holder.pay = con.findViewById(R.id.pay);
        holder.llCompleteView = con.findViewById(R.id.llCompleteView);

        holder.packageName.setText(packageName.get(pos));
        holder.packageValidity.setText(packageValidity.get(pos)+" days");
        holder.packagePrice.setText("Rs."+packagePrice.get(pos));

        if (packageOfferPrice.get(pos).equals("0")){

            holder.packageOfferPrice.setVisibility(View.GONE);
        }
        else {

            holder.packageOfferPrice.setVisibility(View.VISIBLE);
            holder.packagePrice.setPaintFlags(holder.packagePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.packageOfferPrice.setText("Rs."+packageOfferPrice.get(pos));
        }

        holder.llCompleteView.setOnClickListener(v -> {

            Intent intent = new Intent(cont, ShowInfoActivity.class);
            intent.putExtra("packageName", packageName.get(pos));
            intent.putExtra("packageDes", packageDes.get(pos));
            //intent.putExtra("packageImg", packageImg.get(pos));
            intent.putExtra("packageValidity", packageValidity.get(pos));
            intent.putExtra("packagePrice", packagePrice.get(pos));
            intent.putExtra("packageOfferPrice", packageOfferPrice.get(pos));
            cont.startActivity(intent);
        });

        if (uid != null) {

            Log.d("userInfo1", firebaseAuth.getUid());
            String email = firebaseAuth.getCurrentUser().getEmail();
            Log.d("email", email);

            pendingRef = datab.getReference("PendingUsers").orderByChild("email").equalTo(email);

            pendingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){

                            Log.d("user", "exist");
                            for(DataSnapshot datas: snapshot.getChildren()) {

                                userEmail = datas.child("email").getValue().toString();
                                userName = datas.child("username").getValue().toString();
                                userCreation = datas.child("creationDate").getValue().toString();
                                userPayment = datas.child("payment").getValue().toString();
                                userId = datas.child("id").getValue().toString();
                                userEligiableChat = datas.child("isEligibleForChat").getValue().toString();
                                userRole = datas.child("role").getValue().toString();
                                userExp = datas.child("expirationDate").getValue().toString();

                                Log.d("userInfo", userName + " " + userEmail + " " + userPayment + " " + userCreation + " " + userId+ " " + userEligiableChat
                                + " "+ userRole+ " " + userExp);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            Query userData = users.orderByChild("id").equalTo(uid);

            userData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){

                        Log.d("user111", "exist");

                        for(DataSnapshot datas: snapshot.getChildren()) {

                            userEmail = datas.child("email").getValue().toString();
                            userName = datas.child("username").getValue().toString();
                            userCreation = datas.child("creationDate").getValue().toString();
                            userPayment = datas.child("payment").getValue().toString();
                            userId = datas.child("id").getValue().toString();
                            userEligiableChat = datas.child("isEligibleForChat").getValue().toString();
                            userRole = datas.child("role").getValue().toString();
                            userExp = datas.child("expirationDate").getValue().toString();

                            Log.d("userInfo111", userName + " " + userEmail + " " + userPayment + " " + userCreation + " " + userId+ " " + userEligiableChat
                                    + " "+ userRole+ " " + userExp);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        holder.pay.setOnClickListener(view -> {


            dialog = new Dialog(cont);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_payment);
            dialog.show();

            ImageView gPay = dialog.findViewById(R.id.gPay);
            ImageView paytm = dialog.findViewById(R.id.paytm);
            ImageView phonePay = dialog.findViewById(R.id.phonePay);

            /* Date temp1;
            SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.US);
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            try {
                temp1 = formatter1.parse(userCreation);
                Log.d("date", String.valueOf(format1.format(temp1)));
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

            Date date=null;
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

            Date c1 = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c1);

            try {
                date = formatter.parse(formattedDate);
                Log.e("formated date ", date + "");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar c = Calendar.getInstance();
            if (date != null) {
                c.setTime(date);
            }
            c.add(Calendar.DATE, 2);
            Date expDate = c.getTime();
            String formatedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(expDate);

            Log.d("exp", formatedDate);

            gPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (uid != null) {

                        validity = packageValidity.get(pos);
                        requestpaymentGPay(packageOfferPrice.get(pos));
                    }
                    else {

                        Toast.makeText(cont, "Please Login first", Toast.LENGTH_LONG).show();
                    }
                }
            });

            phonePay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (uid != null) {

                        validity = packageValidity.get(pos);
                        requestPaymentPhonePay(packageOfferPrice.get(pos));
                    }
                    else {

                        Toast.makeText(cont, "Please Login first", Toast.LENGTH_LONG).show();
                    }
                }
            });

            paytm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (uid != null) {

                        validity = packageValidity.get(pos);
                        requestPaymentPaytm(packageOfferPrice.get(pos));
                    }
                    else {

                        Toast.makeText(cont, "Please Login first", Toast.LENGTH_LONG).show();
                    }
                }
            });


        });

        return con;
    }

    private void requestPaymentPaytm(String amt) {

        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        String PAYTM_PAY_PACKAGE_NAME = "net.one97.paytm";

        String paytmuri = "upi://pay?pa=Q318099710@ybl&pn=Merchant%20Name&am="+amt+"&cu=INR&mode=02&orgid=000000&tn=Subscribe&tr="+timeStamp;

        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(paytmuri));
        intent.setData(Uri.parse(paytmuri));
        intent.setPackage(PAYTM_PAY_PACKAGE_NAME);

        PackageManager pm = cont.getPackageManager();

        try{

            Boolean isInstalled = isPakageInstalled(PAYTM_PAY_PACKAGE_NAME, pm);
            if (isInstalled){

                appCompatActivity.startActivityForResult(intent, LOAD_GOOGLEPAY_PAYMENT_DATA_REQUEST_CODE);

            }
            else{
                Toast.makeText(cont,"App not found in your device", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){

            Toast.makeText(cont,"App not found in your device", Toast.LENGTH_LONG).show();
        }
    }

    private void requestPaymentPhonePay(String amt) {

        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        String PHONE_PAY_PACKAGE_NAME = "com.phonepe.app";

        String phonepeuri = "upi://pay?pa=Q318099710@ybl&pn=Merchant%20Name&am="+amt+"&cu=INR&mode=02&orgid=000000&tn=Subscribe&tr="+timeStamp;

        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(phonepeuri));
        intent.setData(Uri.parse(phonepeuri));
        intent.setPackage(PHONE_PAY_PACKAGE_NAME);

        PackageManager pm = cont.getPackageManager();

        try{

            Boolean isInstalled = isPakageInstalled(PHONE_PAY_PACKAGE_NAME, pm);
            if (isInstalled){

                appCompatActivity.startActivityForResult(intent, LOAD_GOOGLEPAY_PAYMENT_DATA_REQUEST_CODE);

            }
            else{
                Toast.makeText(cont,"App not found in your device", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){

            Toast.makeText(cont,"App not found in your device", Toast.LENGTH_LONG).show();
        }
    }

    private void requestpaymentGPay(String amt) {

        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";

        String gpayuri = "upi://pay?pa=Q318099710@ybl&pn=Merchant%20Name&am="+amt+"&cu=INR&mode=02&orgid=000000&tn=Subscribe&tr="+timeStamp;

        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(gpayuri));
        intent.setData(Uri.parse(gpayuri));
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);

        PackageManager pm = cont.getPackageManager();

        try{

            Boolean isInstalled = isPakageInstalled(GOOGLE_PAY_PACKAGE_NAME, pm);
            if (isInstalled){

                appCompatActivity.startActivityForResult(intent, LOAD_GOOGLEPAY_PAYMENT_DATA_REQUEST_CODE);

            }
            else{
                Toast.makeText(cont,"App not found in your device", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){

            Toast.makeText(cont,"App not found in your device", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == LOAD_GOOGLEPAY_PAYMENT_DATA_REQUEST_CODE){

            if (RESULT_OK == resultCode || resultCode == 11){

                if (data != null){

                    String trxt = data.getStringExtra("response");
                    Log.d("UPI", "onActivityResult: " + trxt);
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add(trxt);
                    upiPaymentDataOperation(dataList);
                }
            }
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> dataList) {

        String str = dataList.get(0);
        Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
        String  paymentCancel = "";
        if (str == null){

            str = "discard";
        }
        String status = "";
        String approvalRefNo = "";
        String[] response = (str.split("&"));
        Arrays.sort(response);

        for (int i =0; i < response.length; i++){

            String[] equlaStr = response[i].split("=");

            if (equlaStr.length >= 2){

                if (equlaStr[0].toLowerCase().equals("Status".toLowerCase())){

                    status = equlaStr[1].toLowerCase();
                }
                else if (equlaStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equlaStr[0].toLowerCase().equals("txnRef".toLowerCase())){

                    approvalRefNo = equlaStr[1];
                }

            }
            else {

                paymentCancel = "Payment cancelled by user.";
            }
        }

        if (status.equals("success")){

            Log.d("UPI", "responseStr: "+approvalRefNo);
            Toast.makeText(cont, "Payment Successfull", Toast.LENGTH_LONG).show();
            createUser(validity);
            SharedPreferences prefs = cont.getSharedPreferences("My App", MODE_PRIVATE);
            prefs.edit().putString("isGoogleLogin", "false").apply();
            prefs.edit().putString("role", "user").apply();
            prefs.edit().putString("firUserId", uid).apply();
            prefs.edit().putString("username", userEmail.trim()).apply();
            dialog.cancel();
            Intent intent = new Intent(cont, UserDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cont.startActivity(intent);
        }
        else if ("Payment cancelled by user.".equals(paymentCancel)) {
            //showResponseDialog("Payment cancelled by user.")
            Toast.makeText(cont, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
        } else {
            //showResponseDialog("Transaction failed.Please try again")
            Toast.makeText(cont, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void createUser(String validity) {

        Date date=null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        Date c1 = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c1);

        try {
            date = formatter.parse(formattedDate);
            Log.e("formated date ", date + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        if (date != null) {
            c.setTime(date);
        }
        c.add(Calendar.DATE, Integer.parseInt(validity));
        Date expDate = c.getTime();
        String formatedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(expDate);

        Log.d("exp", formatedDate);

        userPayment = "Yes";
        userExp = formatedDate;

        FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        DatabaseReference mRef = database.getReference().child("Users").child(userId);
        mRef.child("creationDate").setValue(userCreation);
        mRef.child("email").setValue(userEmail);
        mRef.child("expirationDate").setValue(userExp);
        mRef.child("id").setValue(userId);
        mRef.child("isEligibleForChat").setValue(userEligiableChat);
        mRef.child("payment").setValue(userPayment);
        mRef.child("role").setValue(userRole);
        mRef.child("username").setValue(userName);

        DatabaseReference pendingUsersRef = database.getReference().child("PendingUsers");
        pendingUsersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Boolean isPakageInstalled(String google_pay_package_name, PackageManager pm) {

        try {
            pm.getPackageInfo(google_pay_package_name, 0);
            return true;
        }
        catch (Exception e){

            return false;
        }
    }

    class Holder {
        TextView packageName, packageValidity, packagePrice, packageOfferPrice;
        Button pay;
        LinearLayout llCompleteView;
    }

}
