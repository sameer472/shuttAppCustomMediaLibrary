package com.example.customlibrary.adapter;


import com.example.customlibrary.R;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.customlibrary.entity.ChosenImage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {


    private Context context;
    private List<ChosenImage> chosenImages=null;

    public ChatAdapter(Context context, List<ChosenImage> chosenImages) {
        this.context = context;
        this.chosenImages = chosenImages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_type, parent, false);
        return new ChatAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(chosenImages.get(position).getQueryUri())
                .into(holder.imageview);
    }

    @Override
    public int getItemCount() {
        if(chosenImages!=null)
            return chosenImages.size();
        else
            return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview=itemView.findViewById(R.id.imageview);
        }
    }
}
