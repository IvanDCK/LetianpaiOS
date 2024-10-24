package com.renhejia.robot.launcherbaselib.qrcode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import com.renhejia.robot.launcherbaselib.app.RHJApp
import com.renhejia.robot.launcherbaselib.overseas.OverseasUtils

object QRCodeUtil {
    private val TAG: String = QRCodeUtil::class.java.simpleName
    private const val BLACK = -0x1000000

    @Throws(Exception::class)
    fun createBitmap(size: Int): Bitmap? {
        //TODO 需要增加拼配网络请求的逻辑，这部分需要确认网络请求格后增加这部分逻辑，再再验证
        var url: String = ActivationConsts.LTP_ACTIVATE_URL

        if (OverseasUtils.isInternationalized(RHJApp.rhjApp)) {
            url = ActivationConsts.Companion.LTP_ACTIVATE_OVERSEAS_URL
        }
        //TODO 需要增加网络请求逻辑
        Log.i(TAG, "url===$url")
        val bitmap = createQRCode(url, size)
        return bitmap
    }

    fun createQRCode(str: String?, widthAndHeight: Int): Bitmap? {
//    public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException {
//        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
//        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); //容错率
//        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//        hints.put(EncodeHintType.MARGIN, 1);
//        BitMatrix matrix = new QRCodeWriter().encode(str,
//                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
//        int width = matrix.getWidth();
//        int height = matrix.getHeight();
//        int[] pixels = new int[width * height];
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if (matrix.get(x, y)) {
//                    pixels[y * width + x] = BLACK;
//                }
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(width, height,
//                                            Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//        return bitmap;
        return null
    }

    fun decToHex(dec: Int): String {
        var dec = dec
        var hex = ""
        while (dec != 0 && dec != -1) {
            var h = (dec and 0xff).toString(16)
            if ((h.length and 0x01) == 1) {
                h = "0$h"
            }
            hex = hex + h
            dec = dec shr 8
        }
        return hex
    }

    /**
     * 加密 RC4>Base64
     * @param
     * @return
     */
    fun encodeWithOnlyHardCode(data: IntArray?): String? {
        // TODO 需要做加密逻辑
        return null
    }

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     * @param src byte[] data
     * @return hex string
     */
    fun bytesToHexString(src: IntArray?): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.size <= 0) {
            return null
        }
        for (i in src.indices) {
            val v = src[i] and 0xFF
            val hv = Integer.toHexString(v)
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
        }
        return stringBuilder.toString()
    }

    /**
     * 在二维码中间添加Logo图案
     */
    fun addLogo(src: Bitmap?, logo: Bitmap?): Bitmap? {
        if (src == null) {
            return null
        }

        if (logo == null) {
            return src
        }

        //获取图片的宽高
        val srcWidth = src.width
        val srcHeight = src.height
        val logoWidth = logo.width
        val logoHeight = logo.height

        if (srcWidth == 0 || srcHeight == 0) {
            return null
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src
        }

        //logo大小为二维码整体大小的1/5
//        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        val scaleFactor = srcWidth * 1.0f / 4 / logoWidth
        var bitmap: Bitmap? = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(bitmap!!)
            canvas.drawBitmap(src, 0f, 0f, null)
            canvas.scale(
                scaleFactor,
                scaleFactor,
                (srcWidth / 2).toFloat(),
                (srcHeight / 2).toFloat()
            )
            canvas.drawBitmap(
                logo,
                ((srcWidth - logoWidth) / 2).toFloat(),
                ((srcHeight - logoHeight) / 2).toFloat(),
                null
            )

            //            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.save()
            canvas.restore()
        } catch (e: Exception) {
            bitmap = null
            e.stackTrace
        }

        return bitmap
    }

    fun getLogoBitmap(context: Context, res: Int): Bitmap {
        val logoBitmap = BitmapFactory.decodeResource(context.resources, res)
        return logoBitmap
    }
}
