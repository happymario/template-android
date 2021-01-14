package com.victoria.bleled.data.remote.response;

import com.victoria.bleled.data.remote.ApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseResponse<T> implements Serializable {
    private int result = ApiException.SUCCESS;
    private String msg = "";
    private String reason = "";
    private T data = null;

    public static <T> BaseResponse<List<T>> convertFromResponseArray(BaseResponse<ResponseArray<T>> response) {
        BaseResponse<List<T>> resultResponse = new BaseResponse<List<T>>();
        if (response.getData() == null) {
            resultResponse.setData(new ArrayList<>());
        } else {
            resultResponse.setData(response.getData().contents);
        }
        resultResponse.setMsg(response.getMsg());
        resultResponse.setReason(response.getReason());
        resultResponse.setResult(response.getResult());

        return resultResponse;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
