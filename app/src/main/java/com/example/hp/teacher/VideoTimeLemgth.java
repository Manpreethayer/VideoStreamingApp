package com.example.hp.teacher;

import android.app.Application;

public class VideoTimeLemgth extends Application {

     static String convertMillis(String millis) {
        long timeMillis = Long.parseLong(millis);
        long time = timeMillis / 1000;
        String seconds = Integer.toString((int) (time % 60));
        String minutes = Integer.toString((int) ((time % 3600) / 60));
        String hours = Integer.toString((int) (time / 3600));
        for (int i = 0; i < 2; i++) {
            if (seconds.length() < 2) {
                seconds = "0" + seconds;
            }
            if (minutes.length() < 2) {
                minutes = "0" + minutes;
            }
            if (hours.length() < 2) {
                hours = "0" + hours;
            }
        }

        if(hours.equals("00"))
        {
            return minutes + ":" + seconds;
        }
        return hours + ":" + minutes + ":" + seconds;

    }
}
