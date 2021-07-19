package com.victoria.bleled.data.remote;

public class ApiException extends Exception {
    public static final int SUCCESS = 0; //성공
    public static final int ERR_NO_DATA = -1;

    private int code;
    private String msg;
    private String reason;

    public ApiException(int code, String msg, String reason) {
        super(msg);

        this.code = code;
        this.msg = msg;
        this.reason = reason;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
