package com.example.customlibrary.callbacks;

import com.example.customlibrary.entity.ChosenImage;

import java.util.List;

public interface ImagePickerCallback extends Pickercallback {
    void onImagesChosen(List<ChosenImage> images);

}
