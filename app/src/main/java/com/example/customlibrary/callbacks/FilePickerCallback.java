package com.example.customlibrary.callbacks;

import com.example.customlibrary.entity.ChosenFile;

import java.util.List;

/**
 * Created by kbibek on 2/23/16.
 */
public interface FilePickerCallback extends Pickercallback {
    void onFilesChosen(List<ChosenFile> files);
}
