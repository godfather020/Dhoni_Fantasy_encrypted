package com.app.dharaneesh.holder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dharaneesh.R;


public class upholder extends RecyclerView.ViewHolder{
    public TextView chatname, unreadcount;
    public RelativeLayout mainhead;
    public View v;

    public upholder(@NonNull View itemView) {
        super(itemView);

        chatname = itemView.findViewById(R.id.chatname);
        unreadcount = itemView.findViewById(R.id.unreadcount);
        mainhead = itemView.findViewById(R.id.mainhead);


        v = itemView;

    }
}
