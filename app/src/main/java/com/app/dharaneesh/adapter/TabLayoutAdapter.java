package com.app.dharaneesh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dharaneesh.R;
import com.app.dharaneesh.interfaces.TabLayoutInterface;

import java.util.ArrayList;

public class TabLayoutAdapter extends RecyclerView.Adapter<TabLayoutAdapter.ViewHolder> {
    ArrayList<String> tabNames;
    Context context;
    TabLayoutInterface tabLayoutInterface;
    private int lastCheckedPosition = 0;

    public TabLayoutAdapter(ArrayList<String> tabNames, Context context, TabLayoutInterface tabLayoutInterface) {
        this.tabNames = tabNames;
        this.context = context;
        this.tabLayoutInterface = tabLayoutInterface;

    }

    @NonNull
    @Override
    public TabLayoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.adapter_match_type, parent, false);
        TabLayoutAdapter.ViewHolder viewHolder = new TabLayoutAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TabLayoutAdapter.ViewHolder holder, int position) {

        String title = tabNames.get(position);

        holder.tvTitleTabName.setText(title);

        holder.tvTitleTabName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabLayoutInterface.onClick(title);
                lastCheckedPosition = position;
                notifyDataSetChanged();
            }
        });

        if (position == lastCheckedPosition) {
            holder.tvTitleTabName.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvTitleTabName.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorUserMain));
        } else {
            holder.tvTitleTabName.setTextColor(context.getResources().getColor(R.color.colorUserMain));
            holder.tvTitleTabName.setBackgroundTintList(null);
            holder.tvTitleTabName.setBackground(context.getResources().getDrawable(R.drawable.bg_match_type_unselect));
        }
    }

    @Override
    public int getItemCount() {
        return tabNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitleTabName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvTitleTabName = (TextView) itemView.findViewById(R.id.tvTitleTabName);
        }

    }
}
