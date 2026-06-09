package com.github.tvbox.osc.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Image utilities - base64 decoding, text placeholder, source style
 */
public class ImgUtil {
    private static final Map<String, Drawable> drawableCache = new HashMap<>();

    public static boolean isBase64Image(String picUrl) {
        return picUrl.startsWith("data:image");
    }

    public static int defaultWidth = 244;
    public static int defaultHeight = 320;

    /**
     * style: ratio (width/height), type (rect/list)
     */
    public static class Style {
        public float ratio;
        public String type;

        public Style(float ratio, String type) {
            this.ratio = ratio;
            this.type = type;
        }
    }

    public static Style initStyle() {
        String bStyle = ApiConfig.get().getHomeSourceBean().getStyle();
        if (!bStyle.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(bStyle);
                float ratio = (float) jsonObject.getDouble("ratio");
                String type = jsonObject.getString("type");
                return new Style(ratio, type);
            } catch (JSONException e) {
            }
        }
        return null;
    }

    public static int spanCountByStyle(Style style, int defaultCount) {
        int spanCount = defaultCount;
        if (style == null) return spanCount;
        if ("rect".equals(style.type)) {
            if (style.ratio >= 1.7) {
                spanCount = 3;
            } else if (style.ratio >= 1.3) {
                spanCount = 4;
            }
        } else if ("list".equals(style.type)) {
            spanCount = 1;
        }
        return spanCount;
    }

    public static int getStyleDefaultWidth(Style style) {
        if (style == null) return 280;
        int styleDefaultWidth = 280;
        if (style.ratio < 1) styleDefaultWidth = 214;
        if (style.ratio > 1.7) styleDefaultWidth = 380;
        return styleDefaultWidth;
    }

    public static Bitmap decodeBase64ToBitmap(String base64Str) {
        String base64Data = base64Str.substring(base64Str.indexOf(",") + 1);
        byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static Drawable createTextDrawable(String text) {
        if (text.isEmpty()) text = "TVBox";
        text = text.substring(0, 1);
        if (drawableCache.containsKey(text)) {
            return drawableCache.get(text);
        }
        int width = 180, height = 240;
        int randomColor = getRandomColor();
        float cornerRadius = 10f; // dp

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(randomColor);
        paint.setStyle(Paint.Style.FILL);
        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float x = width / 2f;
        float y = (height - fontMetrics.bottom - fontMetrics.top) / 2f;

        canvas.drawText(text, x, y, paint);
        Drawable drawable = new BitmapDrawable(bitmap);
        drawableCache.put(text, drawable);
        return drawable;
    }

    public static int getRandomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static void clearCache() {
        drawableCache.clear();
    }
}
