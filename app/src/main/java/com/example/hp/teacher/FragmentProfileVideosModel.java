package com.example.hp.teacher;

public class FragmentProfileVideosModel {

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

    public String getUploaded_by() {
        return uploaded_by;
    }

    public void setUploaded_by(String uploaded_by) {
        this.uploaded_by = uploaded_by;
    }

    public String getVideo_about() {
        return video_about;
    }

    public void setVideo_about(String video_about) {
        this.video_about = video_about;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getUploaded_date() {
        return uploaded_date;
    }

    public void setUploaded_date(String uploaded_date) {
        this.uploaded_date = uploaded_date;
    }

    String title;
    String thumb_image;
    String uploaded_by;
    String video_about;
    String video_url;

    public FragmentProfileVideosModel(String title, String thumb_image, String uploaded_by, String video_about, String video_url, String uploaded_date) {
        this.title = title;
        this.thumb_image = thumb_image;
        this.uploaded_by = uploaded_by;
        this.video_about = video_about;
        this.video_url = video_url;
        this.uploaded_date = uploaded_date;
    }

    public FragmentProfileVideosModel()
    {

    }

    String uploaded_date;


}
