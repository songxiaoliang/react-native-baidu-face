/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */

package com.baidu.idl.face.platform.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 这个类提供一些操作Bitmap的方法
 */
public final class BitmapUtils {
    /**
     * Log TAG
     */
    private static final String TAG = "ImageUtils";
    /**
     * 保存图片的质量：100
     */
    private static final int QUALITY = 100;
    /**
     * 图像的旋转方向是0
     */
    public static final int ROTATE0 = 0;
    /**
     * 图像的旋转方向是90
     */
    public static final int ROTATE90 = 90;
    /**
     * 图像的旋转方向是180
     */
    public static final int ROTATE180 = 180;
    /**
     * 图像的旋转方向是270
     */
    public static final int ROTATE270 = 270;
    /**
     * 图像的旋转方向是360
     */
    public static final int ROTATE360 = 360;
    /**
     * 图片太大内存溢出后压缩的比例
     */
    public static final int PIC_COMPRESS_SIZE = 4;
    /**
     * 图像压缩边界
     */
    public static final int IMAGEBOUND = 128;
    /**
     * 图片显示最大边的像素
     */
    public static final int MAXLENTH = 1024;
    /**
     * 默认的最大尺寸
     */
    private static final int DEFAULT_MAX_SIZE_CELL_NETWORK = 600;
    /**
     * 题编辑wifi环境下压缩的最大尺寸
     */
    private static final int QUESTION_MAX_SIZE_CELL_NETWORK = 1024;
    /**
     * 图片压缩的质量
     */
    private static final int QUESTION_IMAGE_JPG_QUALITY = 75;
    /**
     * 默认的图片压缩的质量
     */
    private static final int DEFAULT_IMAGE_JPG_QUALITY = 50;
    /**
     * 网络请求超时时间
     */
    private static final int CONNECTTIMEOUT = 3000;

    public static final String IMAGE_KEY_SUFFIX = "jpg";
    private static final int DEFAULT_JPEG_QUALITY = 90;

    /**
     * Private constructor to prohibit nonsense instance creation.
     */
    private BitmapUtils() {
    }

    /**
     * 得到要显示的图片数据
     *
     * @param context     Context
     * @param data        拍照保存的图片数据byte[]类型
     * @param orientation 图片方向
     * @return Bitmap 返回显示的图片bitmap
     */
    public static Bitmap createBitmap(Context context, byte[] data, float orientation) {
        Bitmap bitmap = null;
        Bitmap transformed = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try {

            int width = DensityUtils.getDisplayWidth(context);
            int hight = DensityUtils.getDisplayHeight(context);
            int min = Math.min(width, hight);
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            opts.inSampleSize =
                    BitmapUtils.computeSampleSize(opts, min, BitmapUtils.MAXLENTH * BitmapUtils.MAXLENTH);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            transformed = BitmapUtils.rotateBitmap(orientation, bitmap);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
            if (transformed != null && !transformed.isRecycled()) {
                transformed.recycle();
                transformed = null;
            }
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            opts.inSampleSize =
                    BitmapUtils.computeSampleSize(opts, -1, opts.outWidth * opts.outHeight
                            / BitmapUtils.PIC_COMPRESS_SIZE);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            transformed = BitmapUtils.rotateBitmap(orientation, bitmap);
        }
        if (transformed != bitmap && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        return transformed;

    }

    /**
     * 根据从数据中读到的方向旋转图片
     *
     * @param orientation 图片方向
     * @param bitmap      要旋转的bitmap
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(float orientation, Bitmap bitmap) {
        Bitmap transformed;
        Matrix m = new Matrix();
        if (orientation == 0) {
            transformed = bitmap;
        } else {
            m.setRotate(orientation);
            transformed = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }
        return transformed;
    }

    /**
     * 获取无损压缩图片合适的压缩比例
     *
     * @param options        图片的一些设置项
     * @param minSideLength  最小边长
     * @param maxNumOfPixels 最大的像素数目
     * @return 返回合适的压缩值
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = BitmapUtils.computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) { // SUPPRESS CHECKSTYLE
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8; // SUPPRESS CHECKSTYLE
        }
        return roundedSize;
    }

    /**
     * 获取无损压缩图片的压缩比
     *
     * @param options        图片的一些设置项
     * @param minSideLength  最小边长
     * @param maxNumOfPixels 最大的像素数目
     * @return 返回合适的压缩值
     */
    public static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
                                               int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound =
                (minSideLength == -1) ? BitmapUtils.IMAGEBOUND : (int) Math.min(
                        Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 解析图片的旋转方向
     *
     * @param path 图片的路径
     * @return 旋转角度
     */
    public static int decodeImageDegree(String path) {
        int degree = ROTATE0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation =
                    exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = ROTATE90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = ROTATE180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = ROTATE270;
                    break;
                default:
                    degree = ROTATE0;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            degree = ROTATE0;
        }
        return degree;
    }

    /**
     * @param bitmap  图片
     * @param quality 生成的JPG的质量
     * @param maxSize 最大边像素数
     * @return base64编码的数据
     */
    public static String bitmapToJpegBase64(Bitmap bitmap, int quality, float maxSize) {
        try {
            float scale = maxSize / Math.max(bitmap.getWidth(), bitmap.getHeight());
            if (scale < 1) {
                bitmap = scale(bitmap, scale);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, quality, out);
            byte[] data = out.toByteArray();
            out.close();

            return Base64Utils.encodeToString(data, Base64Utils.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据图片类别设置图片最大SIZE和图片压缩质量
     *
     * @param context Context对象
     * @param type    界面类型
     * @param bitmap  图片
     * @return base64编码的数据
     */
    public static String bitmapToJpegBase64(Context context, String type, Bitmap bitmap) {
        // 图片最大SIZE
        float maxSize = getSizeParams(context, type) * 1.0f;
        // 图片压缩质量
        int quality = getQuealityParams(context, type);
        try {
            float scale = maxSize / Math.max(bitmap.getWidth(), bitmap.getHeight());
            if (scale < 1) {
                bitmap = scale(bitmap, scale);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, quality, out);
            byte[] data = out.toByteArray();
            out.close();

            return Base64Utils.encodeToString(data, Base64Utils.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置图片大小
     *
     * @param context Context对象
     * @param type    Category对象
     * @return 压缩的图片大小
     */
    private static int getSizeParams(Context context, String type) {
        int maxSize = DEFAULT_MAX_SIZE_CELL_NETWORK;
        if (type == null) {
            return maxSize;
        }
        maxSize = DEFAULT_MAX_SIZE_CELL_NETWORK;
        return maxSize;
    }

    /**
     * 设置图片压缩质量
     *
     * @param context Context对象
     * @param type    Category对象
     * @return 压缩的质量
     */
    private static int getQuealityParams(Context context, String type) {
        int quality = DEFAULT_IMAGE_JPG_QUALITY;
        if (type == null) {
            return quality;
        }
        return quality;
    }

    /**
     * 等比压缩图片
     *
     * @param bitmap 原图
     * @param scale  压缩因子
     * @return 压缩后的图片
     */
    private static Bitmap scale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 尺寸缩放
     *
     * @param bitmap bitmap
     * @param w      width
     * @param h      height
     * @return scaleBitmap
     */
    public static Bitmap scale(Bitmap bitmap, int w, int h) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

    }

    /**
     * 等比压缩图片
     *
     * @param resBitmap 原图
     * @param desWidth  压缩后图片的宽度
     * @param desHeight 压缩后图片的高度
     * @return 压缩后的图片
     */
    public static Bitmap calculateInSampleSize(Bitmap resBitmap, int desWidth, int desHeight) {
        int resWidth = resBitmap.getWidth();
        int resHeight = resBitmap.getHeight();
        if (resHeight > desHeight || resWidth > desWidth) {
            // 计算出实际宽高和目标宽高的比率
            final float heightRatio = (float) desHeight / (float) resHeight;
            final float widthRatio = (float) desWidth / (float) resWidth;
            float scale = heightRatio < widthRatio ? heightRatio : widthRatio;
            return scale(resBitmap, scale);
        }
        return resBitmap;
    }

    /**
     * 解析图片的旋转方向
     *
     * @param jpeg 图片数据
     * @return 旋转角度
     */
    public static int decodeImageDegree(byte[] jpeg) {
        int degree = ImageExif.getOrientation(jpeg);
        return degree;
    }

    /**
     * 得到要显示的图片数据
     *
     * @param filename 图片文件
     * @return Bitmap 返回显示的图片bitmap
     */
    public static Bitmap createBitmap(Context context, String filename, int orientatoin) {
        Bitmap bitmap = null;
        Bitmap transformed = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try {
            int width = DensityUtils.getDisplayWidth(context);
            int hight = DensityUtils.getDisplayHeight(context);
            int min = Math.min(width, hight);
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, opts);
            opts.inSampleSize =
                    BitmapUtils.computeSampleSize(opts, min, BitmapUtils.MAXLENTH * BitmapUtils.MAXLENTH);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(filename, opts);
            transformed = BitmapUtils.rotateBitmap(orientatoin, bitmap);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (bitmap != null) {
                bitmap.recycle();
            }
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, opts);
            opts.inSampleSize =
                    BitmapUtils.computeSampleSize(opts, -1, opts.outWidth * opts.outHeight
                            / BitmapUtils.PIC_COMPRESS_SIZE);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(filename, opts);
            transformed = BitmapUtils.rotateBitmap(orientatoin, bitmap);
        }
        if (transformed != bitmap && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        return transformed;
    }

    public static Bitmap createBitmap(Context context, byte[] imageByte, int orientatoin) {
        Bitmap bitmap = null;
        Bitmap transformed = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try {
            int width = DensityUtils.getDisplayWidth(context);
            int hight = DensityUtils.getDisplayHeight(context);
            int min = Math.min(width, hight);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            transformed = BitmapUtils.rotateBitmap(orientatoin, bitmap);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (bitmap != null) {
                bitmap.recycle();
            }
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, opts);
            opts.inSampleSize =
                    BitmapUtils.computeSampleSize(opts, -1, opts.outWidth * opts.outHeight
                            / BitmapUtils.PIC_COMPRESS_SIZE);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, opts);
            transformed = BitmapUtils.rotateBitmap(orientatoin, bitmap);
        }
        if (transformed != bitmap && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        return transformed;
    }

    public static Bitmap createBitmap(Context context, int[] argbByte, Rect roundRect, Rect cropRect, int orientatoin) {
        Bitmap bitmap = null;
        Bitmap transformed = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try {
//            int width = DensityUtils.getDisplayWidth(context);
//            int hight = DensityUtils.getDisplayHeight(context);
//            int min = Math.min(width, hight);
            opts.inJustDecodeBounds = false;
            bitmap = Bitmap.createBitmap(argbByte, roundRect.width(), roundRect.height(), Bitmap.Config.ARGB_8888);
//            bitmap = Bitmap.createBitmap(roundRect.width(), roundRect.height(), Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(argbByte, 0, roundRect.width(), 0, 0, roundRect.width(), roundRect.height());
            bitmap = Bitmap.createBitmap(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height());

            transformed = BitmapUtils.rotateBitmap(orientatoin, bitmap);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        if (transformed != bitmap && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        return transformed;
    }

    public static Bitmap createBitmap(Context context, int pw, int ph, int[] argbByte) {
        Bitmap bitmap = null;
        Bitmap transformed = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try {
            int width = DensityUtils.getDisplayWidth(context);
            int hight = DensityUtils.getDisplayHeight(context);
            int min = Math.min(width, hight);
            opts.inJustDecodeBounds = false;
            bitmap = Bitmap.createBitmap(argbByte, pw, ph, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        if (transformed != bitmap && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        return transformed;
    }

    /**
     * 图片转换为base64
     *
     * @param bitmap  图片
     * @param quality 压缩质量
     * @return base64编码的数据
     */
    public static String bitmapToJpegBase64(Bitmap bitmap, int quality) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, quality, out);
            byte[] data = out.toByteArray();
            return Base64Utils.encodeToString(data, Base64Utils.NO_WRAP);
        } catch (Exception e) {
            return null;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 从文件中加载图片数据
     *
     * @param path 图片的本地存储路径
     * @return Bitmap 图片数据
     */
    public static Bitmap loadBitmapFromFile(String path) {
        Bitmap bitmap = null;
        if (path != null) {
            try {
                bitmap = BitmapFactory.decodeFile(path);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return bitmap;
    }


    /**
     * 从文件中加载图片数据
     *
     * @param path 图片的本地存储路径
     * @return Bitmap 图片数据
     */
    public static Bitmap loadBitmapFromFile(Context context, String path) {
        Bitmap bitmap = null;
        if (path != null) {
            try {
                int orientatoin = decodeImageDegree(path);
                bitmap = createBitmap(context, path, orientatoin);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return bitmap;
    }

    public static boolean saveTakePictureResult(final Context context, String folderName, Bitmap image) {
        boolean flag = false;
        File file = null;
        // 取得图片保存目录
        String dir = getTakePictureCacheDir(context);
        if (!TextUtils.isEmpty(dir)) {
            // 取得图片文件夹名称
            dir = dir + File.separator + folderName;
            file = new File(dir);
            // 判断保存图片文件夹是否存在
            if (ensureDirectoryExist(file)) {
                String imageFileName = String.format("B%s.%s",
                        System.currentTimeMillis() / 1000, IMAGE_KEY_SUFFIX);
                file = new File(folderName + File.separator + imageFileName);
                boolean fileFlag = true;
                try {
                    if (!file.exists()) {
                        fileFlag = file.createNewFile();
                    }
                } catch (IOException e) {
                    fileFlag = false;
                }
                if (fileFlag) {
                    // 保存图片数据
                    compressToFile(image, 100, dir + File.separator + imageFileName);
                    flag = true;
                }
            } else {
                // 保存目录不可使用
                flag = false;
            }
        } else {
            // 保存目录不可使用
            flag = false;
        }
        return flag;
    }

    public static String getTakePictureCacheDir(Context context) {
        File cacheDir = null;
        // 如果SDCard可写，得到SDCard路径
        boolean sdcardWriteable = TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState());
        if (sdcardWriteable) {
            cacheDir = Environment.getExternalStorageDirectory();
        }
        return (null != cacheDir) ? cacheDir.getAbsolutePath() : null;
    }

    public static Uri saveTakePictureImage(byte[] data, String dir, String filename) {
        File file = new File(dir, filename);
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
            fout.write(data);
            fout.flush();
        } catch (Exception e) {
            e.printStackTrace();

            // 异常时删除保存失败的文件
            try {
                if (file != null && file.exists() && file.isFile()) {
                    file.delete();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Uri.fromFile(file);
    }

    public static boolean ensureDirectoryExist(final File dir) {
        if (dir == null) {
            return false;
        }
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (SecurityException e) {
                return false;
            }
        }
        return true;
    }

    public static void compressToFile(Bitmap bitmap, String path) {
        compressToFile(bitmap, DEFAULT_JPEG_QUALITY, path);
    }

    public static void compressToFile(Bitmap bitmap, int quality, String path) {
        compressToFile(bitmap, CompressFormat.JPEG, quality, path);
    }

    public static void compressToFile(Bitmap bitmap, CompressFormat format, int quality, String path) {
        File f = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            bitmap.compress(format, quality, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // 异常时删除保存失败的文件
            try {
                if (f != null && f.exists() && f.isFile()) {
                    f.delete();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            try {
                if (f != null && f.exists() && f.isFile()) {
                    f.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            IoUtils.closeQuietly(fos);
        }
    }

    public static Bitmap createLivenessBitmap(Context context, int[] argbByte, Rect roundRect, Rect cropRect, int orientatoin) {
        Bitmap bitmap = null;
        Bitmap transformed = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try {
//            int width = DensityUtils.getDisplayWidth(context);
//            int hight = DensityUtils.getDisplayHeight(context);
//            int min = Math.min(width, hight);
            opts.inJustDecodeBounds = false;
            bitmap = Bitmap.createBitmap(argbByte, roundRect.width(), roundRect.height(), Bitmap.Config.ARGB_8888);
//            bitmap = Bitmap.createBitmap(roundRect.width(), roundRect.height(), Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(argbByte, 0, roundRect.width(), 0, 0, roundRect.width(), roundRect.height());
            bitmap = Bitmap.createBitmap(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height());

            transformed = BitmapUtils.rotateBitmap(orientatoin, bitmap);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        if (transformed != bitmap && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        return transformed;
    }

    public static Bitmap createLivenessBitmap(Context context, int[] argbByte, Rect roundRect) {
        Bitmap transformed = null;
        try {
            transformed = Bitmap.createBitmap(argbByte,
                    roundRect.width(), roundRect.height(),
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (transformed != null) {
                transformed.recycle();
            }
        }
        return transformed;
    }
}
