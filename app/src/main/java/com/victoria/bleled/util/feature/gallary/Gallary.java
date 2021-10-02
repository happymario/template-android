package com.victoria.bleled.util.feature.gallary;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.databinding.BaseObservable;

public class Gallary extends BaseObservable implements Parcelable {

    private long id;
    private String name;
    private String path;
    private boolean is_select;
    private int num = 0;
    private String content;

    public Gallary(long id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getImageUri() {
        Uri contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
        );
        return contentUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gallary image = (Gallary) o;
        return image.getPath().equalsIgnoreCase(getPath());
    }

    /* --------------------------------------------------- */
    /* > Parcelable */
    /* --------------------------------------------------- */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeInt(this.num);
        dest.writeString(this.content);
        dest.writeInt((this.is_select ? 1 : 0));
    }

    protected Gallary(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.path = in.readString();
        this.num = in.readInt();
        this.content = in.readString();
        this.is_select = in.readInt() == 0 ? false : true;
    }

    public static final Creator<Gallary> CREATOR = new Creator<Gallary>() {
        @Override
        public Gallary createFromParcel(Parcel source) {
            return new Gallary(source);
        }

        @Override
        public Gallary[] newArray(int size) {
            return new Gallary[size];
        }
    };

    public boolean isIs_select() {
        return is_select;
    }

    public void setIs_select(boolean is_select) {
        this.is_select = is_select;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
