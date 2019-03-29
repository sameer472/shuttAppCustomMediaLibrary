package com.example.customlibrary.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

public class ImagePreviewBottomIconAdapter extends RecyclerView.Adapter<ImagePreviewBottomIconAdapter.ImagePreviewIconViewHolder> {
    private Context context;
    private List<ChosenImage> chosenImages=null;
    private ImageIconInterface imageIconInterface;
    private int row_index=-1;

    public ImagePreviewBottomIconAdapter(Context context, List<ChosenImage> chosenImages,ImageIconInterface imageIconInterface) {
        this.context = context;
        this.chosenImages = chosenImages;
        this.imageIconInterface=imageIconInterface;
    }

    public class ImagePreviewIconViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        View view;

        public ImagePreviewIconViewHolder(final View itemView) {
            super(itemView);
            this.view=itemView;
            imageView=itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // imageView.setBackground(R.drawable.icon_border1);
                    //itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.icon_border1));
                    imageIconInterface.onImageSelectIcon(getAdapterPosition());
                }
            });
        }
    }


    @NonNull
    @Override
    public ImagePreviewIconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_image_type, parent, false);
        return new ImagePreviewBottomIconAdapter.ImagePreviewIconViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ImagePreviewIconViewHolder holder, final int position) {

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ChosenImage image : chosenImages) {
                    image.setIsSelected(0);
                }
                chosenImages.get(position).setIsSelected(1);
                notifyDataSetChanged();
                imageIconInterface.onImageSelectIcon(position);
            }
        });
        if(chosenImages.get(position).getIsSelected()==1){

            holder.view.setBackground(ContextCompat.getDrawable(context, R.drawable.icon_border1));
        }
        else
        {
            holder.view.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));
            //holder.view.setBackground(null);
        }



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

    public interface ImageIconInterface {
        void onImageSelectIcon(int position);
    }
}
