package info.dvkr.screenstream.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.google.firebase.crash.FirebaseCrash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import info.dvkr.screenstream.R;
import info.dvkr.screenstream.ScreenStreamApplication;
import info.dvkr.screenstream.data.HttpServer;

public final class NotifyImageGenerator {
    private static int sCurrentScreenSizeX;
    private static byte[] sCurrentDefaultScreen;

    public static byte[] getDefaultScreen(final Context context) {
        if (sCurrentScreenSizeX != ScreenStreamApplication.getScreenSize().x)
            sCurrentDefaultScreen = null;
        if (sCurrentDefaultScreen != null) return sCurrentDefaultScreen;

        sCurrentDefaultScreen = generateImage(context.getString(R.string.image_generator_press),
                context.getString(R.string.main_activity_start_stream).toUpperCase(),
                context.getString(R.string.image_generator_on_device));

        sCurrentScreenSizeX = ScreenStreamApplication.getScreenSize().x;
        return sCurrentDefaultScreen;
    }


    public static byte[] getClientNotifyImage(final Context context, final int reason) {
        if (reason == HttpServer.SERVER_SETTINGS_RESTART)
            return generateImage(context.getString(R.string.image_generator_settings_changed), "", context.getString(R.string.image_generator_go_to_new_address));
        if (reason == HttpServer.SERVER_PIN_RESTART)
            return generateImage(context.getString(R.string.image_generator_settings_changed), "", context.getString(R.string.image_generator_reload_this_page));
        return null;
    }


    private static byte[] generateImage(final String text1, final String text2, final String text3) {
        final Bitmap bitmap = Bitmap.createBitmap(ScreenStreamApplication.getScreenSize().x,
                ScreenStreamApplication.getScreenSize().y,
                Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmap);
        canvas.drawRGB(255, 255, 255);

        int textSize, x, y;
        final Rect bounds = new Rect();
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (!"".equals(text1)) {
            textSize = (int) (12 * ScreenStreamApplication.getScale());
            paint.setTextSize(textSize);
            paint.setColor(Color.BLACK);
            paint.getTextBounds(text1, 0, text1.length(), bounds);
            x = (bitmap.getWidth() - bounds.width()) / 2;
            y = (bitmap.getHeight() + bounds.height()) / 2 - 2 * textSize;
            canvas.drawText(text1, x, y, paint);
        }

        if (!"".equals(text2)) {
            textSize = (int) (16 * ScreenStreamApplication.getScale());
            paint.setTextSize(textSize);
            paint.setColor(Color.rgb(153, 50, 0));
            paint.getTextBounds(text2, 0, text2.length(), bounds);
            x = (bitmap.getWidth() - bounds.width()) / 2;
            y = (bitmap.getHeight() + bounds.height()) / 2;
            canvas.drawText(text2.toUpperCase(), x, y, paint);
        }

        if (!"".equals(text3)) {
            textSize = (int) (12 * ScreenStreamApplication.getScale());
            paint.setTextSize(textSize);
            paint.setColor(Color.BLACK);
            paint.getTextBounds(text3, 0, text3.length(), bounds);
            x = (bitmap.getWidth() - bounds.width()) / 2;
            y = (bitmap.getHeight() + bounds.height()) / 2 + 2 * textSize;
            canvas.drawText(text3, x, y, paint);
        }

        byte[] jpegByteArray = null;
        try (final ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, ScreenStreamApplication.getAppSettings().getJpegQuality(), jpegOutputStream);
            jpegByteArray = jpegOutputStream.toByteArray();
        } catch (IOException e) {
            FirebaseCrash.report(e);
        }
        bitmap.recycle();
        return jpegByteArray;
    }
}