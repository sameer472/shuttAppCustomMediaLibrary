package com.example.customlibrary.thread;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.customlibrary.ImagePreviewActivity;
import com.example.customlibrary.callbacks.ImagePickerCallback;
import com.example.customlibrary.entity.ChosenFile;
import com.example.customlibrary.entity.ChosenImage;
import com.example.customlibrary.exception.PickerException;
import com.example.customlibrary.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kbibek on 2/20/16.
 */
public final class ImageProcessorThread extends FileProcessorThread {
    private final static String TAG = ImageProcessorThread.class.getSimpleName();

    private boolean shouldGenerateThumbnails;
    private boolean shouldGenerateMetadata;

    private int maxImageWidth = -1;
    private int maxImageHeight = -1;
    private int quality = 100;
    private boolean allowMultiple=false;

    private ImagePickerCallback callback;
    private ImagePickerCallback mutipleImagePickerCallback;

    public ImageProcessorThread(Context context, List<ChosenImage> paths, int cacheLocation) {
        super(context, paths, cacheLocation);
    }

    public void setShouldGenerateThumbnails(boolean shouldGenerateThumbnails) {
        this.shouldGenerateThumbnails = shouldGenerateThumbnails;
    }

    public void setImagePickerCallback(ImagePickerCallback callback) {
        this.callback = callback;
    }

    public void setMultipleImagePickerCallback(ImagePickerCallback callback) {
        this.mutipleImagePickerCallback = callback;
    }

    @Override
    public void run() {
        super.run();
        postProcessImages();
        onDone();
    }

    private void onDone() {
        try {
            if (callback != null || mutipleImagePickerCallback!=null) {
                getActivityFromContext().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //callback.onImagesChosen((List<ChosenImage>) files);

                        if(allowMultiple){
                           // Toast.makeText(context,"all mutiple",Toast.LENGTH_LONG).show();
                             mutipleImagePickerCallback.onImagesChosen((List<ChosenImage>) files);

                        }else{
                            Intent intent = new Intent(context, ImagePreviewActivity.class);
                            intent.putParcelableArrayListExtra("data", (ArrayList<? extends Parcelable>) files);
                            // intent.putExtra("callback",callback);
                            //Toast.makeText(context,"all mutiple intent page",Toast.LENGTH_LONG).show();
                            context.startActivity(intent);
                        }



                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void postProcessImages() {
        for (ChosenFile file : files) {
            ChosenImage image = (ChosenImage) file;
            try {
                postProcessImage(image);
                image.setSuccess(true);
            } catch (PickerException e) {
                e.printStackTrace();
                image.setSuccess(false);
            }
        }
    }

    private ChosenImage postProcessImage(ChosenImage image) throws PickerException {
        if (maxImageWidth != -1 && maxImageHeight != -1) {
            image = ensureMaxWidthAndHeight(maxImageWidth, maxImageHeight, quality, image);
        }
        LogUtils.d(TAG, "postProcessImage: " + image.getMimeType());
        if (shouldGenerateMetadata) {
            try {
                image = generateMetadata(image);
            } catch (Exception e) {
                LogUtils.d(TAG, "postProcessImage: Error generating metadata");
                e.printStackTrace();
            }
        }
        if (shouldGenerateThumbnails) {
            image = generateThumbnails(image);
        }
        LogUtils.d(TAG, "postProcessImage: " + image);
        return image;
    }

    private ChosenImage generateMetadata(ChosenImage image) {
        image.setWidth(Integer.parseInt(getWidthOfImage(image.getOriginalPath())));
        image.setHeight(Integer.parseInt(getHeightOfImage(image.getOriginalPath())));
        image.setOrientation(getOrientation(image.getOriginalPath()));
        return image;
    }

    private ChosenImage generateThumbnails(ChosenImage image) throws PickerException {
        String thumbnailBig = downScaleAndSaveImage(image.getOriginalPath(), THUMBNAIL_BIG, quality);
        image.setThumbnailPath(thumbnailBig);
        String thumbnailSmall = downScaleAndSaveImage(image.getOriginalPath(), THUMBNAIL_SMALL, quality);
        image.setThumbnailSmallPath(thumbnailSmall);
        return image;
    }

    public void setShouldGenerateMetadata(boolean shouldGenerateMetadata) {
        this.shouldGenerateMetadata = shouldGenerateMetadata;
    }

    public void setOutputImageDimensions(int maxWidth, int maxHeight) {
        this.maxImageWidth = maxWidth;
        this.maxImageHeight = maxHeight;
    }

    public void setOutputImageQuality(int quality) {
        this.quality = quality;
    }

    public void setMultiple(boolean allowMultiple) {
        this.allowMultiple=allowMultiple;
    }
}
