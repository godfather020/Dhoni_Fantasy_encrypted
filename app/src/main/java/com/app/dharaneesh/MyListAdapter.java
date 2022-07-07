package com.app.dharaneesh;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dharaneesh.support.messagescreen;
import com.app.dharaneesh.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private ArrayList<UserModelClass> listdata;
    String[] expireTime = {"15 Days", "30 Days", "60 Days", "90 Days"};
    DatabaseReference ref;
    AlertDialog.Builder builder;
    Context context;
    final Calendar myCalendar = Calendar.getInstance();
    String isForPending;


    // RecyclerView recyclerView;
    public MyListAdapter(ArrayList<UserModelClass> listdata, Context context, String isForPending) {
        this.listdata = listdata;
        this.context = context;
        this.isForPending = isForPending;
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
        ref = mDatabase.getReference("Users");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.adapter_users_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserModelClass myListData = listdata.get(position);

        if (isForPending != null && isForPending.equalsIgnoreCase("yes")) {
            holder.ivchat.setVisibility(View.GONE);
            holder.ivDeleteUser.setVisibility(View.GONE);
        }

        holder.tvUserAdapterUserName.setText(myListData.getUsername() + " (" + myListData.getRole() + ")");
        holder.tvUserAdapterEmailId.setText(myListData.getEmail());


        holder.ivDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete User?")
                        .setMessage("Are you sure you want to delete this User?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                DatabaseReference ref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();
                                Query applesQuery = ref.child("Users").orderByChild("email").equalTo(myListData.getEmail());

                                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                            appleSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    ((Activity) context).finish();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        holder.ivApproveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_update_payment);
                dialog.show();


                Button btnTvUpdatePayment = dialog.findViewById(R.id.btnTvUpdatePayment);
//                Spinner spDurationOfUpdatePay = dialog.findViewById(R.id.spDurationOfUpdatePay);
                RadioButton rbNo = dialog.findViewById(R.id.rbNo);
                RadioButton rbYes = dialog.findViewById(R.id.rbYes);
                TextView tvUserExpDate = dialog.findViewById(R.id.tvUserExpDate);
                TextView tvDialogUpdatePaymentExpDate = dialog.findViewById(R.id.tvDialogUpdatePaymentExpDate);


                if (myListData.getPayment().equals("Yes")) {
                    rbYes.setChecked(true);
                    tvUserExpDate.setVisibility(View.VISIBLE);
                    tvUserExpDate.setText(myListData.getUsername() + "'s current Expiry Date: " + myListData.getExpirationDate());
                } else {
                    rbNo.setChecked(true);
                    tvUserExpDate.setVisibility(View.INVISIBLE);
                }

                ArrayAdapter aaExpire = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, expireTime);
                aaExpire.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spDurationOfUpdatePay.setAdapter(aaExpire);

                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);
                        updateLabel(tvDialogUpdatePaymentExpDate);
                    }
                };

                tvDialogUpdatePaymentExpDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                btnTvUpdatePayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String duration = tvDialogUpdatePaymentExpDate.getText().toString();

                        if (duration.equals("Select")) {
                            Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (isForPending != null && isForPending.equalsIgnoreCase("yes")) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE);
                            DatabaseReference mRef = database.getReference().child("Users").child(myListData.getId());
                            mRef.child("creationDate").setValue(myListData.getCreationDate());
                            mRef.child("email").setValue(myListData.getEmail());
                            mRef.child("expirationDate").setValue(myListData.getExpirationDate());
                            mRef.child("id").setValue(myListData.getId());
                            mRef.child("isEligibleForChat").setValue(myListData.getIsEligibleForChat());
                            mRef.child("payment").setValue(myListData.getPayment());
                            mRef.child("role").setValue(myListData.getRole());
                            mRef.child("username").setValue(myListData.getUsername());


                            DatabaseReference pendingUsersRef = database.getReference().child("PendingUsers");
                            pendingUsersRef.child(myListData.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.getRef().removeValue();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        Query roomQuery = ref.orderByChild("email").equalTo(myListData.getEmail());
                        roomQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                String date = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy", new java.util.Date()));
//                                String expirationDate = addDays(date, duration);


                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    if (rbNo.isChecked()) {
                                        childSnapshot.getRef().child("payment").setValue("No");
                                    } else {
                                        childSnapshot.getRef().child("payment").setValue("Yes");
                                    }
//                                    childSnapshot.getRef().child("expireDays").setValue(duration + "");
                                    childSnapshot.getRef().child("expirationDate").setValue(duration + "");

                                }
                                dialog.cancel();
                                Intent intent = new Intent(v.getContext(), AdminDashActivity.class);
                                context.startActivity(intent);
                                ((Activity) context).finish();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("thisIsTheError", databaseError.getMessage());
                            }
                        });

                    }
                });
            }
        });

        holder.ivchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiver = myListData.id;
                Log.d("tag", "checkingid" + receiver);

                Intent go = new Intent(context, messagescreen.class);
                go.putExtra("userid", receiver);
                go.putExtra("name", myListData.getUsername());
                context.startActivity(go);
                ((Activity) context).finish();

            }
        });

        holder.ivadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//                LayoutInflater inflater = getLayoutInflater();
//
//                final View dialogView = inflater.inflate(R.layout.drink_list, null);
//
//                dialogBuilder.setView(dialogView);
//
//                //Necessary to get list
//                listViewDrinksMain = (ListView) dialogView.findViewById(R.id.listViewDrinksMain);
//                drinks = new ArrayList<>();
//
//                String id = databaseVenues.push().getKey();
//                databaseDrinks = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference("drinks").child(id);
//
//                dialogBuilder.setTitle(venueName + "Deals");
//
//                final AlertDialog alertDialog = dialogBuilder.create();
//                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserAdapterUserName, tvUserAdapterEmailId,
                tvDialogUpdatePaymentExpDate;
        ImageView ivDeleteUser, ivApproveUser, ivchat, ivadd, ivremove;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvUserAdapterUserName = (TextView) itemView.findViewById(R.id.tvUserAdapterUserName);
            this.tvUserAdapterEmailId = (TextView) itemView.findViewById(R.id.tvUserAdapterEmailId);
            this.tvDialogUpdatePaymentExpDate = (TextView) itemView.findViewById(R.id.tvDialogUpdatePaymentExpDate);
            this.ivDeleteUser = (ImageView) itemView.findViewById(R.id.ivDeleteUser);
            this.ivApproveUser = (ImageView) itemView.findViewById(R.id.ivApproveUser);
            this.ivchat = (ImageView) itemView.findViewById(R.id.ivchat);
            this.ivadd = (ImageView) itemView.findViewById(R.id.ivadd);
            this.ivremove = (ImageView) itemView.findViewById(R.id.ivsub);
        }

    }

    public void filterList(ArrayList<UserModelClass> filteredList) {
        listdata = filteredList;
        notifyDataSetChanged();
    }

    public String addDays(String date, String days) {
        String dt = date;  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, Integer.parseInt(days.replace(" Days", "")));  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        String output = sdf1.format(c.getTime());
        return output;
    }

    private void updateLabel(TextView textView) {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        textView.setText(dateFormat.format(myCalendar.getTime()));
    }
}
