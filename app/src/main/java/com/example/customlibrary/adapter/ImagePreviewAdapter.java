package com.example.customlibrary.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.customlibrary.R;
import com.example.customlibrary.entity.ChosenImage;
import com.example.customlibrary.entity.ChosenVideo;

import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.MyViewHolder> {

    private Context context;
    private List<ChosenImage> chosenImages=null;
    private AdapterCallback adapterCallback;


    public ImagePreviewAdapter(Context context, List<ChosenImage> images ,AdapterCallback adapterCallback) {
        this.context = context;
        this.chosenImages = images;
        this.adapterCallback=adapterCallback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.image_type, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(chosenImages.get(position).getQueryUri())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        if(chosenImages!=null)
        return chosenImages.size();
        else
            return 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterCallback.onImageSelecte(getAdapterPosition());
                }
            });
        }

    }

    public interface AdapterCallback {
        void onImageSelecte(int position);
    }

}
