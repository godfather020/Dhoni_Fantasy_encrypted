package com.app.dharaneesh;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.watermark.androidwm.WatermarkBuilder;
import com.watermark.androidwm.bean.WatermarkText;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShowInfoActivity extends AppCompatActivity {
    TextView txtTitle, txtDetails, txtNameFi, txtNameSec, txtNameThi, txtNameFor, txtNameLeftFi, txtNameLeftSec, txtNameLeftThi, txtNameLeftFor, txtNameFor1, txtNameLeftFor1;
    TextView txtPrice, txtOfferPrice;
    LinearLayout layout;
    ImageView imgDetails;
    ProgressDialog pDialog;
    String whichTeam = "0";
    RecyclerView recyclerViewHomeWorkImages;
    String[] urlsArray;
    String img;
    int i = 0;
    String[] fileName;
    List<ImageHomeworkModel> imagesList;
    ImagesHomeworkAdapter adapter;
    WatermarkText watermarkText;
    CardView cvSL;
    ProgressBar pvImage;
    String packageName = "null";
    String packageDes, packageImg, packagePrice, packageOfferPrice, packageValidity;
    String time_txt = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
        //        WindowManager.LayoutParams.FLAG_SECURE);

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        pvImage = findViewById(R.id.pvImage);
        txtDetails = findViewById(R.id.txtInfoDetails);
        txtTitle = findViewById(R.id.txtInfoTitle);
        imgDetails = findViewById(R.id.imgInfoImage);
        layout = findViewById(R.id.layMatch);
        txtNameFi = findViewById(R.id.txtNameFi);
        txtNameSec = findViewById(R.id.txtNameSe);
        txtNameThi = findViewById(R.id.txtNameTh);
        txtNameFor = findViewById(R.id.txtNameFor);
        txtNameLeftFi = findViewById(R.id.txtNameLeftFi);
        txtNameLeftSec = findViewById(R.id.txtNameLeftSec);
        txtNameLeftThi = findViewById(R.id.txtNameLeftTh);
        txtNameLeftFor = findViewById(R.id.txtNameLeftFor);
        txtNameLeftFor1 = findViewById(R.id.txtNameLeftFor2);
        txtNameFor1 = findViewById(R.id.txtNameFor1);
        cvSL = findViewById(R.id.cvSL);
        txtPrice = findViewById(R.id.txtPrice);
        txtOfferPrice = findViewById(R.id.txtOfferPrice);


        recyclerViewHomeWorkImages = findViewById(R.id.recyclerViewHomeWorkImages);

        SharedPreferences prefs = getSharedPreferences("My App", MODE_PRIVATE);
//        String id = prefs.getString("firUserId", "No name defined");
        String name = prefs.getString("username", "");


        String textForWaterMark = name.replace("@gmail.com", "");
        watermarkText = new WatermarkText(textForWaterMark)
                .setPositionX(2)
                .setPositionY(2)
                .setTextColor(Color.WHITE)
                .setTextFont(R.font.quicksand_medium)
                .setTextAlpha(180)
                .setRotation(50)
                .setTextSize(11);


        if (name == null) {
//            finishAffinity();
            imgDetails.setVisibility(View.GONE);
        } else if (name.trim().equals("")) {
//            finishAffinity();
            imgDetails.setVisibility(View.GONE);
        }

//
//        if (id.equalsIgnoreCase("No name defined")) {
//            checkPhoneLogin checkPhone = new checkPhoneLogin();
//            String mob = checkPhone.checkStatus(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), LoginActivity.userLoginId);
//        } else {
//            checkPhoneLogin checkPhone = new checkPhoneLogin();
//            String mob = checkPhone.checkStatus(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), id);
//        }

        pDialog = new ProgressDialog(ShowInfoActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);

        imagesList = new ArrayList<>();
        adapter = new ImagesHomeworkAdapter(ShowInfoActivity.this, imagesList, name);
        recyclerViewHomeWorkImages.setHasFixedSize(true);
        recyclerViewHomeWorkImages.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerViewHomeWorkImages.setAdapter(adapter);

        txtTitle.setText(getIntent().getStringExtra("title"));
        txtDetails.setText(getIntent().getStringExtra("details"));
        img = "0";
        if (getIntent().getStringExtra("img") != null) {
            img = getIntent().getStringExtra("img");
        }
        whichTeam = "0";
        if (getIntent().getStringExtra("whichTeam") != null){

            whichTeam = getIntent().getStringExtra("whichTeam");
        }
        packageName = getIntent().getStringExtra("packageName");
        packageDes = getIntent().getStringExtra("packageDes");
        //packageImg = getIntent().getStringExtra("packageImg");
        /*if (getIntent().getStringExtra("packageImg") != null){

            img = getIntent().getStringExtra("packageImg");
        }*/
        packageValidity = "Validity:- "+getIntent().getStringExtra("packageValidity");
        packagePrice = "Price:- "+getIntent().getStringExtra("packagePrice");
        if (getIntent().getStringExtra("packageOfferPrice").equals("0")) {
            packageOfferPrice = getIntent().getStringExtra("packageOfferPrice");
        }
        else {
            packageOfferPrice = "Offer Price:- " + getIntent().getStringExtra("packageOfferPrice");
        }

        if (getIntent().getStringExtra("time") != null){

            time_txt = getIntent().getStringExtra("time");
        }

        Long time = null;


        if (packageName != null){

            txtPrice.setVisibility(View.VISIBLE);
            txtOfferPrice.setVisibility(View.VISIBLE);

            txtTitle.setText(packageName+"\t\t"+packageValidity);
            txtDetails.setText(packageDes);
            txtPrice.setText(packagePrice);

            if (packageOfferPrice.equals("0")){

                txtOfferPrice.setVisibility(View.GONE);
            }
            else {

                txtOfferPrice.setVisibility(View.VISIBLE);
                txtPrice.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                txtOfferPrice.setText(packageOfferPrice);
            }

            //img = packageImg;
            imgDetails.setVisibility(View.VISIBLE);
            cvSL.setVisibility(View.GONE);
            //Log.d("img", img);
            /*String finalImg = img;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        URL url = new URL(finalImg);
                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        // String appPath = App.getApp().getApplicationContext().getFilesDir().getAbsolutePath();
                        imgDetails.post(new Runnable() {
                            @Override
                            public void run() {
                                WatermarkBuilder
                                        .create(ShowInfoActivity.this, image)
                                        .loadWatermarkText(watermarkText) // use .loadWatermarkImage(watermarkImage) to load an image.
                                        .setTileMode(true)
                                        .getWatermark()
                                        .setToImageView(imgDetails);
                                pvImage.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {
                        pvImage.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }).start();*/

        }
        else {

            txtPrice.setVisibility(View.GONE);
            txtOfferPrice.setVisibility(View.GONE);
            imgDetails.setVisibility(View.GONE);
            cvSL.setVisibility(View.VISIBLE);
        }


        if (!time_txt.equals("0")) {
            time = Long.parseLong(time_txt);
        }

        if (whichTeam.equals("Match") && whichTeam != "0") {
            pvImage.setVisibility(View.VISIBLE);
            imgDetails.setVisibility(View.VISIBLE);
            recyclerViewHomeWorkImages.setVisibility(View.GONE);
            cvSL.setVisibility(View.VISIBLE);
        } else {
            if (!img.equals("default")) {

                if (time != null) {

                    if (System.currentTimeMillis() >= time) {

                        cvSL.setVisibility(View.GONE);
                        imgDetails.setVisibility(View.INVISIBLE);
                        recyclerViewHomeWorkImages.setVisibility(View.VISIBLE);

                        urlsArray = img.split(", ");

                        for (i = 0; i < urlsArray.length; i++) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference httpsReference = storage.getReferenceFromUrl(urlsArray[i]);
                            fileName = new String[urlsArray.length];
                            fileName[i] = httpsReference.getName();

                            imagesList.add(new ImageHomeworkModel(urlsArray[i], fileName[i]));
                        }
                    }
                }
            }

        }
        System.out.println("Image Path " + img);
        if (img.equalsIgnoreCase("default")) {
            pvImage.setVisibility(View.GONE);
        } else {

            pDialog.show();
//            Picasso.with(getApplicationContext()).
//                    load(img).
//                    transform(new WatermarkTransformation(name)).into(imgDetails);

//            try {
//                URL url = new URL(img);
//                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                Bitmap waterMarkImage = mark(image, name, new Point(100, 1000), 0, 60, false);
//                imgDetails.setImageBitmap(waterMarkImage);
//            } catch (IOException e) {
//                System.out.println(e);
//            }

            Log.d("curTime", String.valueOf(time));

            if (time != null) {

                if (System.currentTimeMillis() >= time) {

                    String finalImg = img;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                URL url = new URL(finalImg);
                                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                // String appPath = App.getApp().getApplicationContext().getFilesDir().getAbsolutePath();
                                imgDetails.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        WatermarkBuilder
                                                .create(ShowInfoActivity.this, image)
                                                .loadWatermarkText(watermarkText) // use .loadWatermarkImage(watermarkImage) to load an image.
                                                .setTileMode(true)
                                                .getWatermark()
                                                .setToImageView(imgDetails);
                                        pvImage.setVisibility(View.GONE);
                                    }
                                });
                            } catch (Exception e) {
                                pvImage.setVisibility(View.GONE);
                                e.printStackTrace();
                            }

                        }
                    }).start();

                }
                else {

                    pvImage.setVisibility(View.GONE);
                }
            }
            else {

                pvImage.setVisibility(View.GONE);
            }

            pDialog.dismiss();
            txtNameThi.setText(name);
            txtNameFor.setText(name);
            txtNameSec.setText(name);
            txtNameFi.setText(name);
            txtNameLeftThi.setText(name);
            txtNameLeftFor.setText(name);
            txtNameLeftFor.setText(name);
            txtNameLeftSec.setText(name);
            txtNameLeftFi.setText(name);
            txtNameFor1.setText(name);
            txtNameLeftFor1.setText(name);
        }
        layout.setOnClickListener(v -> {
            finish();
        });
    }

}