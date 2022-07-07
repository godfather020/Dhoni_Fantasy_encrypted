package com.app.dharaneesh.support;


import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dharaneesh.R;
import com.app.dharaneesh.UserModelClass;
import com.app.dharaneesh.holder.upholder;
import com.app.dharaneesh.util.Constant;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class groups extends Fragment {

    View vv;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<UserModelClass> option;
    FirebaseRecyclerAdapter<UserModelClass, upholder> adapter;
    DatabaseReference uoref;
    String role;
    AlertDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vv = inflater.inflate(R.layout.fragment_chatfragment, container, false);
        recyclerView = vv.findViewById(R.id.chatrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        SharedPreferences prefs = getActivity().getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("firUserId", "No name defined");
        role = prefs.getString("role", "no");

        readheads();
        return vv;

    }

    private void readheads() {

        if(role.matches("admin")){
            uoref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("admingroups");
//            uoref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups");
        }else if(role.matches("user")){
            uoref = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups");//.child(FirebaseAuth.getInstance().getUid());
        }

        option = new FirebaseRecyclerOptions.Builder<UserModelClass>()
                .setQuery(uoref, UserModelClass.class).build();


        adapter = new FirebaseRecyclerAdapter<UserModelClass, upholder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull upholder holder, int position, @NonNull UserModelClass model) {

//                if(role.matches("admin")) {
////                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("adminchats").child(FirebaseAuth.getInstance().getUid()).child(model.getSenderid()).child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot snapshot) {
////                            if (snapshot.getValue() == null) {
////                                holder.unreadcount.setVisibility(View.INVISIBLE);
////                            } else {
////                                int unreadCount = ((Long) snapshot.getValue()).intValue();
////                                holder.unreadcount.setText((String.valueOf(unreadCount)));
////                                Log.i("unreadCount", String.valueOf(snapshot.getValue()) + FirebaseAuth.getInstance().getUid());
////                            }
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////                        }
////                    });
//                }
//                else{

                    Log.i("unreadCount", model.getGroupname() + "," + FirebaseAuth.getInstance().getUid() + "," + model.getId());
                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(model.getGroupname()).child("users").child(FirebaseAuth.getInstance().getUid()).child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.getValue() == null) {
                                holder.unreadcount.setVisibility(View.INVISIBLE);
                            } else {
                                holder.unreadcount.setVisibility(View.VISIBLE);
                                int unreadCount = ((Long) snapshot.getValue()).intValue();
                                holder.unreadcount.setText((String.valueOf(unreadCount)));
                                Log.i("unreadCount", String.valueOf(snapshot.getValue()) + FirebaseAuth.getInstance().getUid());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
//                }

                if(role.matches("user")) {
                    Log.d("TAG", "checkid"+model.getId());
                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("groups").child("usergroups").child(model.getGroupname()).child("users")
                            .child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                holder.chatname.setText(model.getGroupname());
                                holder.v.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent go = new Intent(getActivity(), groupmessagescreen.class);
                                        go.putExtra("userid",model.getGroupname() );
                                        go.putExtra("chattype", "group");
                                        go.putExtra("topic", model.getTopic());
//                                        go.putExtra("groupparent",key );
                                        startActivity(go);
                                    }
                                });
                            }else{
                                holder.mainhead.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//                    if (FirebaseAuth.getInstance().getUid().matches(model.getId())) {

//
//                        holder.v.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent go = new Intent(getActivity(), groupmessagescreen.class);
//                                go.putExtra("userid",getRef(holder.getAbsoluteAdapterPosition()).getKey() );
//                                go.putExtra("chattype", "group");
//
//                                startActivity(go);
//
//                            }
//                        });
//                    }
                }else if(role.matches("admin")){

                    holder.chatname.setText(model.getGroupname());
                    String key = getRef(holder.getAbsoluteAdapterPosition()).getKey();
                    holder.v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent go = new Intent(getActivity(), groupmessagescreen.class);
                            go.putExtra("userid",model.getGroupname() );
                            go.putExtra("chattype", "group");
                            go.putExtra("groupparent",key );
                            go.putExtra("topic", model.getTopic());
                            startActivity(go);

                        }
                    });

                }




            }

            @NonNull
            @Override
            public upholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlist,parent, false);
//                v.setOnClickListener(clickListener);
                return new upholder(v);
            }
        };


        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }


}
