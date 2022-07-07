package com.app.dharaneesh.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dharaneesh.R;
import com.app.dharaneesh.models.Messages;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final Date date = new Date();
    private static final int
            MSG_TYPE_LEFT = 0,
            MSG_TYPE_RIGHT = 1;
    private final DateFormat
            hourMinuteFormat = new SimpleDateFormat("h:mm a", Locale.getDefault()),
            withoutYearFormat = new SimpleDateFormat("h:mm a MMM dd", Locale.getDefault()),
            formatter = new SimpleDateFormat("h:mm a yyyy MMM dd", Locale.getDefault()),
            todayYearFormat = new SimpleDateFormat("yyyy", Locale.getDefault()),
            todayYearMonthDayFormat = new SimpleDateFormat("yyyy MMM dd", Locale.getDefault());

    private final ArrayList<Messages> messages;
    private final String currentUid;
    private boolean longCLickEnabled = true;

    public ChatAdapter(ArrayList<Messages> messages) {
        this.messages = messages;
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void disableLongClick() {
        longCLickEnabled = false;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_right_sent, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_left_received, parent, false));
        }
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).hashCode();
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.addMessage(messages.get(position));

//            if(holder.getClass() == senderholder.class)

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (messages.get(position).getSenderid().equals(currentUid)) {
            return MSG_TYPE_RIGHT;
        }

        return MSG_TYPE_LEFT;
    }

//        void showMessageDeleteDialog(ChatAdapter messageMap) {
//            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
//            alert.setTitle("Do you want to delete this message?");
//            alert.setPositiveButton("Delete", (dialog, which) -> {
//                deleteMessageListener.deleteMessage(messageMap, dialog);
//            });
//            alert.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//            alert.create().show();
//        }

    private String getTimeFormatted(long time) {

        if (time < 1000000000000L) {
            time *= 1000;
        }
        if (todayYearMonthDayFormat.format(date)
                .equals(todayYearMonthDayFormat.format(time))) {
            return hourMinuteFormat.format(time);

        } else if (todayYearFormat.format(date).equals(todayYearFormat.format(time))) {
            return withoutYearFormat.format(time);
        } else {
            return formatter.format(time);
        }
    }

//        public interface DeleteMessageListener {
//            void deleteMessage(MessageMap messageMap, DialogInterface dialog);
//        }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tv_chat_received, tv_chat_time_received;
        private boolean timeIsVisible;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_chat_received = itemView.findViewById(R.id.tv_chat_received);
            tv_chat_time_received = itemView.findViewById(R.id.tv_chat_time_received);
            itemView.setOnClickListener(this);
        }

        void addMessage(Messages message) {

            if (message == null)
                return;

            tv_chat_received.setText(message.getMessage());
            Log.d("TAG", "checkingmessages"+message.getMessage());


            if (timeIsVisible) {
                tv_chat_time_received.setText(getTimeFormatted(message.getTimestamp()));
                tv_chat_time_received.setVisibility(View.VISIBLE);
            } else {
                tv_chat_time_received.setVisibility(View.GONE);
            }

        }

        @Override
        public void onClick(View view) {

            if (tv_chat_time_received.getVisibility() == View.GONE) {
                tv_chat_time_received.setText(getTimeFormatted(messages.get(getBindingAdapterPosition()).getTimestamp()));
                tv_chat_time_received.setVisibility(View.VISIBLE);
                timeIsVisible = true;
            } else {
                tv_chat_time_received.setVisibility(View.GONE);
                timeIsVisible = false;
            }

        }

    }

}