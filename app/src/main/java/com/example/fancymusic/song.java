package com.example.fancymusic;

import java.io.Serializable;

public class song implements Serializable {
    private String songname;
    private String singger;
    private String songurl;
    private String songpath;

    public song(String songname, String singger, String songurl) {
        this.songname = songname;
        this.singger = singger;
        this.songurl = songurl;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getSingger() {
        return singger;
    }

    public void setSingger(String singger) {
        this.singger = singger;
    }

    public String getSongurl() {
        return songurl;
    }

    public void setSongurl(String songurl) {
        this.songurl = songurl;
    }
}
