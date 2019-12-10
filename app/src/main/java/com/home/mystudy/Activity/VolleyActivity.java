package com.home.mystudy.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.home.mystudy.Model.Weather;
import com.home.mystudy.Model.WeatherInfo;
import com.home.mystudy.R;
import com.home.mystudy.Util.GsonRequest;

import org.json.JSONObject;

public class VolleyActivity extends Activity {
    /**
     * 请求队列对象,
     * 缓存所有Http请求,
     * 每个Activity只需要创建一个就可以了
     * */
    private RequestQueue mRequestQueue = null;
    private String TAG;
    private ImageView imageView;
    /**
     * 图片地址集合
     */
    private String url[]={
            "http://img.blog.csdn.net/20160903083245762",
            "http://img.blog.csdn.net/20160903083252184",
            "http://img.blog.csdn.net/20160903083257871",
            "http://img.blog.csdn.net/20160903083257871",
            "http://img.blog.csdn.net/20160903083311972",
            "http://img.blog.csdn.net/20160903083319668",
            "http://img.blog.csdn.net/20160903083326871"
    };
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String imageUrl = url[msg.what];
            ImageRequest imageRequest = createImageRequest(imageUrl);
            mRequestQueue.add(imageRequest);
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);
        TAG = VolleyActivity.class.getName();
        mRequestQueue = Volley.newRequestQueue(this);
        //StringRequest
        //StringRequest stringRequest = createStrRequest("https://www.baidu.com");
        //mRequestQueue.add(stringRequest);
        //JsonRequest
        //JsonObjectRequest jsonObjectRequest = createJsonObjectRequest("http://api.map.baidu.com/telematics/v3/weather?location=嘉兴&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ");
        //mRequestQueue.add(jsonObjectRequest);

        //ImageRequest
        imageView = (ImageView)findViewById(R.id.image);
        imageView.setVisibility(View.VISIBLE);
        downLoad_images();

        //自定义GsonRequest
        //get_weather();
    }

    /**
     * create StringRequest
     * */
    private StringRequest createStrRequest(String requestUrl){
        StringRequest stringRequest = new StringRequest(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.getMessage());
            }
        });
        return stringRequest;
    }

    /**
     * create JsonRequest
     * */
    private JsonObjectRequest createJsonObjectRequest(String requestUrl){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject jsonObject = response;
                Log.d(TAG,jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.getMessage());
            }
        });
        return jsonObjectRequest;
    }

    //ImageRequest
    private ImageRequest createImageRequest(String requestUrl){
        ImageRequest imageRequest = new ImageRequest(requestUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    if(error instanceof TimeoutError){
                        Toast.makeText(VolleyActivity.this,"网络请求超时，请重试！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(error instanceof ServerError) {
                        int statusCode = error.networkResponse.statusCode;
                        if(statusCode==301 || statusCode==302){
                           String location = error.networkResponse.headers.get("Location");
                           ImageRequest imageRequest1 = createImageRequest(location);
                           mRequestQueue.add(imageRequest1);
                        }
                        //Toast.makeText(VolleyActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(error instanceof NetworkError) {
                        Toast.makeText(VolleyActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(error instanceof ParseError) {
                        Toast.makeText(VolleyActivity.this,"数据格式错误",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(VolleyActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }

                String error_msg = error.getMessage();
                //Log.e(TAG,error_msg);
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        });
        return imageRequest;
    }

    private void downLoad_images(){
        for (int i = 0;i<url.length;i++){
            mHandler.sendEmptyMessageDelayed(i,1000*i);
        }
//        ImageRequest imageRequest = createImageRequest(url[0]);
//        mRequestQueue.add(imageRequest);
    }

    //Weather
    private void get_weather(){
        GsonRequest<Weather> gsonRequest = new GsonRequest(
                "http://www.weather.com.cn/data/sk/101010100.html", Weather.class,
                new Response.Listener<Weather>() {
                    @Override
                    public void onResponse(Weather weather) {
                        WeatherInfo weatherInfo = weather.getWeatherinfo();
                        Log.d("TAG", "city is " + weatherInfo.getCity());
                        Log.d("TAG", "temp is " + weatherInfo.getTemp());
                        Log.d("TAG", "time is " + weatherInfo.getTime());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mRequestQueue.add(gsonRequest);
    }


}
