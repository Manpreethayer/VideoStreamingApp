package com.example.hp.teacher.Songs.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.Long;
import android.widget.Toast;


import com.example.hp.teacher.R;
import com.example.hp.teacher.Songs.models.Song;
import com.example.hp.teacher.UploadMusicActivity;
import com.facebook.drawee.view.SimpleDraweeView;


import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {

    private List<Song> mSongsList;

    private Context mContext;

    private boolean isPlaylist;
    private boolean animate;


    public SongsAdapter(AppCompatActivity applicationContext, List<Song> mSongsList, boolean isPlaylist, boolean animate) {

        this.mSongsList = mSongsList;
        this.mContext = applicationContext;
        this.isPlaylist = isPlaylist;


        this.animate = animate;

    }

    @Override
    public SongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;


        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offline_songs_single, parent, false);

        SongsViewHolder songsViewHolder = new SongsViewHolder(v);

        return songsViewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder songsViewHolder, int i) {

        Song c = mSongsList.get(i);
        songsViewHolder.setName(c.title);

        songsViewHolder.setId(c.id);


    }




    @Override
    public int getItemCount() {
        return mSongsList.size();
    }


    public class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;
        TextView songTitle;
        SimpleDraweeView songsImage;
        Long path;
        private String songName;

        //   public TextView displayName;
        // public ImageView messageImage;
        SongsViewHolder(View view) {
            super(view);

            mView = view;



            mView.setOnClickListener((View.OnClickListener) this);


        }
        public void setName(String name) {
            songTitle = (TextView) mView.findViewById(R.id.songName);
            songTitle.setText(name);
            songName=name;
        }

        public void setImage(String name) {
            songsImage = (SimpleDraweeView) mView.findViewById(R.id.songImage);
        }

        public void setId(long id) {
            path=id;
        }

        @Override
        public void onClick(View v) {

            String uri=getSongUri(mContext,path).toString();

            Intent uploadSongsIntent=new Intent(mContext, UploadMusicActivity.class);
            uploadSongsIntent.putExtra("songname",songName);
            uploadSongsIntent.putExtra("songUri",uri);
            mContext.startActivity(uploadSongsIntent);



            Toast.makeText(mContext,uri,Toast.LENGTH_SHORT).show();




        }

        Uri getSongUri(Context context, long id) {
            final String[] projection = new String[]{
                    BaseColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM_ID
            };
            final StringBuilder selection = new StringBuilder();
            selection.append(BaseColumns._ID + " IN (");
            selection.append(id);
            selection.append(")");
            final Cursor c = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection.toString(),
                    null, null);

            if (c == null) {
                return null;
            }
            c.moveToFirst();


            try {

                Uri uri = Uri.parse(c.getString(1));
                c.close();

                return uri;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}



