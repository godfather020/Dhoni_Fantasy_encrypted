package com.app.dharaneesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.dharaneesh.util.Constant;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class PaymentDetailsAdmin extends AppCompatActivity {

    TextInputEditText txtPackageName, txtPackageDes, txtPackagePrice, txtPackageOffer, txtPackageValidity;
    ImageView packageImage;
    Button btnChooseImg, btnSubmitNewPackage;
    Boolean isValidate = false;
    private static final int imgReq = 1;
    Uri profile_uri;
    ProgressDialog pD;
    StorageReference storageReference;
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details_admin);

        txtPackageDes = findViewById(R.id.txtPackageDes);
        txtPackageName = findViewById(R.id.txtPackageName);
        txtPackageOffer = findViewById(R.id.txtPackageOffer);
        txtPackagePrice = findViewById(R.id.txtPackagePrice);
        txtPackageValidity = findViewById(R.id.txtPackageValidity);
        packageImage = findViewById(R.id.packageImage);
        btnChooseImg = findViewById(R.id.btnChooseImg);
        btnSubmitNewPackage = findViewById(R.id.btnSubmitNewPackage);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openImage();
            }
        });


        btnSubmitNewPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isValidate = validateTxt(txtPackageName.getText().toString() ,txtPackageDes.getText().toString(), txtPackagePrice.getText().toString()
                        , txtPackageOffer.getText().toString(), txtPackageValidity.getText().toString());

                if (isValidate){

                    //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

                    //uploadProfile();

                    try {
                        FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
                        DatabaseReference myRef = database.getReference("Package").push();
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("packageName", txtPackageName.getText().toString().trim());
                        hashMap.put("packageDetails", txtPackageDes.getText().toString().trim());
                        hashMap.put("packagePrice", txtPackagePrice.getText().toString());
                        if(txtPackageOffer.getText().toString().isEmpty()){
                            hashMap.put("packageOfferPrice", "0");
                        }
                        else {
                            hashMap.put("packageOfferPrice", txtPackageOffer.getText().toString());
                        }
                        hashMap.put("packageValidity", txtPackageValidity.getText().toString());
                       // hashMap.put("packageimg", mUri);
                        myRef.setValue(hashMap);
                        Toast.makeText(getApplicationContext(), "Package Upload Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                }
                else {

                    Toast.makeText(getApplicationContext(), "Please Fill all Details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openImage() {
        Intent imgIntent = new Intent();
        imgIntent.setType("image/*");
        imgIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imgIntent, imgReq);
    }

    private boolean validateTxt(String pakName, String pakDes, String pakPrice, String pakOffer, String pakValidity) {

        if (pakName.isEmpty()){

            return false;
        }
        else if (pakDes.isEmpty()){

            return false;
        }
        else if (pakPrice.isEmpty()){

            return false;
        }
        /*else if (pakOffer.isEmpty()){

            return false;
        }*/
        else if (pakValidity.isEmpty()){

            return false;
        }
        else {

            return true;
        }

    }

    private String getFileExte(Uri uri) {
        ContentResolver cont = getApplicationContext().getContentResolver();
        MimeTypeMap min = MimeTypeMap.getSingleton();
        return min.getExtensionFromMimeType(cont.getType(uri));
    }

    private void uploadProfile() {
        pD = new ProgressDialog(PaymentDetailsAdmin.this);
        pD.setMessage("Please wait...");
        pD.setIndeterminate(true);
        pD.setCancelable(false);
        pD.show();
        if (profile_uri != null) {
            final StorageReference fileRefernce = storageReference.child(System.currentTimeMillis() + "." + getFileExte(profile_uri));
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), profile_uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            uploadTask = fileRefernce.putBytes(data);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(Task<UploadTask.TaskSnapshot> tas) throws Exception {
                    if (!tas.isSuccessful()) {
                        throw tas.getException();
                    }
                    return fileRefernce.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> tas) {
                    if (tas.isSuccessful()) {
                        Uri downloadUri = tas.getResult();
                        String mUri = downloadUri.toString();

                        try {
                            FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
                            DatabaseReference myRef = database.getReference("Package").push();
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("packageName", txtPackageName.getText().toString().trim());
                            hashMap.put("packageDetails", txtPackageDes.getText().toString().trim());
                            hashMap.put("packagePrice", txtPackagePrice.getText().toString());
                            hashMap.put("packageOfferPrice", txtPackageOffer.getText().toString());
                            hashMap.put("packageValidity", txtPackageValidity.getText().toString());
                            hashMap.put("packageimg", mUri);
                            myRef.setValue(hashMap);
                            Toast.makeText(getApplicationContext(), "Package Upload Successfully", Toast.LENGTH_LONG).show();
                            finish();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                        pD.dismiss();
                        //showTo("Photo Uploaded successfully");
                        finish();

                    } else {
                        showTo("Failed");
                        pD.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showTo("Failed");
                    pD.dismiss();
                }
            });
        } else {
            pD.dismiss();
            showTo("No Image Selected");
        }
    }

    public void showTo(String mess) {
        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int reqCode, int result, Intent da) {
        super.onActivityResult(reqCode, result, da);
        if (reqCode == imgReq && result == RESULT_OK && da != null && da.getData() != null) {
            profile_uri = da.getData();
            packageImage.setImageURI(null);
            packageImage.setImageURI(da.getData());
        }
    }
}