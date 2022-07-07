package com.app.dharaneesh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.watermark.androidwm.WatermarkBuilder;
import com.watermark.androidwm.bean.WatermarkText;

import java.net.URL;
import java.util.List;

public class ImagesHomeworkAdapter extends RecyclerView.Adapter<ImagesHomeworkAdapter.ImageViewHolder> {

    Context context;
    List<ImageHomeworkModel> imagesList;
    String email;
    WatermarkText watermarkText;

    public ImagesHomeworkAdapter(Context context, List<ImageHomeworkModel> imagesList, String email) {
        this.context = context;
        this.imagesList = imagesList;
        this.email = email;

        String textForWaterMark = email.replace("@gmail.com", "");
        watermarkText = new WatermarkText(textForWaterMark)
                .setPositionX(2)
                .setPositionY(2)
                .setTextColor(Color.WHITE)
                .setTextFont(R.font.quicksand_medium)
                .setTextAlpha(180)
                .setRotation(50)
                .setTextSize(10);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picked_img_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.btnRemove.setVisibility(View.GONE);

        //        Picasso.with(context).lo/ad(String.valueOf(imagesList.get(position).getUrls())).into(holder.imgSetData);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imagesList.get(position).getUrls());
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    if (email != null && !email.equals("")) {
//                        Bitmap waterMarkImage = mark(image, email, 60, false);
                        holder.imgSetData.post(new Runnable() {
                            @Override
                            public void run() {
                                WatermarkBuilder
                                        .create(context, image)
                                        .loadWatermarkText(watermarkText) // use .loadWatermarkImage(watermarkImage) to load an image.
                                        .setTileMode(true)
                                        .getWatermark()
                                        .setToImageView(holder.imgSetData);
                            }
                        });
                    } else {
                        holder.imgSetData.setImageBitmap(image);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
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
