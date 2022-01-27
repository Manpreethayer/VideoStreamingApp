package com.example.hp.teacher.Videos;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.example.hp.teacher.Songs.models.Song;
import com.example.hp.teacher.Videos.models.Video;

import java.util.ArrayList;
import java.util.List;

public class VideosLoader {

    private static final long[] sEmptyList = new long[0];

    public static ArrayList<Video> getVideosForCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                String thumbnail = cursor.getString(2);
                String duration=cursor.getString(3);




                arrayList.add(new Video(id,title,thumbnail,duration));
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

   /* public static Video getVideoForCursor(Cursor cursor) {
        Video video = new Video();
        if ((cursor != null) && (cursor.moveToFirst())) {
            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String thumb = cursor.getString(2);

            video = new Video(id, title,thumb);
        }

        if (cursor != null)
            cursor.close();
        return video;
    }
*/
   /* public static final long[] getVideoListForCursor(Cursor cursor) {
        if (cursor == null) {
            return sEmptyList;
        }
        final int len = cursor.getCount();
        final long[] list = new long[len];
        cursor.moveToFirst();
        int columnIndex = -1;
        try {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
        } catch (final IllegalArgumentException notaplaylist) {
            columnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }
        for (int i = 0; i < len; i++) {
            list[i] = cursor.getLong(columnIndex);
            cursor.moveToNext();
        }
        cursor.close();
        cursor = null;
        return list;
    }*/

   /* public static Video getVideoFromPath(String videoPath, Context context) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.DATA;
        String[] selectionArgs = {videoPath};
        String[] projection = new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id"};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            Video video = getVideoForCursor(cursor);
            cursor.close();
            return video;
        }
        else return new Video();
    }*/

    public static ArrayList<Video> getAllVideos(Context context) {
        return getVideosForCursor(makeVideoCursor(context, null, null));
    }

   /* public static long[] getVideoListInFolder(Context context, String path) {
        String[] whereArgs = new String[]{path + "%"};
        return getVideoListForCursor(makeVideoCursor(context, MediaStore.Video.Media.DATA + " LIKE ?", whereArgs, null));
    }*/

  /*  public static Video getVideoForID(Context context, long id) {
        return getVideoForCursor(makeVideoCursor(context, "_id=" + String.valueOf(id), null));
    }*/

   /* public static List<Video> searchVideos(Context context, String searchString, int limit) {
        ArrayList<Video> result = getVideosForCursor(makeVideoCursor(context, "title LIKE ?", new String[]{searchString + "%"}));
        if (result.size() < limit) {
            result.addAll(getVideosForCursor(makeVideoCursor(context, "title LIKE ?", new String[]{"%_" + searchString + "%"})));
        }
        return result.size() < limit ? result : result.subList(0, limit);
    }*/


    public static Cursor makeVideoCursor(Context context, String selection, String[] paramArrayOfString) {
        final String videoSortOrder = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
        return makeVideoCursor(context, selection, paramArrayOfString, videoSortOrder);
    }

    private static Cursor makeVideoCursor(Context context, String selection, String[] paramArrayOfString, String sortOrder) {
        String selectionStatement = "is_video=1 AND title != ''";

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        return context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.MINI_THUMB_MAGIC,MediaStore.Video.Media.DURATION}, null, paramArrayOfString, sortOrder);

    }

  /*  public static Video videoFromFile(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        return new Song(
                -1,
                -1,
                -1,
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)),
                0
        );
    }*/

}
