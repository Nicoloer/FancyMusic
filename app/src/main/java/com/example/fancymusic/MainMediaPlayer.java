package com.example.fancymusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelStoreOwner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMediaPlayer extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer mediaPlayer;
    private TextView allTime;
    private TextView playTime;
    private TextView songName;
    private SeekBar seekBar;
    private List<song> list= new ArrayList<>();
    public static final String DIRECTORY= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    private int p=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_media_player);
        initView();
        initList();
        try {
            getPermisson();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        playTime=findViewById(R.id.played_time);
        allTime=findViewById(R.id.all_time);
        songName=findViewById(R.id.song_name);
        seekBar=findViewById(R.id.seek_bar);

        Button stop=findViewById(R.id.stop);
        Button play=findViewById(R.id.play);
        Button pause=findViewById(R.id.pause);
        Button before=findViewById(R.id.before);
        Button after=findViewById(R.id.after);
        stop.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        before.setOnClickListener(this);
        after.setOnClickListener(this);
//       监听seekbar 如果用户滑动，就使用seekTo处理
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser==true){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
//  播放界面
    private  void initMediaPlayer() throws IOException {
        try {
            mediaPlayer = new MediaPlayer();
            String filename=getIntent().getStringExtra("filename");
            File file = new File(DIRECTORY + "/"+filename);
            Log.e("MainMediaPlayer", "initMediaPlayer: filename"+file.getName() );
            mediaPlayer.setDataSource(file.getPath());
            Log.e("MainMediaPlayer", "initMediaPlayer: filepath"+file.getPath() );

            mediaPlayer.prepare();

            songName.setText(file.getName());
//        设置最大值
            seekBar.setMax(mediaPlayer.getDuration());

        }catch (Exception e) {
        e.printStackTrace();
        }

    }
    private  void shift() throws IOException {
        try {
            mediaPlayer.reset();
            File file=new File(DIRECTORY+"/"+list.get(p).getSongname());

            Log.e("MainMediaPlayer", "initMediaPlayer: filename"+file.getName() );
            mediaPlayer.setDataSource(file.getPath());
            Log.e("MainMediaPlayer", "initMediaPlayer: filepath"+file.getPath() );

            mediaPlayer.prepare();
//            mediaPlayer.start();

            songName.setText(file.getName());
//        设置最大值
            seekBar.setMax(mediaPlayer.getDuration());

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                if(!mediaPlayer.isPlaying()){
//                    开始播放
                    mediaPlayer.start();
//                    给进度条设置时长
                    Message message=handler.obtainMessage();
                    message.what=1;
                    message.arg1=mediaPlayer.getDuration();

                    handler.sendMessage(message);
                    handler.post(updateThread);
                }
                break;
            case R.id.pause:
                if(mediaPlayer.isPlaying()){
//                    开始播放
                    mediaPlayer.pause();;
                  handler.removeCallbacks(updateThread);
                }
                break;
            case R.id.stop:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    try {
                        shift();
                        handler.removeCallbacks(updateThread);
                        handler.sendEmptyMessage(3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.before:
                if(p==0){
                    Log.e("p", "onClick: "+p);
                    //0的上一首就是列表的最后一首
                    p=list.size()-1;
                    Log.e("p", "onClick: "+p);
                }
                else{
                    p=(p-1)%list.size();
                }
                try {
                    shift();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.after:
                p=(p+1)%list.size();
                try {
                    shift();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
//            根据Massage控制进度条
            switch (msg.what){
                case 1:
                    allTime.setText(msg.arg1/60000+":"+msg.arg1/1000%60);
                    break;
                case 3:
//                    停止
                    allTime.setText("00:00");
                    playTime.setText("00:00");
                    seekBar.setProgress(0);
                    break;
            }
        }

    };
    //  进度条更新
    Runnable updateThread=new Runnable() {
        @Override
        public void run() {
//  获取歌曲在播放位置或者设置播放进度条的值
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            playTime.setText(mediaPlayer.getCurrentPosition()/60000+
                    ":"+mediaPlayer.getCurrentPosition()/1000%60);
//  每次延迟100毫秒在启动线程更新目前的进度条
            handler.postDelayed(updateThread,100);
        }
    };
    //获取读权限
    private void getPermisson() throws IOException {
        if(Build.VERSION.SDK_INT>=23){
            int checkPermisson=
                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.e("SDK>=23", "getPermisson: "+checkPermisson);
            if(checkPermisson!= PackageManager.PERMISSION_GRANTED){
                Log.e("SDK>=23", "getPackageManager: "+PackageManager.PERMISSION_GRANTED);
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                        111);
                return;
            }else {
                initMediaPlayer();
            }
        }else {
            initMediaPlayer();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case 111:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"获取权限成功",Toast.LENGTH_SHORT).show();
                    try {
                        initMediaPlayer();
                        Log.e("getPermision", "onRequestPermissionsResult: 初始化音乐播放器");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(this,"获取权限失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
    public void initList() {
        //读取DOWNLOAD目录下所有的文件
        File dir = new File(DIRECTORY);
        File[] files = dir.listFiles();
        for (File f : files) {
            //逐个遍历，将以.mp3结尾的文件加入播放列表
            String fName = f.getName();
            Log.e("1", "initList: filename:"+fName);
            if (fName.endsWith(".mp3")) {
                //分出作者名
                int indexAuthor = fName.lastIndexOf("_");
                String singer = fName.substring(0, indexAuthor);
                song song = new song(fName, singer, "");

                list.add(song);
            }
        }
    }

}