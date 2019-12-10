package com.home.mystudy.Util;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private  Class<T> clazz;
    private  Map<String, String> headers;
    private  Response.Listener<T> listener;



    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
    }


    public GsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        clazz = clazz;
        listener = listener;
    }

    public GsonRequest(String url, Class<T> clazz, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        this(Method.GET, url, clazz, listener, errorListener);
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,"UTF-8");
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }

//        {"weatherinfo":
//            {"city":"北京",
//                    "cityid":"101010100",
//                    "temp":"27.9",
//                    "WD":"南风",
//                    "WS":"小于3级",
//                    "SD":"28%",
//                    "AP":"1002hPa",
//                    "njd":"暂无实况",
//                    "WSE":"<3",
//                    "time":"17:55",
//                    "sm":"2.1",
//                    "isRadar":"1",
//                    "Radar":"JC_RADAR_AZ9010_JB"}}
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}
