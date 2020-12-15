package com.example.fancymusic;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    public static final String DIRECTORY= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    private int fileLength;
    private String path;
    String fileName;
    public MyIntentService() {
        super("MyIntentService");
    }
//  处理intent请求
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            path=intent.getStringExtra("url");
               handleDownload(path);
           }
        }

// 启动线程下载
private void handleDownload(String path){
    this.path = path;
    new Thread(downloadRun).start();

    Log.e("Mytentservice", "handleDownload: 从"+path+"下载了" );
}
@Override
public void onCreate(){
        super.onCreate();
//    handleDownload(path);
    Log.e("myintentservice", "onCreate: 服务开启");
}



    private Runnable downloadRun = new  Runnable(){
        @Override
        public void run() {
            InputStream ips = null;
            try {
                Log.e("download..", "run: 开始下载");
                 String urlString=path;
                int start=urlString.lastIndexOf("/");
                int end=urlString.lastIndexOf("?");
                fileName=urlString.substring(start,end);
//下载位置
                File file=new File(DIRECTORY+"/"+fileName);
                Log.e("下载位置", "run: "+DIRECTORY);

                OutputStream output = new FileOutputStream(file);
                URL url = new URL(urlString);
                //     URLConnection类的子类HttpURLConnection,
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");
//                HttpURLConnection是基于HTTP协议的，其底层通过socket通信实现。如果不设置超时（timeout），在网络异常的情况下，可能会导致程序僵死而不继续往下执行。
                huc.setReadTimeout(10000);
                huc.setConnectTimeout(3000);
                fileLength = Integer.valueOf(huc.getHeaderField("Content-Length"));
                ips = huc.getInputStream();
                Log.e("下载位置", "run: "+fileLength);

                InputStream in = new BufferedInputStream(ips);
                byte[] arr = readStream(in);

                output.write(arr,0,fileLength);
                output.flush();
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public  byte[] readStream(InputStream in) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte data[] = new byte[1024];
        long total = 0;
        int count;
        while((count = in.read(data))!=-1){
            total+=count;
            bos.write(data,0,count);
        }
        bos.close();
        in.close();
        return bos.toByteArray();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("myintentservice", "onCreate: 服务结束");
    }
}

