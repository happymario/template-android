package com.victoria.bleled.data.remote.myservice;


import com.victoria.bleled.data.model.BaseModel;

import java.util.List;

public class ResponsePageList<T> extends BaseModel {
    private int total_count;
    private int total_page;
    private boolean is_last;
    private List<T> contents = null;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public boolean isIs_last() {
        return is_last;
    }

    public void setIs_last(boolean is_last) {
        this.is_last = is_last;
    }

    public List<T> getContents() {
        return contents;
    }

    public void setContents(List<T> contents) {
        this.contents = contents;
    }
}
