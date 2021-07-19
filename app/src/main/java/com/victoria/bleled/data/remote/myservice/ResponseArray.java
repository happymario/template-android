package com.victoria.bleled.data.remote.myservice;

import com.victoria.bleled.data.model.BaseModel;

import java.util.List;

public class ResponseArray<T> extends BaseModel {
    public List<T> contents = null;
}
