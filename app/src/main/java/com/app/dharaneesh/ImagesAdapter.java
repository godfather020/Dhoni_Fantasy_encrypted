package com.app.dharaneesh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

/*This adapter is required only if you are going to show the list of selected images*/
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

    Context context;
    List<ImagesModel> imagesList;

    public ImagesAdapter(Context context, List<ImagesModel> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picked_img_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagesList.remove(position);
                notifyDataSetChanged();
            }
        });

        Picasso.with(context).load(String.valueOf(imagesList.get(position).getImageUrl())).into(holder.imgSetData);

    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView btnRemove, imgSetData;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            btnRemove = itemView.findViewById(R.id.imgDeleteSelectedImage);
            imgSetData = itemView.findViewById(R.id.imgSetData);
        }
    }
}