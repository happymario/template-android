package com.mario.template.helper.gallary;


import java.util.ArrayList;

/**
 * Created by boss1088 on 8/22/16.
 */
public class Folder {

    private String folderName;
    private ArrayList<Gallary> images;

    public Folder(String bucket) {
        folderName = bucket;
        images = new ArrayList<>();
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public ArrayList<Gallary> getImages() {
        return images;
    }

    public void setImages(ArrayList<Gallary> images) {
        this.images = images;
    }
}
