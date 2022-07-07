package com.app.dharaneesh;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.service.autofill.FieldClassification;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.app.dharaneesh.util.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

class CostumAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public static int clientIdNu;

    ArrayList<MatchDataModel> matchDataModelsArr;
    Context cont;
    CostumAdapter adapter;
    String whichTeam, version;
    String userType;


    public CostumAdapter(Context incomeFragment, ArrayList<MatchDataModel> matchDataModelsArr,
                         String whichTeam, String userType) {
        cont = incomeFragment;
        this.matchDataModelsArr = matchDataModelsArr;
        inflater = (LayoutInflater) cont.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.adapter = this;
        this.whichTeam = whichTeam;
        this.userType = userType;
    }

    @Override
    public int getCount() {
        return matchDataModelsArr.size();
    }

    @Override
    public Object getItem(int po) {
        return null;
    }

    @Override
    public long getItemId(int po) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(final int pos, View con, ViewGroup parent) {
        con = inflater.inflate(R.layout.customelist, null);
        Holder holder = new Holder();


        holder.txtTitle = con.findViewById(R.id.txtLstTitle);
        holder.tvMatchType = con.findViewById(R.id.tvMatchType);
        holder.tvPrimeMatch = con.findViewById(R.id.tvPrimeMatch);
        holder.linearLayout = con.findViewById(R.id.layoutLstLinear);
        holder.txtDateTime = con.findViewById(R.id.txtLstDate);
        holder.cvMainCustomList = con.findViewById(R.id.cvMainCustomList);

        holder.txtDateTime.setText(matchDataModelsArr.get(pos).getPostdatetime());
        holder.txtTitle.setText(matchDataModelsArr.get(pos).getPosttitle() + " - " + matchDataModelsArr.get(pos).getPostLeagueType());
        holder.tvMatchType.setText(matchDataModelsArr.get(pos).getPostMatchType());


        try {
            if (matchDataModelsArr.get(pos).getPostTeamType() != null) {
                holder.tvPrimeMatch.setText(matchDataModelsArr.get(pos).getPostTeamType());
                if (matchDataModelsArr.get(pos).getPostTeamType().equalsIgnoreCase("Prime")) {
                    holder.tvPrimeMatch.setBackgroundTintList(cont.getResources().getColorStateList(R.color.red));
                } else {
                    holder.tvPrimeMatch.setBackgroundTintList(cont.getResources().getColorStateList(R.color.green));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String time = matchDataModelsArr.get(pos).getScheduledTime();
        Log.d("TAG", time);
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.US);
        LocalDateTime localDate = LocalDateTime.parse(date, formatter);
        long timeInMilliseconds = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
        Log.d("TAG", "Date in milli :: FOR API >= 26 >>> " + timeInMilliseconds);
        Log.d("TAG", "Date in milli :: FOR API >= 26 >>> " + System.currentTimeMillis());*/

        holder.linearLayout.setOnClickListener(view -> {

            if (userType.equalsIgnoreCase("Open")) {
                if (matchDataModelsArr.get(pos).getPostTeamType().equals("Prime")) {

                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        Intent intent = new Intent(cont, StaticSubsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        cont.startActivity(intent);
                        return;
                    }
                    else {

                        Toast.makeText(cont, "Please Login First", Toast.LENGTH_LONG).show();
                    }
                }
            }

            FirebaseDatabase datab = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
            DatabaseReference myRedrence = datab.getReference("AppDetails");

            myRedrence.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    AppDetailsModel matchDataModel = snapshot.getValue(AppDetailsModel.class);

                    try {
                        PackageInfo pInfo = cont.getPackageManager().getPackageInfo(cont.getPackageName(), 0);
                        version = String.valueOf(pInfo.versionCode);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (!version.equals(matchDataModel.getVersionName())) {
                        Toast.makeText(cont, "Update Available", Toast.LENGTH_SHORT).show();

                        new AlertDialog.Builder(cont)
                                .setCancelable(false)
                                .setTitle("Update Available")
                                .setMessage("Please update app to continue")

                                .setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        cont.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(matchDataModel.getAppLink())));
//                                        cont.finish();
                                    }
                                })
                                .show();
                    } else {

                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            Intent intent = new Intent(view.getContext(), ShowInfoActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("title", matchDataModelsArr.get(pos).getPosttitle());
                            intent.putExtra("details", matchDataModelsArr.get(pos).getPostdetails());
                            intent.putExtra("img", matchDataModelsArr.get(pos).getPostimg());
                            intent.putExtra("time", time);
                            if (matchDataModelsArr.get(pos).getPostLeagueType().equalsIgnoreCase("GL")) {
                                intent.putExtra("whichTeam", "GLMatch");
                            } else {
                                intent.putExtra("whichTeam", "Match");
                            }
                            view.getContext().startActivity(intent);
                        }

                        if (matchDataModelsArr.get(pos).getPostTeamType().equals("Open")){

                            Intent intent = new Intent(view.getContext(), ShowInfoActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("title", matchDataModelsArr.get(pos).getPosttitle());
                            intent.putExtra("details", matchDataModelsArr.get(pos).getPostdetails());
                            intent.putExtra("img", matchDataModelsArr.get(pos).getPostimg());
                            intent.putExtra("time", time);
                            if (matchDataModelsArr.get(pos).getPostLeagueType().equalsIgnoreCase("GL")) {
                                intent.putExtra("whichTeam", "GLMatch");
                            } else {
                                intent.putExtra("whichTeam", "Match");
                            }
                            view.getContext().startActivity(intent);
                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(cont, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

//            ((Activity) view.getContext()).finish();
        });
        return con;
    }

    class Holder {
        TextView txtTitle, txtDateTime, tvMatchType, tvPrimeMatch;
        LinearLayout linearLayout;
        CardView cvMainCustomList;
    }
}
