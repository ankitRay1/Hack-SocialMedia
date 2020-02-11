package com.videodownloader.whatsappstatussaver.download_feature;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.LruCache;
import android.widget.ImageView;

public class VideoThumbDownloader {

    private LruCache<String, Bitmap> lruCache;

    @SuppressLint("NewApi")
    public VideoThumbDownloader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();// obtain maximum
        // memory to run
        int maxSize = maxMemory / 4;// get cache memory size 35
        lruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // this will be called when the cache deposited in each
                return value.getByteCount();
            }
        };
    }

    public void addVideoThumbToCache(String path, Bitmap bitmap) {
        if (getVideoThumbToCache(path) == null && bitmap != null) {

            lruCache.put(path, bitmap);
        }
    }

    public Bitmap getVideoThumbToCache(String path) {

        return lruCache.get(path);
    }

    public void showThumbByAsynctack(String path, ImageView imgview) {

        if (getVideoThumbToCache(path) == null) {
            // asynchronous loading
            new MyBobAsynctack(imgview, path).execute(path);
        } else {
            imgview.setImageBitmap(getVideoThumbToCache(path));
        }
    }

    class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        private String path;

        public MyBobAsynctack(ImageView imageView, String path) {
            this.imgView = imageView;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(params[0], MediaStore.Video.Thumbnails.MICRO_KIND);
            // provide
            if (getVideoThumbToCache(params[0]) == null) {
                addVideoThumbToCache(path, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imgView.getTag().equals(path)) {
                imgView.setImageBitmap(bitmap);
            }
        }
    }
}
