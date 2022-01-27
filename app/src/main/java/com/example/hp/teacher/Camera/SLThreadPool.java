package com.example.hp.teacher.Camera;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by sungeunkim on 2015. 11. 16..
 */
public class SLThreadPool {

    public static Executor executor;
    private static final Object LOCK = new Object();
    public static Handler handler;

    public static void execute(Runnable runnable){
        getExecutor().execute(runnable);
    }

    public static Executor getExecutor(){
        synchronized (LOCK) {
            if (executor == null) {
                executor = AsyncTask.THREAD_POOL_EXECUTOR;
            }
        }
        return executor;
    }

    public static void postOnUiThread(Runnable runnable){
        getMainHandler().post(runnable);
    }

    public static Handler getMainHandler(){
        synchronized (LOCK) {
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper());
            }
        }
        return handler;
    }

}
