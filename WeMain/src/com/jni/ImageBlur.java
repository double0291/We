package com.jni;

import android.graphics.Bitmap;

public class ImageBlur {
    // 默认压缩比例
    private static int DEFAULT_SCALE_FACTOR = 4;

    /**
     * 压缩图片后再做高斯模糊，默认压缩半径是4
     *
     * @param bitmap 原图
     * @param radius 模糊半径
     * @return 模糊后的图片
     */
    public static Bitmap doBlurAfterScale(Bitmap bitmap, int radius) {
        return doBlurAfterScale(bitmap, DEFAULT_SCALE_FACTOR, radius);
    }

    /**
     * 压缩图片后再做高斯模糊
     *
     * @param bitmap 原图
     * @param scale  压缩半径
     * @param radius 模糊半径
     * @return 模糊后的图片
     */
    public static Bitmap doBlurAfterScale(Bitmap bitmap, int scale, int radius) {
        Bitmap scalebitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale),
                (int) (bitmap.getHeight() / scale), true);
        // scale为1的话，createScaledBitmap直接返回原图
        if (scale > 1 && bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return doBlur(scalebitmap, radius);
    }

    /**
     * 对图片做高斯模糊
     *
     * @param sentBitmap 原图
     * @param radius     模糊半径
     * @return 模糊后的图片
     */
    public static Bitmap doBlur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap;
        // 非mutable的bitmap进行像素化会crash
        if (sentBitmap.isMutable()) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }
        // Jni BitMap
        ImageBlur.blur(bitmap, radius);

        return (bitmap);
    }

    public static native void blur(Bitmap bitmap, int r);

    static {
        System.loadLibrary("ImageBlur");
    }
}
