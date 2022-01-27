package com.example.hp.teacher.Videos.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.media.TimedText;
import android.net.Uri;
import android.os.CancellationSignal;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.teacher.R;
import com.example.hp.teacher.UploadVideoActivity;
import com.example.hp.teacher.Videos.models.Video;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {

    private List<Video> mVideosList;

    private Context mContext;
    String durationinMillis=null;

    private boolean isPlaylist;
    private boolean animate;


    public VideosAdapter(AppCompatActivity applicationContext, List<Video> mVideosList, boolean isPlaylist, boolean animate) {

        this.mVideosList = mVideosList;
        this.mContext = applicationContext;
        this.isPlaylist = isPlaylist;


        this.animate = animate;

    }

    @Override
    public VideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;


        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.on_device_videos_single, parent, false);

        Fresco.initialize(parent.getContext());
        VideosViewHolder videosViewHolder = new VideosViewHolder(v);

        return videosViewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder videosViewHolder, int i) {

        Video c = mVideosList.get(i);
        /*videosViewHolder.setName(c.title);*/

        videosViewHolder.setId(c.id);
       videosViewHolder.setImage(c.id);
       durationinMillis=c.duration;




    }




    @Override
    public int getItemCount() {
        return mVideosList.size();
    }


    public class VideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;
        TextView videoTitle;
        SimpleDraweeView videoImage;
        String path;
        private String videoName;
        TextView durationVideo;

        //   public TextView displayName;
        // public ImageView messageImage;
        VideosViewHolder(View view) {
            super(view);

            mView = view;



            mView.setOnClickListener((View.OnClickListener) this);


        }
      /*  public void setName(String name) {
            videoTitle = (TextView) mView.findViewById(R.id.videoName);
            if(name!=null) {
                videoTitle.setText(name);
            }else
            {
                videoTitle.setText("No Name");
            }
            videoName=name;
        }*/

        public void setImage(final String vid_id) {
            videoImage = (SimpleDraweeView) mView.findViewById(R.id.video_preview_Image);
            Uri uri =  getVideoUri(mContext,vid_id);
            File vidFile=new File(uri.toString());
            Bitmap vidThumb= ThumbnailUtils.createVideoThumbnail(uri.toString(),MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
           MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(uri.toString());
            Bitmap thumbframe=mediaMetadataRetriever.getFrameAtTime(7000000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            videoImage.setImageBitmap(thumbframe);

        }


        public void setId(String id) {
            path=id;
        }

        @Override
        public void onClick(View v) {

            String uri=getVideoUri(mContext,path).toString();

            Intent uploadVideoIntent=new Intent(mContext, UploadVideoActivity.class);
            uploadVideoIntent.putExtra("videoname",videoName);
            uploadVideoIntent.putExtra("videoUri",uri);
            uploadVideoIntent.putExtra("video_duration",durationinMillis);
            mContext.startActivity(uploadVideoIntent);



            Toast.makeText(mContext,uri,Toast.LENGTH_SHORT).show();




        }

        Uri getVideoUri(Context context, String id) {
            final String[] projection = new String[]{
                    BaseColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.Video.Media.ALBUM
            };
            final StringBuilder selection = new StringBuilder();
            selection.append(BaseColumns._ID + " IN (");
            selection.append(id);
            selection.append(")");
            final Cursor c = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection.toString(),
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


