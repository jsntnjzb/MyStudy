package com.home.mystudy.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.home.mystudy.Model.ImageModel;
import com.home.mystudy.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends NetworkActivity {
    private MyHandlerThread mHandlerThread;
    private Handler subHandler = null;
//    private Button button;
    private ImageView imageView;
    private String TAG;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = this.getClass().getName();
        Log.d(TAG,"onCreate");
        init();

        /**
         * HandlerThread
         * */
        //创建实例对象
        createHandlerThread("mHandlerThread");

        /**
         * 构建循环消息处理机制
         * 将上面HandlerThread中的looper对象最为Handler的参数
         * 然后重写Handler的Callback接口类中的handlerMessage方法来处理耗时任务
         * */
        createHandler(mHandlerThread.getLooper(),mSubCallBack);

        downLoad_images();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    private void init(){
//        button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //发送异步耗时任务到HandlerThread中
//               // subHandler.sendEmptyMessage(0);
//                Intent intent = new Intent(MainActivity.this,NewActivity.class);
//                startActivity(intent);
//            }
//        });
        imageView = (ImageView)findViewById(R.id.image);
    }

    public void createHandlerThread(String name){
        if(mHandlerThread==null){
            //mHandlerThread
            mHandlerThread = new MyHandlerThread(name);
        }
        if(!mHandlerThread.isAlive()){
            /**
             *启动handlerThread线程,必须先start,才能创建Handler对象
             * 该方法主要是创建Looper对象，并且通知getLooper方法中的wait
             * */
            mHandlerThread.start();
        }
    }

    private void createHandler(Looper looper, Handler.Callback callback){
        if(subHandler == null){
            subHandler = new Handler(looper, callback);
        }
    }

    private Handler.Callback mSubCallBack = new Handler.Callback() {
        //该接口实现就是异步处理耗时任务的,该方法执行在子线程中
        @Override
        public boolean handleMessage(Message msg) {
            //在子线程中进行网络请求
            Bitmap bitmap=downLoadUrlBitmap(url[msg.what]);
            ImageModel imageModel=new ImageModel();
            imageModel.bitmap=bitmap;
            imageModel.url=url[msg.what];
            Message msg1 = new Message();
            msg1.what = msg.what;
            msg1.obj =imageModel;
            //通知主线程去更新UI
            mUIHandler.sendMessage(msg1);
            return false;
        }
    };

    private class MyHandlerThread extends HandlerThread {

        public MyHandlerThread(String name) {
            super(name);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            Log.i("MainActivity","onLooperPrepared");
        }
    }

    private Handler mUIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("MainActivity","次数:"+msg.what);
            ImageModel model = (ImageModel) msg.obj;
            imageView.setImageBitmap(model.bitmap);
        }
    };

    private void downLoad_images(){
        for (int i = 0;i<url.length;i++){
            subHandler.sendEmptyMessageDelayed(i,1000*i);
        }
    }

    private Bitmap downLoadUrlBitmap(String urlString){
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        Bitmap bitmap=null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(3000);
            urlConnection.setUseCaches(true);
            urlConnection.connect();
            int code = urlConnection.getResponseCode();
            Log.e(TAG,"run:"+code);
            if(code==200){
                inputStream = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }else if(code==301 || code==302){
                //重定向问题
                String location = urlConnection.getHeaderField("Location");
                url=new URL(location);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(false);
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(3000);
                urlConnection.setUseCaches(true);
                urlConnection.connect();
                code = urlConnection.getResponseCode();
                if(code==200){
                    inputStream = urlConnection.getInputStream();
                    byte[] data = readStream(inputStream);
                    //byte[] base64_data =  Base64.decode(inputStream.toString(),Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    //bitmap = decodeSampledBitmapFromStream(inputStream,20,20);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bitmap;
    }

    /**
     *得到图片字节流 数组大小
     *
     * */
    public static byte[] readStream(InputStream inStream){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while((len=inStream.read(buffer)) != -1){
                outStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                outStream.close();
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outStream.toByteArray();
    }

    public static Bitmap decodeSampledBitmapFromStream(InputStream inputStream, int reqWidth, int reqHeight) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            int n;
            byte[] buffer = new byte[1024];
            while ((n = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, n);
            }
            return decodeSampledBitmapFromByteArray(outputStream.toByteArray(), reqWidth, reqHeight);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int
            reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            while (halfWidth / inSampleSize >= reqWidth && halfHeight / inSampleSize >= reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mHandlerThread.quitSafely();
        }else {
            mHandlerThread.quit();
        }
    }
}
