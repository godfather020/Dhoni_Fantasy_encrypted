package com.app.dharaneesh;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;

class CostumAdapterAdmin extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public static int clientIdNu;

    ArrayList<String> titleArr;
    ArrayList<String> datetimeArr;
    ArrayList<String> idArr;
    Context cont;
    CostumAdapterAdmin adapter;
    String matchType;
    ArrayList<String> detailArray, imgArray, teamTypeArr, isScheduled, scheduledTime;
    DatabaseReference reference;

    public CostumAdapterAdmin(Context incomeFragment, ArrayList<String> titleArr, ArrayList<String> datetimeArr,
                              ArrayList<String> idArr, String matchType, ArrayList<String> detailArray,
                              ArrayList<String> imgArray, ArrayList<String> teamTypeArr, ArrayList<String> isScheduled, ArrayList<String> scheduledTime) {
        cont = incomeFragment;
        this.titleArr = titleArr;
        this.datetimeArr = datetimeArr;
        this.idArr = idArr;
        inflater = (LayoutInflater) cont.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.adapter = this;
        this.matchType = matchType;
        this.detailArray = detailArray;
        this.imgArray = imgArray;
        this.teamTypeArr = teamTypeArr;
        this.isScheduled = isScheduled;
        this.scheduledTime = scheduledTime;
    }

    @Override
    public int getCount() {
        return titleArr.size();
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
        con = inflater.inflate(R.layout.admincustomelist, null);
        Holder holder = new Holder();
        holder.txtTitle = con.findViewById(R.id.txtUpdateLstTitle);
        holder.btnUpdate = con.findViewById(R.id.btnUpdateInfo);
        holder.txtDateTime = con.findViewById(R.id.txtUpdateLstDate);
        holder.tvPrimeMatch = con.findViewById(R.id.tvPrimeMatch);
        holder.btnDelete = con.findViewById(R.id.btnDeleteInfo);
        holder.llCompleteView = con.findViewById(R.id.llCompleteView);
        holder.isScheduled = con.findViewById(R.id.scheduled);

        holder.txtDateTime.setText(datetimeArr.get(pos));
        holder.txtTitle.setText(titleArr.get(pos));
        holder.tvPrimeMatch.setText(teamTypeArr.get(pos));

        if(teamTypeArr.get(pos).equalsIgnoreCase("Prime")){
            holder.tvPrimeMatch.setBackgroundTintList(cont.getResources().getColorStateList(R.color.red));
        } else {
            holder.tvPrimeMatch.setBackgroundTintList(cont.getResources().getColorStateList(R.color.green));
        }

        if (isScheduled.get(pos).equals("true")){

            holder.isScheduled.setVisibility(View.VISIBLE);
            holder.btnUpdate.setVisibility(View.GONE);
        }
        else {

            holder.isScheduled.setVisibility(View.GONE);
            holder.btnUpdate.setVisibility(View.VISIBLE);
        }

        String time = scheduledTime.get(pos);

        Log.d("time", time);

        Log.d("img", imgArray.get(pos));

        holder.llCompleteView.setOnClickListener(v -> {
            Intent intent = new Intent(cont, ShowInfoActivity.class);
            intent.putExtra("title", titleArr.get(pos));
            intent.putExtra("details", detailArray.get(pos));
            intent.putExtra("img", imgArray.get(pos));
            intent.putExtra("whichTeam", matchType);
            intent.putExtra("time", time);
            cont.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(cont);
            builder1.setMessage("Are you sure for delete this match");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("schedule")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds : snapshot.getChildren()){
                                                String v = ds.child("keys").getValue(String.class);
                                                if(v.matches(idArr.get(pos))){
                                                    String p = ds.getKey();
                                                    Log.d("tag", "checkdelet"+p);
                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("schedule").child(p).removeValue();


                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });



                            FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
                            database.getReference("Match")
                                    .child(idArr.get(pos))
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

//            Intent intent = new Intent(view.getContext(), UpdateActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("nam", idArr.get(pos));
//            view.getContext().startActivity(intent);
//            ((Activity) view.getContext()).finish();
        });

        holder.btnUpdate.setOnClickListener(v -> {
            if (matchType.equals("Match")) {
                Intent intent = new Intent(v.getContext(), UpdateActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("nam", idArr.get(pos));
                intent.putExtra("title", titleArr.get(pos));
                intent.putExtra("time", time);
                v.getContext().startActivity(intent);
            } else if(matchType.matches("admin")){
                Intent intent = new Intent(v.getContext(), UpdateActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("nam", idArr.get(pos));
                intent.putExtra("name", "admin");
                intent.putExtra("title", titleArr.get(pos));
                intent.putExtra("time", time);
                v.getContext().startActivity(intent);
            }else {
                Intent intent = new Intent(v.getContext(), UpdateGlActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("nam", idArr.get(pos));
                intent.putExtra("title", titleArr.get(pos));
                v.getContext().startActivity(intent);
            }
        });

        return con;
    }

    class Holder {
        TextView txtTitle, txtDateTime, tvPrimeMatch, isScheduled;
        Button btnUpdate, btnDelete;
        LinearLayout llCompleteView;
    }
}