package com.example.fancymusic;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String DIRECTORY= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    private List<song> list= new ArrayList<>();
    private Adapter adapter;
    protected song item,item1,item2,item3,item4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView=findViewById(R.id.listView);
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, MainMediaPlayer.class);
//                intent.putExtra("path","https://freemusicarchive.org/genre/Classical/");
                startActivity(intent);
            }
        });
        Button download=(Button)findViewById(R.id.download);


        initialList();
        adapter = new Adapter(MainActivity.this,R.layout.item,list);
        listView.setAdapter(adapter);
//      获取当前点击的歌曲判断是否存在于文件中，若不存在则启动服务下载
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("点击事件position", String.valueOf(position));
                song currentsong=list.get(position);
                String path=currentsong.getSongurl();
                int start=path.lastIndexOf("/");
                int end=path.lastIndexOf("?");
                String fileName=path.substring(start,end);
                System.out.println("下载的文件名为"+fileName);
                if(fileIsExists(DIRECTORY+fileName)) {
                    Intent intent=new Intent(MainActivity.this, MainMediaPlayer.class);
                    intent.putExtra("filename",fileName);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this,"该歌曲不存在即将下载！",Toast.LENGTH_SHORT);

                    Intent intentservice = new Intent(MainActivity.this, MyIntentService.class);
                    Log.e("item", "onItemClick: " + path);
                    intentservice.putExtra("url", path);
//                intentservice.putExtra("Ser",currentsong);
                    startService(intentservice);
                }
            }
        });
    }

//    初始化音乐列表
    private void initialList() {

        item=new song ("Juanitos","Exotica","https://files.freemusicarchive.org/storage-freemusicarchive-org/music/Oddio_Overplay/Juanitos/Exotica/Juanitos_-_06_-_Exotica.mp3?download=1");
        item1=new song ("K.I.R.K","Don't Go","https://files.freemusicarchive.org/storage-freemusicarchive-org/music/ccCommunity/KIRK/FrostWire_Creative_Commons_Mixtape_Vol_5/KIRK_-_02_-_Dont_Go.mp3?download=1");
        item2=new song ("Little Glass Men","Spray paint it Gold","https://files.freemusicarchive.org/storage-freemusicarchive-org/music/Music_for_Video/Little_Glass_Men/The_Age_of_Insignificance/Little_Glass_Men_-_07_-_Spray_paint_it_Gold.mp3?download=1");
        item3=new song ("Captive Portal","T-Shirts Silly Bus","https://files.freemusicarchive.org/storage-freemusicarchive-org/music/no_curator/Captive_Portal/Somethign_Abbadat_-_EP/Captive_Portal_-_05_-_T-Shirts_Silly_Bus.mp3?download=1");
        item4=new song (" Cullah","Lovely Spider","https://files.freemusicarchive.org/storage-freemusicarchive-org/music/Music_for_Video/Cullah/Cullahmity/Cullah_-_04_-_Lonely_Spider.mp3?download=1");
        list.add(item);
        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);

    }

//  判断文件是否存在
    public boolean fileIsExists(String strFile) {
        Log.e("判断文件是否存在", "fileIsExists: "+strFile );
        try {
            File f=new File(strFile);
            if(f.exists()) {
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}