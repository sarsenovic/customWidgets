package com.example.testcustomwidgetslibrary.fileDirectory.listener;

import com.example.testcustomwidgetslibrary.fileDirectory.model.FileModel;

import java.io.Serializable;

public interface ActivityListener extends Serializable {
    void onDirectoryChanged(String absolutePath);
    void onFileSelect(FileModel fileModel);
    void onBackButtonPressed(String absolutePath);
}
