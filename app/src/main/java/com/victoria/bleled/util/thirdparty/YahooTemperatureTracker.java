package com.victoria.bleled.util.thirdparty;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class YahooTemperatureTracker {

    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor mTempSensor;
    private Sensor mHumanitySensor;
    private WeatherCallback mCallback;

    private String mLocal1, mLocal2, mLocal3;
    private ArrayList<ShortWeather> mArrWeather = new ArrayList<>();


    public YahooTemperatureTracker(Context context) {
        super();

        mContext = context;
    }

    public void getWeather(double latitude, double longitude, WeatherCallback callback) {
        mCallback = callback;

        final String appId = "g2u1MQ7e";
        final String consumerKey = "dj0yJmk9N2FUYndlS1FiNG1VJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PTQz";
        final String consumerSecret = "6a1096fd96d8d5bb5d9a3e38ef8863b4a4bed4e9";
        final String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";

        long timestamp = new Date().getTime() / 1000;
        byte[] nonce = new byte[32];
        Random rand = new Random();
        rand.nextBytes(nonce);
        String oauthNonce = "AAAAAAAABBBBBBBBCCCCCCCCDDDDDDDD";//new String(nonce).replaceAll("\\W", "");

        List<String> parameters = new ArrayList<>();
        parameters.add("oauth_consumer_key=" + consumerKey);
        parameters.add("oauth_nonce=" + oauthNonce);
        parameters.add("oauth_signature_method=HMAC-SHA1");
        parameters.add("oauth_timestamp=" + timestamp);
        parameters.add("oauth_version=1.0");
        // Make sure value is encoded
        parameters.add("lat=" + String.format("%f", latitude));
        parameters.add("lon=" + String.format("%f", longitude));
//        try {
//            parameters.add("location=" + URLEncoder.encode("sunnyvale,ca", "UTF-8"));
//        }
//        catch (Exception e) {}
        parameters.add("format=json");
        Collections.sort(parameters);

        StringBuffer parametersList = new StringBuffer();
        for (int i = 0; i < parameters.size(); i++) {
            parametersList.append(((i > 0) ? "&" : "") + parameters.get(i));
        }
        String signatureString = "";
        try {
            signatureString = "GET&" + URLEncoder.encode(url, "UTF-8") + "&" + URLEncoder.encode(parametersList.toString(), "UTF-8");
        } catch (Exception e) {
        }

        String signature = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec((consumerSecret + "&").getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHMAC = mac.doFinal(signatureString.getBytes());
            Base64.Encoder encoder = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                encoder = Base64.getEncoder();
                signature = encoder.encodeToString(rawHMAC);
            } else {
                //70mNG5L0+JIwqmFh3rFeVhPUTb0=\n
                signature = android.util.Base64.encodeToString(rawHMAC, 0);
                signature = signature.replace("\n", "");
            }
        } catch (Exception e) {
            System.err.println("Unable to append signature");
            System.exit(0);
        }

        String authorizationLine = "OAuth " +
                "oauth_consumer_key=\"" + consumerKey + "\", " +
                "oauth_nonce=\"" + oauthNonce + "\", " +
                "oauth_timestamp=\"" + timestamp + "\", " +
                "oauth_signature_method=\"HMAC-SHA1\", " +
                "oauth_signature=\"" + signature + "\", " +
                "oauth_version=\"1.0\"";

        String params = String.format("?lat=%f&lon=%f&format=json", latitude, longitude);
        //String params = "?location=sunnyvale,ca&format=json";
        // Instantiate the RequestQueue.
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authorizationLine);
        headers.put("Yahoo-App-Id", appId);
        headers.put("Content-Type", "application/json");

        RequestQueue queue = Volley.newRequestQueue(mContext);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        ShortWeather weather = new ShortWeather();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonResultObject = jsonObject.getJSONObject("current_observation");

                            weather.setReh(jsonResultObject.getJSONObject("atmosphere").getString("humidity"));
                            weather.setTemp(jsonResultObject.getJSONObject("condition").getString("temperature"));

                            float currentTemp = Float.parseFloat(weather.getTemp());
                            weather.setTemp(String.format("%.1f", convertFahrenheitToCelcius(currentTemp)));

                            weather.setWfEn(jsonResultObject.getJSONObject("condition").getString("text"));
                        } catch (Exception e) {
                        }

                        if (callback != null) {
                            callback.onChange(weather);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null) {
                    callback.onChange(null);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void getWeatherOld(double latitude, double longitude, WeatherCallback callback) {
        mCallback = callback;

        String sql = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"(%f, %f)\")";
        sql = String.format(sql, latitude, longitude);

        String url = "https://query.yahooapis.com/v1/public/yql?q=" + sql + "&format=json&env=store://datatables.org/alltableswithkeys";

        RequestQueue queue = Volley.newRequestQueue(mContext);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ShortWeather weather = new ShortWeather();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonResultObject = jsonObject.getJSONObject("query").getJSONObject("results").getJSONObject("channel");

                            weather.setReh(jsonResultObject.getJSONObject("atmosphere").getString("humidity"));
                            weather.setTemp(jsonResultObject.getJSONObject("item").getJSONObject("condition").getString("temp"));

                            float currentTemp = Float.parseFloat(weather.getTemp());
                            weather.setTemp(String.format("%.1f", convertFahrenheitToCelcius(currentTemp)));

                            weather.setWfEn(jsonResultObject.getJSONObject("item").getJSONObject("condition").getString("text"));
                        } catch (Exception e) {
                        }

                        if (callback != null) {
                            callback.onChange(weather);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null) {
                    callback.onChange(null);
                }
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void getWeather(String locality, String subLocality, String throughfair, WeatherCallback callback) {
        if (locality == null || locality.isEmpty() == true) {
            if (callback != null) {
                callback.onChange(null);
            }
        }

        mCallback = callback;
        mLocal1 = locality;
        mLocal2 = subLocality;
        mLocal3 = throughfair;

        String url = "http://www.kma.go.kr/DFSROOT/POINT/DATA/top.json.txt";
        getLocality(url, "", 0, callback);
    }

    private void getLocality(String url, String preTopCode, int step, WeatherCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String temp = "";
                            String topCode = preTopCode;

                            String locale = mLocal1;
                            if (step == 1) {
                                locale = mLocal2;
                            } else if (step == 2) {
                                locale = mLocal3;
                            }

                            JSONArray topArray = new JSONArray(response);
                            for (int i = 0; i < topArray.length(); i++) {
                                JSONObject token = topArray.getJSONObject(i);
                                if (token.getString("value").equals(locale)) {
                                    temp += token.getString("value");
                                    topCode = token.getString("code");
                                    break;
                                }
                            }

                            if (topCode.isEmpty() == false) {
                                if (step == 0 && (mLocal2 == null || mLocal2.isEmpty() == true) && (mLocal3 == null || mLocal3.isEmpty() == true)) {
                                    getWheater(topCode, callback);
                                    return;
                                } else if (step == 1 && (mLocal3 == null || mLocal3.isEmpty() == true)) {
                                    getWheater(topCode, callback);
                                    return;
                                } else if (step == 2) {
                                    getWheater(topCode, callback);
                                    return;
                                }

                                int nextStep = step + 1;
                                String nextUrl = "";
                                if (nextStep == 1) {
                                    nextUrl = "http://www.kma.go.kr/DFSROOT/POINT/DATA/mdl." + topCode + ".json.txt";
                                    getLocality(nextUrl, topCode, nextStep, callback);
                                } else if (nextStep == 2) {
                                    nextUrl = "http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf." + topCode + ".json.txt";
                                    getLocality(nextUrl, topCode, nextStep, callback);
                                } else {
                                    getWheater(topCode, callback);
                                }
                            } else {
                                if (callback != null) {
                                    callback.onChange(null);
                                }
                            }
                        } catch (Exception e) {
                            if (callback != null) {
                                callback.onChange(null);
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null) {
                    callback.onChange(null);
                }
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getWheater(String zoneCode, WeatherCallback callback) {
        String url = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=" + zoneCode;
        // Request a string response from the provided URL.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String xml = response;
                        try {
                            String tagName = "";
                            boolean onHour = false;
                            boolean onDay = false;
                            boolean onTem = false;
                            boolean onWfKor = false;
                            boolean onWfEn = false;
                            boolean onRef = false;
                            boolean onEnd = false;
                            boolean isItemTag1 = false;
                            int i = 0;

                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            XmlPullParser parser = factory.newPullParser();

                            parser.setInput(new StringReader(xml));

                            int eventType = parser.getEventType();

                            ArrayList<ShortWeather> shortWeathers = new ArrayList<ShortWeather>();

                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    tagName = parser.getName();
                                    if (tagName.equals("data")) {
                                        shortWeathers.add(new ShortWeather());
                                        onEnd = false;
                                        isItemTag1 = true;
                                    }
                                } else if (eventType == XmlPullParser.TEXT && isItemTag1) {
                                    if (tagName.equals("hour") && !onHour) {
                                        shortWeathers.get(i).setHour(parser.getText());
                                        onHour = true;
                                    }
                                    if (tagName.equals("day") && !onDay) {
                                        shortWeathers.get(i).setDay(parser.getText());
                                        onDay = true;
                                    }
                                    if (tagName.equals("temp") && !onTem) {
                                        shortWeathers.get(i).setTemp(parser.getText());
                                        onTem = true;
                                    }
                                    if (tagName.equals("wfKor") && !onWfKor) {
                                        shortWeathers.get(i).setWfKor(parser.getText());
                                        onWfKor = true;
                                    }
                                    if (tagName.equals("wfEn") && !onWfEn) {
                                        shortWeathers.get(i).setWfEn(parser.getText());
                                        onWfEn = true;
                                    }
                                    if (tagName.equals("reh") && !onRef) {
                                        shortWeathers.get(i).setReh(parser.getText());
                                        onRef = true;
                                    }
                                } else if (eventType == XmlPullParser.END_TAG) {
                                    if (tagName.equals("s06") && onEnd == false) {
                                        i++;
                                        onHour = false;
                                        onDay = false;
                                        onTem = false;
                                        onWfKor = false;
                                        onWfEn = false;
                                        onRef = false;
                                        isItemTag1 = false;
                                        onEnd = true;
                                    }
                                }

                                eventType = parser.next();
                            }

                            mArrWeather.clear();
                            mArrWeather.addAll(shortWeathers);

                            if (callback != null && mArrWeather.size() > 0) {
                                callback.onChange(mArrWeather.get(0));
                            } else {
                                if (callback != null) {
                                    callback.onChange(null);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            if (callback != null) {
                                callback.onChange(null);
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null) {
                    callback.onChange(null);
                }
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public interface WeatherCallback {
        void onChange(ShortWeather weather);
    }

    private float convertFahrenheitToCelcius(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    public class ShortWeather {
        private String hour;  // 시간
        private String day;
        private String temp;  // 온도
        private String reh;   // 습도
        private String wfKor; // 상태 맑음, 구름 조금, 구름 많음,  흐림,  비, 눈/비, 눈
        private String wfEn;  // 영문상태 clear, partly cloudy, modstly cloudy, cloudy, rain, snow/rain, snow

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getReh() {
            return reh;
        }

        public void setReh(String reh) {
            this.reh = reh;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public void setWfKor(String wfKor) {
            this.wfKor = wfKor;
        }

        public String getHour() {
            return hour;
        }

        public String getTemp() {
            return temp;
        }

        public String getWfKor() {
            return wfKor;
        }

        public String getWfEn() {
            return wfEn;
        }

        public void setWfEn(String wfEn) {
            this.wfEn = wfEn;
        }
    }
}