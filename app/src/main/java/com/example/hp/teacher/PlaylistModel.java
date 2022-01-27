package com.example.hp.teacher;

public class PlaylistModel {
    public String getPlaylist_preview() {
        return playlist_preview;
    }

    public void setPlaylist_preview(String playlist_preview) {
        this.playlist_preview = playlist_preview;
    }

    public String getPlaylist_title() {
        return playlist_title;
    }

    public void setPlaylist_title(String playlist_title) {
        this.playlist_title = playlist_title;
    }

    public PlaylistModel(String playlist_preview, String playlist_title) {
        this.playlist_preview = playlist_preview;
        this.playlist_title = playlist_title;
    }

    private String playlist_preview;
    private String playlist_title;

    public PlaylistModel()
    {

    }

}
