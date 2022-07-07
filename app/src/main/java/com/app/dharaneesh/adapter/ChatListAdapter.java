package com.app.dharaneesh.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dharaneesh.R;
import com.app.dharaneesh.UserModelClass;
import com.app.dharaneesh.support.messagescreen;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    Context context;
    ArrayList<UserModelClass> imagesList;
    String role;
    RecyclerView recyclerView;

    public ChatListAdapter(Context context, ArrayList<UserModelClass> imagesList, String role, RecyclerView recyclerView) {
        this.context = context;
        this.imagesList = imagesList;
        this.recyclerView = recyclerView;
        this.role = role;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlist, parent, false);
        return new ChatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        UserModelClass mylist = imagesList.get(position);

        holder.chatname.setText(mylist.getUsername());

        if (role.equals("admin")) {
            holder.memberFrom.setVisibility(View.VISIBLE);
            holder.memberFrom.setText("Member till: " + mylist.getExpirationDate());
        }

        if (mylist.getUnreadCount().equals("0")) {
            holder.unreadcount.setVisibility(View.GONE);
        } else {
            holder.unreadcount.setVisibility(View.VISIBLE);
            holder.unreadcount.setText(mylist.getUnreadCount());
        }
//        getMyUnreadCount(mylist.getId(), holder.unreadcount);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(context, messagescreen.class);
                go.putExtra("userid", mylist.getId());
                go.putExtra("chattype", "chat");
                go.putExtra("messageparent", mylist.getId());
                ((Activity) context).startActivity(go);
            }
        });

        removeExtraDateLayout(position, holder.itemView, imagesList, mylist.getId());


    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatname, unreadcount, memberFrom;

        public ViewHolder(View itemView) {
            super(itemView);
            chatname = itemView.findViewById(R.id.chatname);
            unreadcount = itemView.findViewById(R.id.unreadcount);
            memberFrom = itemView.findViewById(R.id.memberFrom);
        }
    }


    public void filterList(ArrayList<UserModelClass> filteredList) {
        imagesList = filteredList;
        notifyDataSetChanged();
    }


    public void removeItem(int position) {
        imagesList.remove(position);
    }

    public void removeExtraDateLayout(int position, View layout, List<UserModelClass> list, String dateTime) {
        if (position == 0) {
            layout.setVisibility(View.VISIBLE);
        } else {
            UserModelClass previous = list.get(position - 1);

            String previousDate = null;
            String otherDate = null;

            previousDate = previous.getId();


            otherDate = dateTime;


            Log.d("Main Date", previous + "");
            Log.d("Prev Date", otherDate + "");


            if (previousDate.equals(otherDate)) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        imagesList.remove(position);
                        notifyDataSetChanged();
                    }
                });
            } else {
                layout.setVisibility(View.VISIBLE);
            }
        }
    }


}