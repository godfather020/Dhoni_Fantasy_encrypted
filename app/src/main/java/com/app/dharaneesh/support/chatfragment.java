package com.app.dharaneesh.support;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.app.dharaneesh.R;
import com.app.dharaneesh.UserModelClass;
import com.app.dharaneesh.adapter.ChatListAdapter;
import com.app.dharaneesh.holder.upholder;
import com.app.dharaneesh.models.mainchat;
import com.app.dharaneesh.util.Constant;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class chatfragment extends Fragment {
    View vv;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<mainchat> option;
    FirebaseRecyclerAdapter<mainchat, upholder> adapter;
    DatabaseReference uoref;
    AlertDialog dialog;
    String role;
    ArrayList<UserModelClass> myListData;
    ChatListAdapter chatListAdapter;
    EditText svChatFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vv = inflater.inflate(R.layout.fragment_chatfragment, container, false);


        SharedPreferences prefs = getActivity().getSharedPreferences("My App", MODE_PRIVATE);
        String id = prefs.getString("firUserId", "No name defined");
        role = prefs.getString("role", "no");
        Log.d("TAG", "checkrole" + role);
        String mailId = prefs.getString("username", "no");

        myListData = new ArrayList<>();

        recyclerView = vv.findViewById(R.id.chatrecyclerview);
        svChatFragment = vv.findViewById(R.id.svChatFragment);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatListAdapter = new ChatListAdapter(getContext(), myListData, role, recyclerView);
        recyclerView.setAdapter(chatListAdapter);


        svChatFragment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        readheads();
        // Inflate the layout for this fragment
        return vv;
    }

    private void filter(String text) {
        ArrayList<UserModelClass> filteredList = new ArrayList<>();

        for (UserModelClass item : myListData) {
            if (item.getUsername().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        chatListAdapter.filterList(filteredList);
    }

    private void readheads() {
        if (role.matches("user")) {
            DatabaseReference reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();
            Query query = reference.child("Users").orderByChild("role").equalTo("admin");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (myListData != null) {
                        myListData.clear();
                    }
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserModelClass userModelClass = dataSnapshot.getValue(UserModelClass.class);
                        if (userModelClass.getIsEligibleForChat() != null) {
                            if (userModelClass.getIsEligibleForChat().matches("true")) {
                                getMyUnreadCount(userModelClass);
                            }
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else if (role.matches("admin")) {
            DatabaseReference reference = FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference();
            Query query = reference.child("Users").orderByChild("role").equalTo("user");
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (myListData != null) {
                        myListData.clear();
                    }
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModelClass userModelClass = snapshot.getValue(UserModelClass.class);
                    getMyUnreadCount(userModelClass);
//                        myListData.add(userModelClass);
//                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Toast.makeText(getContext(), "this1", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Toast.makeText(getContext(), "this2", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Toast.makeText(getContext(), "this3", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "this4", Toast.LENGTH_SHORT).show();
                }
            });
        }

//        option = new FirebaseRecyclerOptions.Builder<mainchat>()
//                .setQuery(uoref, mainchat.class).build();
//
//
//        adapter = new FirebaseRecyclerAdapter<mainchat, upholder>(option) {
//            @Override
//            protected void onBindViewHolder(@NonNull upholder holder, int position, @NonNull mainchat model) {
//                if (role.matches("admin")) {
//                    FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("adminchats").child(FirebaseAuth.getInstance().getUid()).child(model.getSenderid()).child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot snapshot) {
//                            if (snapshot.getValue() == null) {
//                                holder.unreadcount.setVisibility(View.INVISIBLE);
//                            } else {
//                                holder.unreadcount.setVisibility(View.VISIBLE);
//                                int unreadCount = ((Long) snapshot.getValue()).intValue();
//                                holder.unreadcount.setText((String.valueOf(unreadCount)));
//                                Log.i("unreadCount", String.valueOf(snapshot.getValue()) + FirebaseAuth.getInstance().getUid());
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                        }
//                    });
//                } else {
//                    Log.i("unreadCount", model.getSenderid() + "," + FirebaseAuth.getInstance().getUid() + "m");
//                    try {
//                        FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(FirebaseAuth.getInstance().getUid()).child(model.getSenderid()).child("unreadCount").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot snapshot) {
//                                if (snapshot.getValue() == null) {
//                                    holder.unreadcount.setVisibility(View.INVISIBLE);
//                                } else {
//                                    holder.unreadcount.setVisibility(View.VISIBLE);
//                                    int unreadCount = ((Long) snapshot.getValue()).intValue();
//                                    holder.unreadcount.setText((String.valueOf(unreadCount)));
//                                    Log.i("unreadCount", String.valueOf(snapshot.getValue()) + FirebaseAuth.getInstance().getUid());
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                                databaseError.getMessage();
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
////                if(model.getChattype().matches("chat")) {
//                holder.chatname.setText(model.getSendername());
//                holder.v.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String receiverid = model.getSenderid();
//                        Log.d("ATG", "checking12" + model.getSenderid());
//                        Intent go = new Intent(getActivity(), messagescreen.class);
//                        go.putExtra("userid", receiverid);
//                        go.putExtra("chattype", "chat");
//                        go.putExtra("messageparent", getRef(holder.getAbsoluteAdapterPosition()).getKey());
//                        startActivity(go);
//                    }
//                });
////                }
//            }
//
//            @NonNull
//            @Override
//            public upholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlist, parent, false);
////                v.setOnClickListener(clickListener);
//                return new upholder(v);
//            }
//        };
//
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                adapter.startListening();
////                recyclerView.setAdapter(adapter);
//            }
//        }, 1000);

    }


    public void getMyUnreadCount(UserModelClass userModelClass) {
        if (role.equals("admin")) {

            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("adminchats").child(FirebaseAuth.getInstance().getUid()).child(userModelClass.getId()).child("unreadCount").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
//                        unreadCount.setVisibility(View.INVISIBLE);
                        userModelClass.setUnreadCount("0");
                    } else {
//                        unreadCount.setVisibility(View.VISIBLE);
                        int unreadCountNum = ((Long) snapshot.getValue()).intValue();
//                        unreadCount.setText((String.valueOf(unreadCountNum)));
                        Log.i("unreadCount", String.valueOf(snapshot.getValue()) + FirebaseAuth.getInstance().getUid());

                        userModelClass.setUnreadCount(String.valueOf(unreadCountNum));
                    }


                    myListData.add(userModelClass);

                    Collections.sort(myListData, new Comparator<UserModelClass>() {
                        public int compare(UserModelClass obj1, UserModelClass obj2) {
                            // ## Ascending order
                            return obj2.getUnreadCount().compareToIgnoreCase(obj1.getUnreadCount()); // To compare string values
                            // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                            // ## Descending order
                            // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                            // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                        }
                    });

                    chatListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            FirebaseDatabase.getInstance(Constant.FIREBASE_DATABASE).getReference().child("chatheads").child("chats").child(FirebaseAuth.getInstance().getUid()).child(userModelClass.getId()).child("unreadCount").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot.getValue() == null) {
//                        unreadCount.setVisibility(View.INVISIBLE);
                        userModelClass.setUnreadCount("0");
                    } else {
//                        unreadCount.setVisibility(View.VISIBLE);
                        int unreadCountNum = ((Long) snapshot.getValue()).intValue();
//                        unreadCount.setText((String.valueOf(unreadCountNum)));
                        Log.i("unreadCount", String.valueOf(snapshot.getValue()) + FirebaseAuth.getInstance().getUid());

                        userModelClass.setUnreadCount(String.valueOf(unreadCountNum));

                    }
                    myListData.add(userModelClass);
                    chatListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    databaseError.getMessage();
                }
            });
        }
    }
}