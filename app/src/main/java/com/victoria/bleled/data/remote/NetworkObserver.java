package com.victoria.bleled.data.remote;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.victoria.bleled.R;
import com.victoria.bleled.app.MyApplication;
import com.victoria.bleled.data.remote.myservice.BaseResponse;
import com.victoria.bleled.util.CommonUtil;
import com.victoria.bleled.util.arch.network.NetworkResult;

public class NetworkObserver<T> implements Observer<NetworkResult<T>> {
    private Context context = null;
    private boolean showError = true;

    public NetworkObserver() {
        this.showError = false;
        this.context = MyApplication.Companion.getGlobalApplicationContext();
    }

    public NetworkObserver(Context context, boolean showError) {
        this.context = context;
        this.showError = showError;
    }

    @Override
    public void onChanged(NetworkResult<T> result) {
        if (result == null) {
            return;
        }

        if (result.status.getValue() == NetworkResult.Status.success && result.data instanceof BaseResponse) {
            BaseResponse data = (BaseResponse) result.data;
            int error = data.getResult();
            if (error != ApiException.SUCCESS) {
                result.error = new ApiException(error, data.getMsg(), data.getReason());
                result.status = new MutableLiveData<>(NetworkResult.Status.error);
            }
        }

        // 재시도 등 UI처리가 필요할때 여기서 공통처리 진행
        if (result.status.getValue() == NetworkResult.Status.error && showError) {
            onError(result);
        }
    }

    protected void onError(NetworkResult<T> result) {
        if (result.error != null && result.error instanceof ApiException) {
            ApiException exception = (ApiException) result.error;
            CommonUtil.showToast(context, exception.getMsg() != null ? exception.getMsg() : context.getResources().getString(R.string.server_problem));
        } else {
            CommonUtil.showToast(context, R.string.network_connect_error);
        }
    }

    public String getErrStr(NetworkResult<T> result) {
        return getErrorMsg(context, result);
    }

    public static <T> Throwable getErrorException(NetworkResult<T> result) {
        if (result.status.getValue() == NetworkResult.Status.success && result.data instanceof BaseResponse) {
            BaseResponse data = (BaseResponse) result.data;
            int error = data.getResult();
            if (error != ApiException.SUCCESS) {
                result.error = new ApiException(error, data.getMsg(), data.getReason());
                result.status = new MutableLiveData<>(NetworkResult.Status.error);
            }
        }
        return result.error;
    }

    public static <T> String getErrorMsg(Context p_context, NetworkResult<T> result) {
        Throwable error = getErrorException(result);
        if (result.error != null && result.error instanceof ApiException) {
            ApiException exception = (ApiException) result.error;
            return exception.getMsg() != null ? exception.getMsg() : p_context.getResources().getString(R.string.server_problem);
        } else {
            return p_context.getResources().getString(R.string.network_connect_error);
        }
    }

}
