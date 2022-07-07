package com.app.dharaneesh.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dharaneesh.R;
import com.app.dharaneesh.UserModelClass;
import com.app.dharaneesh.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class addmemberadapter extends RecyclerView.Adapter<addmemberadapter.ViewHolder> {
    private ArrayList<UserModelClass> listdata;
    Context context;
    String type, type1, groupname, topic;


    public addmemberadapter(ArrayList<UserModelClass> myListData, String type, String type1, String groupname, String topic, Context addmember) {
        this.listdata = myListData;
        this.context = addmember;
        this.type = type;
        this.type1 = type1;
        this.groupname = groupname;
        this.topic = topic;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.addmemberlayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull addmemberadapter.ViewHolder holder, int position) {

        UserModelClass mylist = listdata.get(position);
        Log.d("TAG", "checknewlist" + type1 + type);
        if (type.matches("addadmin")) {
            if (type1.matches("add")) {

                holder.tvUserAdapterUserName.setText(mylist.getUsername());
                holder.tvUserAdapterEmailId.setText(mylist.getEmail());

                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override

                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            DatabaseReference reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();

                            Query query = reference.child("Users").orderByChild("id").equalTo(listdata.get(position).getId());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // dataSnapshot is the "issue" node with all children with id 0
                                        mylist.setIsEligibleForChat("true");
                                        dataSnapshot.getRef().child(mylist.getId()).setValue(mylist);
                                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                            // do something with the individual "issues"
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

//                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(mylist.getId()).setValue(main).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Toast.makeText(context,  mylist.getUsername()+" Added as chat admin", Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        } else {
                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(mylist.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, mylist.getUsername() + " Removed as chat admin", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });

//                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(mylist.getId()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()) {
////                            else{
//                                holder.mainhead.setVisibility(View.GONE);
////                            }
//
//                        }else{
//
//                            holder.tvUserAdapterUserName.setText(mylist.getUsername());
//                            holder.tvUserAdapterEmailId.setText(mylist.getEmail());
////                            mainchat main = new mainchat();
////                            main.setSendername(mylist.getUsername());
////                            main.setSenderid(mylist.getId());
////
//                            HashMap<String, String> main = new HashMap<>();
//                            main.put("sendername", mylist.getUsername());
//                            main.put("senderid", mylist.getId());
//
//
//                            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                @Override
//                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                    if(isChecked){
//                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(mylist.getId()).setValue(main).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                Toast.makeText(context,  mylist.getUsername()+" Added as chat admin", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }else{
//                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(mylist.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                Toast.makeText(context,  mylist.getUsername()+" Removed as chat admin", Toast.LENGTH_SHORT).show();
//
//                                            }
//                                        });
//                                    }
//                                }
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
            } else if (type1.matches("remove")) {


                holder.tvUserAdapterUserName.setText(mylist.getUsername());
                holder.tvUserAdapterEmailId.setText(mylist.getEmail());


                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override

                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            DatabaseReference reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();

                            Query query = reference.child("Users").orderByChild("id").equalTo(listdata.get(position).getId());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // dataSnapshot is the "issue" node with all children with id 0
                                        mylist.setIsEligibleForChat("false");
                                        dataSnapshot.getRef().child(mylist.getId()).setValue(mylist);
                                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                            // do something with the individual "issues"
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

//                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(mylist.getId()).setValue(main).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Toast.makeText(context,  mylist.getUsername()+" Added as chat admin", Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        } else {
                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(mylist.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, mylist.getUsername() + " Removed as chat admin", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });

//                Log.d("tag", "checkpoint2");
//                try {
////                    if (mylist.getId()==null){
////                        listdata.remove(position);
////                    }
//
//                    if (mylist.getId() != null) {
//                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(mylist.getId()).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.exists()) {
//                                    holder.tvUserAdapterUserName.setText(mylist.getUsername());
//                                    holder.tvUserAdapterEmailId.setText(mylist.getEmail());
//                                    holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                        @Override
//                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                            if (isChecked) {
//                                                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(mylist.getId()).removeValue();
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

        } else if (type.matches("group")) {
            if (type1.matches("add")) {
                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child(mylist.getId()).child(groupname).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            holder.tvUserAdapterUserName.setText(mylist.getUsername());
                            holder.tvUserAdapterEmailId.setText(mylist.getEmail());
                            HashMap<String, String> data = new HashMap<>();
                            data.put("username", mylist.getUsername());
                            data.put("id", mylist.getId());
                            data.put("email", mylist.getEmail());
                            HashMap<String, String> data1 = new HashMap<>();
                            data1.put("groupname", groupname);
                            data1.put("topic", topic);
                            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(mylist.getId()).setValue(data);

                                                } else {
                                                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).setValue(data1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(mylist.getId()).setValue(data);

                                                            Toast.makeText(context, mylist.getUsername() + "added to " + groupname, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


//
                                    } else {

                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(mylist.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("groupslist").child(groupname)
//                                                        .child(mylist.getId()).removeValue();
                                                Toast.makeText(context, mylist.getUsername() + "removed from " + groupname, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (type1.matches("remove")) {

                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(mylist.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.tvUserAdapterUserName.setText(mylist.getUsername());
                            holder.tvUserAdapterEmailId.setText(mylist.getEmail());
                            HashMap<String, String> data = new HashMap<>();
                            data.put("username", mylist.getUsername());
                            data.put("id", mylist.getId());
                            data.put("email", mylist.getEmail());
                            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(mylist.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("groupslist").child(groupname)
//                                                        .child(mylist.getId()).removeValue();
                                                Toast.makeText(context, mylist.getUsername() + "removed from " + groupname, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(groupname).child("users").child(mylist.getId()).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("groupslist").child(groupname)
//                                                        .child(mylist.getId()).setValue(data);
                                                Toast.makeText(context, mylist.getUsername() + "added to " + groupname, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserAdapterUserName, tvUserAdapterEmailId;
        public CheckBox checkBox;
        public RelativeLayout mainhead;
//        ImageView ivDeleteUser, ivApproveUser, ivchat, ivadd, ivremove;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvUserAdapterUserName = (TextView) itemView.findViewById(R.id.tvUserAdapterUserName);
            this.tvUserAdapterEmailId = (TextView) itemView.findViewById(R.id.tvUserAdapterEmailId);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            this.mainhead = (RelativeLayout) itemView.findViewById(R.id.mainhead);
        }

    }

    public void filterList(ArrayList<UserModelClass> filteredList) {
        listdata = filteredList;
        notifyDataSetChanged();
    }
}
