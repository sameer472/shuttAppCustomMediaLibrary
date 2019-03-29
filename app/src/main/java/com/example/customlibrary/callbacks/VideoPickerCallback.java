package com.example.customlibrary.callbacks;


import com.example.customlibrary.entity.ChosenVideo;

import java.util.List;

/**
 * Created by kbibek on 2/23/16.
 */
public interface VideoPickerCallback extends Pickercallback {
    void onVideosChosen(List<ChosenVideo> videos);
}
