package com.che.zero.common;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

/**
 * Created by soap on 16/5/31.
 */
public class ImageLoadUtil {
    private static Context context;

    private static DisplayImageOptions defaultOption;

    public static void init(Context context) {
        ImageLoadUtil.context = context;
        defaultOption = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存�?
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.ARGB_8888)// 设置图片的解码类�?/
                .build(); // 创建配置过得DisplayImageOption对象
        File cache = new File(ClientConstant.PATH_IMAGE_TEMP);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).
                threadPoolSize(3)// 线程池内加载的线程数
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCache(new UnlimitedDiskCache(cache)) // 自定义缓存路径
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) // 将保存的URI名称用MD5
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOption)
                .build();

        ImageLoader.getInstance().init(config);
    }

//    public static void loadImageS(final String fileName, final ImageView imageView, final int r) {
//        if (Strings.isNullOrEmpty(fileName)) {
//            imageView.setImageResource(r);
//            return;
//        }
//        ImageLoader.getInstance().cancelDisplayTask(imageView);
//        ImageLoader.getInstance().displayImage(WebServiceManage.self().getFileUrl(fileName), imageView, new DisplayImageOptions.Builder().cloneFrom(defaultOption).showImageOnFail(r).build());
//    }
//
//    public static void loadImageSC(final String fileName, final ImageView imageView, final int r) {
//        if (Strings.isNullOrEmpty(fileName)) {
//            imageView.setImageResource(r);
//            return;
//        }
//        ImageLoader.getInstance().cancelDisplayTask(imageView);
//        ImageLoader.getInstance().displayImage(WebServiceManage.self().getFileUrlWithCheck(fileName, ClientConstant.CURRENT_LOGIN_USER.getUserKey()), imageView, new DisplayImageOptions.Builder().cloneFrom(defaultOption).showImageOnFail(r).build());
//    }

    public static void loadImageF(File file, final ImageView imageView, final int r) {
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        ImageLoader.getInstance().displayImage("file://" + file.getAbsolutePath(), imageView, new DisplayImageOptions.Builder().cloneFrom(defaultOption).showImageOnFail(r).build());
    }

    public static void loadImageF(File file, final ImageView imageView) {
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        ImageLoader.getInstance().displayImage("file://" + file.getAbsolutePath(), imageView);
    }

    public static void loadImageF(String filePath, final ImageView imageView) {
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        ImageLoader.getInstance().displayImage("file://" + filePath, imageView);
    }

    public static void loadImageU(Uri uri, final ImageView imageView) {
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        ImageLoader.getInstance().displayImage("file://" + getImageFilePath(context, uri), imageView);
    }

    public static void loadImageU(Uri uri, final ImageView imageView, ImageLoadingListener imageLoadingListener) {
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        ImageLoader.getInstance().displayImage("file://" + getImageFilePath(context, uri), imageView, imageLoadingListener);
    }

    public static void loadImageF(String path, final ImageView imageView, ImageLoadingListener imageLoadingListener) {
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        ImageLoader.getInstance().displayImage("file://" + path, imageView, imageLoadingListener);
    }

//    public static Bitmap loadImageSB(String fileName) {
//        return ImageLoader.getInstance().loadImageSync(WebServiceManage.self().getFileUrl(fileName));
//    }

    public static String getImageFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
