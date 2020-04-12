package de.vectordata.skynet.ui.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.widget.ImageView;

public class DefaultProfileImage {

    private static final int[] COLORS = {
            0xFFF44336,
            0xFF9C27B0,
            0xFF673AB7,
            0xFFE91E63,
            0xFF3F51B5,
            0xFF2196F3,
            0xFF03A9F4,
            0xFF00BCD4,
            0xFF009688,
            0xFF4CAF50,
            0xFF8BC34A,
            0xFFFFEB3B,
            0xFFFFC107,
            0xFFFF9800,
            0xFFFF5722
    };

    private Bitmap bitmap;

    private DefaultProfileImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static DefaultProfileImage create(String nickname, long accountId, int width, int height) {
        String initials = nickname.length() < 2 ? nickname : nickname.substring(0, 1);

        int color = COLORS[(int) Math.abs(accountId % COLORS.length)];
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        canvas.drawRect(new Rect(0, 0, width, height), paint);
        paint.setColor(Color.argb(160, 255, 255, 255));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(68);

        Rect textBounds = new Rect();
        paint.getTextBounds(initials, 0, 1, textBounds);
        int textHeight = Math.abs(textBounds.height());

        canvas.drawText(initials, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f + textHeight / 2f, paint);
        return new DefaultProfileImage(bitmap);
    }

    public void loadInto(ImageView view) {
        view.setImageBitmap(transform(bitmap));
        bitmap.recycle();
    }

    private Bitmap transform(Bitmap source) {
        float size = source.getHeight() > source.getWidth() ? source.getWidth() : source.getHeight();
        Bitmap output = Bitmap.createBitmap((int) size, (int) size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect srcRect = new Rect(0, 0, source.getWidth(), source.getHeight());
        final Rect trgRect = new Rect(0, 0, (int) size, (int) size);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        float radius = size / 2F;
        canvas.drawCircle(size / 2, size / 2, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, trgRect, trgRect, paint);

        source.recycle();
        return output;
    }

    private Rect centerRectInRect(Rect big, Rect center) {
        int height = center.height();
        center.top += big.height() / 2 - height / 2;
        center.bottom += big.height() / 2 - height / 2;
        return center;
    }

}
