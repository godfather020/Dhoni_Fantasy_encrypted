package com.app.dharaneesh;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.dharaneesh.util.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PackageListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public static int clientIdNu;

    ArrayList<String> packageName;
    ArrayList<String> packageValidity;
    ArrayList<String> packagePrice;
    Context cont;
    PackageListAdapter adapter;
    ArrayList<String> packageDes, packageOfferPrice, packageId;
    DatabaseReference reference;

    public PackageListAdapter(Context incomeFragment, ArrayList<String> packageName, ArrayList<String> packageDes,
                              ArrayList<String> packageId, ArrayList<String> packageValidity,
                              ArrayList<String> packagePrice, ArrayList<String> packageOfferPrice) {
        cont = incomeFragment;
        this.packageName = packageName;
        this.packageDes = packageDes;
        this.packageId = packageId;
        inflater = (LayoutInflater) cont.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.adapter = this;
        this.packageValidity = packageValidity;
        //this.packageImg = packageImg;
        this.packagePrice = packagePrice;
        this.packageOfferPrice = packageOfferPrice;
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
        con = inflater.inflate(R.layout.package_list_adapter, null);
        PackageListAdapter.Holder holder = new PackageListAdapter.Holder();
        holder.packageName = con.findViewById(R.id.packageName);
        holder.packagePrice = con.findViewById(R.id.orgPrice);
        holder.packageValidity = con.findViewById(R.id.packageValidity);
        holder.packageOfferPrice = con.findViewById(R.id.offerPrice);
        holder.btnDelete = con.findViewById(R.id.btnDeleteInfo);
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


        //Plan Exp code below
        /*Date date=null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String temp = "01-07-2022";
        try {
            date = formatter.parse(temp);
            Log.e("formated date ", date + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 90);
        Date expDate = c.getTime();
        String formatedDate = new SimpleDateFormat("dd-MM-yyyy").format(expDate);

        Log.d("exp", formatedDate);*/


        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(cont);
            builder1.setMessage("Are you sure for delete this match");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
                            database.getReference("Package")
                                    .child(packageId.get(pos))
                                    .removeValue();
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        });

        return con;
    }

    class Holder {
        TextView packageName, packageValidity, packagePrice, packageOfferPrice;
        Button  btnDelete;
        LinearLayout llCompleteView;
    }
}
