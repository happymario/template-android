package com.mario.template.helper.thirdparty;


import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
//import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitHelper {
    public static MultipartBody.Part getUploadParam(String path) {
        if (path != null && path.isEmpty() == false) {
            File file = new File(path);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            return MultipartBody.Part.createFormData("uploadfile", file.getName(), requestFile);
        }

        return null;
    }

    public static List<MultipartBody.Part> getUploadParam(List<String> filePaths) {
        List<MultipartBody.Part> arrMultipartBody = new ArrayList<>();
        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("uploadfile[]", file.getName(), requestFile);
            arrMultipartBody.add(multipartBody);
        }

        return arrMultipartBody;
    }

    public static final class NewGsonFactory implements TypeAdapterFactory {

        private final Gson gson;

        public NewGsonFactory(final Gson gson) {
            this.gson = gson;
        }

        @Override
        public <T> TypeAdapter<T> create(final Gson responseGson, final TypeToken<T> typeToken) {
            // Using responseGson would result in infinite recursion since this type adapter factory overrides any type
            return new ResponseExtractorTypeAdapter<>(gson, typeToken.getType());
        }

        private final class ResponseExtractorTypeAdapter<T> extends TypeAdapter<T> {

            private final Gson gson;
            private final Type type;

            private ResponseExtractorTypeAdapter(final Gson gson, final Type type) {
                this.gson = gson;
                this.type = type;
            }

            @Override
            public void write(final JsonWriter out, final T value) {
                try {
                    out.jsonValue(gson.toJson(value));
                } catch (Exception e) {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public T read(final JsonReader in) throws IOException {
                T result = null;
                result = gson.fromJson(in, type);
                return result;
            }

        }

    }

    public static String mapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : map.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            String value = map.get(key);
            try {
                stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
                stringBuilder.append("=");
                stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }

        return stringBuilder.toString();
    }

    public static Map<String, String> stringToMap(String input) {
        Map<String, String> map = new HashMap<String, String>();

        String[] nameValuePairs = input.split("&");
        for (String nameValuePair : nameValuePairs) {
            String[] nameValue = nameValuePair.split("=");
            try {
                map.put(URLDecoder.decode(nameValue[0], "UTF-8"), nameValue.length > 1 ? URLDecoder.decode(
                        nameValue[1], "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }

        return map;
    }

    public static String encryptParams(String key, HashMap<String, String> p_params) {
        String r = RetrofitHelper.mapToString(p_params);
        String encrypted = RetrofitHelper.encrypt(key, r);

        return encrypted;
    }

    public static String encryptParamsForApach(String key, HashMap<String, String> p_params) {
        String r = RetrofitHelper.mapToString(p_params);
        r = r.replaceAll("\\+", "%20");
        String encrypted = RetrofitHelper.encrypt(key, r);
        encrypted = encrypted.replaceAll("\\+", "%2B");
        return encrypted;
    }

    public static String encrypt(String key, String SourceData) {

        if (SourceData == null || SourceData.length() < 1)
            return "";

        try {
            byte[] realKey = key.getBytes();

            int count = 0;
            int length = SourceData.getBytes().length;
            byte[] encryptData = new byte[length];
            int keylen = realKey.length;

            for (int i = 0; i < length; i++) {
                if (count == keylen) {
                    count = 0;
                }
                encryptData[i] = (byte) (SourceData.getBytes()[i] ^ realKey[count]);
                count++;
            }

            String out = Base64.encodeToString(encryptData, 0);
//        Log.d("enc:", enc + "\n");
            //Log.d("enc:", out);
            return out;

        } catch (Exception e) {

        }
        return "";
    }

    public static String decrypt(String key, String encString) {
        if (encString == null || encString.length() < 1)
            return "";

        byte[] realKey = key.getBytes();

        byte[] encryptedData;
        try {
            String sss = encString.trim();
            encryptedData = Base64.decode(sss.getBytes("UTF-8"), 0);

            byte[] Decrypt = new byte[encryptedData.length];

            int count = 0;
            int length = encryptedData.length;
            int keylen = realKey.length;

            for (int i = 0; i < length; i++) {
                if (count == keylen) {
                    count = 0;
                }

                Decrypt[i] = (byte) (encryptedData[i] ^ realKey[count]);
                count++;
            }

            String out = new String(Decrypt, "UTF-8");
            //Log.d("dec:", out);
            return out;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
}
