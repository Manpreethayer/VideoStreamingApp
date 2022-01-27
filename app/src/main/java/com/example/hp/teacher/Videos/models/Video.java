package com.example.hp.teacher.Videos.models;

public class Video{


    public final String id;
    public final String title;
    public final String thumb;
    public String duration;






    public Video() {
        this.id = "";
        this.title = "";
        this.thumb="";
        this.duration="";
    }
    public Video(String _id, String _title, String thumb,String duration) {
        this.id = _id;

        this.title = _title;
        this.thumb=thumb;
        this.duration=duration;
    }


}
