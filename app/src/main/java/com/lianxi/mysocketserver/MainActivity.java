package com.lianxi.mysocketserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lianxi.mysocketserver.one.MyBean;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "checkManifest";
    private String[] permisson = {Manifest.permission.INTERNET, Manifest.permission_group.STORAGE};
    private int i;
    private TextView tv;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);

        i = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (i == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permisson, 1);
            }
        } else {
            getLoaclHost();
        }


        MyAsyncTask myAsyncTask = new MyAsyncTask(tv);
//        myAsyncTask.execute("https://www.baidu.com");

        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(1000 * 10, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .method("GET", null)
                .url("https://www.baidu.com")
                .build();



        MyBean myBean = new MyBean();
        Log.i(TAG, "onCreate: " + myBean.getNum());
    }


    class MyAsyncTask extends AsyncTask<String, Integer, String> {
        TextView textView;
        volatile String str = "false";

        public MyAsyncTask(TextView textView) {
            this.textView = textView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.connect();
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    Log.i(TAG, "doInBackground: 成功");
                    str = "true";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i(TAG, "onPostExecute: " + s);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            Log.i(TAG, "onRequestPermissionsResult: " + Arrays.toString(grantResults));
            getLoaclHost();
        }
    }

    private void getLoaclHost() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //1.创建serverSocket
                    ServerSocket serverSocket = new ServerSocket(8800);
                    InetAddress localHost = InetAddress.getLocalHost();
                    String hostAddress = localHost.getHostAddress();
                    Log.i(TAG, "onCreate: " + localHost + "+" + hostAddress);
                    Socket socket = null;
                    //2.调用accept()等待客户端连接
                    socket = serverSocket.accept();


                    //3.连接后获取输入流，读取客户端信息
                    InputStream inputStream = socket.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String s = bufferedReader.readLine();
                    Log.i(TAG, "run: " + s);

                    if (socket.isInputShutdown() == false) {
                        socket.shutdownInput();
                    }
                    if (socket.isOutputShutdown() == false) {
                        socket.shutdownOutput();
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}