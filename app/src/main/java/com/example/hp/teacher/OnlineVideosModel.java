package com.example.hp.teacher;



public class OnlineVideosModel {
    private String title;
    private String thumb_image;
    private String video_url;
    private String uploaded_by;
    private String uploaded_date;
    private String video_about;

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    private String channel_name;






    public OnlineVideosModel(String channel_name,String title, String thumb_image, String video_url, String uploaded_by, String uploaded_date, String video_about) {
        this.title = title;
        this.thumb_image = thumb_image;
        this.video_url = video_url;
        this.uploaded_by = uploaded_by;
        this.uploaded_date = uploaded_date;
        this.video_about = video_about;
        this.channel_name=channel_name;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getUploaded_by() {
        return uploaded_by;
    }

    public void setUploaded_by(String uploaded_by) {
        this.uploaded_by = uploaded_by;
    }

    public String getUploaded_date() {
        return uploaded_date;
    }

    public void setUploaded_date(String uploaded_date) {
        this.uploaded_date = uploaded_date;
    }

    public String getVideo_about() {
        return video_about;
    }

    public void setVideo_about(String video_about) {
        this.video_about = video_about;
    }

    public OnlineVideosModel()
    {

    }






}