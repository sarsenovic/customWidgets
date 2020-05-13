package com.example.testcustomwidgetslibrary.fileDirectory.model;

/**
 * Created by sasaarsenovic
 */
public class FileModel {
    private String absolutePath;
    private String directoryPath;
    private FileModelType fileModelType;
    private boolean isSelected;
    private int itemType;
    private boolean enabled;

    public FileModel(String absolutePath, FileModelType fileModelType, int itemType) {
        this(absolutePath, fileModelType);
        this.isSelected = false;
        this.itemType = itemType;
    }

    public FileModel(String absolutePath, FileModelType fileModelType) {
        this.absolutePath = absolutePath;
        this.directoryPath = this.absolutePath;
        this.fileModelType = fileModelType;
        this.isSelected = false;
        this.enabled = true;
    }

    public FileModel(String absolutePath, String directoryPath, FileModelType fileModelType) {
        this(absolutePath, fileModelType);
        this.directoryPath = directoryPath;
        this.enabled = true;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public FileModelType getFileModelType() {
        return fileModelType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public void setFileModelType(FileModelType fileModelType) {
        this.fileModelType = fileModelType;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
