package com.hpe.oo.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.hpe.oo.android.model.Run;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by revnic on 5/8/2016.
 */
public class RunsDownloader<T> extends HandlerThread {
    private static final String TAG = "RunsDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap();
    private ThumbnailDownloadListener<T> mThumbnailDownloadListenr;


    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setRunsDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListenr = listener;
    }

    public RunsDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);

        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, " Got a request from URL " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    private void handleRequest(final T target) {

        if (target == null) {
            return;
        }

        final String url = mRequestMap.get(target);

        if (url == null) {
            return;
        }

            /*
            List<Run> runs = new OoConnector("http://16.60.160.67:8080/oo/rest/v2").getUrlBytes();
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mRequestMap.get(target) != url){
                        return;
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloadListenr.onThumbnailDownloaded(target, bitmap);
                }
            });
            */

    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
}
