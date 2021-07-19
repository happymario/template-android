package com.victoria.bleled.data.remote.myservice;


import com.victoria.bleled.data.model.BaseModel;

import java.util.List;

public class ResponsePipeList<T> extends BaseModel {
    private int first_uid; // 첫번째 채팅 UID
    private int last_uid; // 마지막 채팅 UID
    private List<T> contents = null;

    public int getFirst_uid() {
        return first_uid;
    }

    public void setFirst_uid(int first_uid) {
        this.first_uid = first_uid;
    }

    public int getLast_uid() {
        return last_uid;
    }

    public void setLast_uid(int last_uid) {
        this.last_uid = last_uid;
    }

    public List<T> getContents() {
        return contents;
    }

    public void setContents(List<T> contents) {
        this.contents = contents;
    }
}
