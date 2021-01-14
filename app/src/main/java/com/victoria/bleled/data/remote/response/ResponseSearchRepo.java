package com.victoria.bleled.data.remote.response;


import com.victoria.bleled.data.model.BaseModel;
import com.victoria.bleled.data.model.ModelUser;

import java.util.List;

public class ResponseSearchRepo extends BaseModel {
    public int total = 0;
    public List<ModelUser> items = null;
}
