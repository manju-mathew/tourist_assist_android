package com.google.firebase.samples.apps.mlkit.carparkingdetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.WindowManager;

import com.google.firebase.samples.apps.mlkit.GraphicOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangding
 */

class ImageGraphic extends GraphicOverlay.Graphic {

    private final List<Bitmap> bitmaps;
    private final Paint paint;

    private int screenWidth;
    private int screenHeight;

    ImageGraphic(GraphicOverlay overlay, int... imageRes) {
        super(overlay);
        bitmaps = new ArrayList<>();
        paint = new Paint();
        resolveScreenSize();
        resolveBitmapFromResourceArray(imageRes);
    }

    private void resolveScreenSize() {
        WindowManager windowManager =
                (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        display.getSize(outPoint);

        screenWidth = outPoint.x;
        screenHeight = outPoint.y;
    }

    private void resolveBitmapFromResourceArray(int[] resArray) {
        for (int res : resArray) {
            Bitmap b = resolveBitmapFromResource(res);
            if (b != null) {
                bitmaps.add(b);
            }
        }
    }

    @Nullable
    private Bitmap resolveBitmapFromResource(int res) {
        return BitmapFactory.decodeResource(getApplicationContext().getResources(), res);
    }

    @Override
    public void draw(Canvas canvas) {
        if (bitmaps.isEmpty()) {
            return;
        }
        if (bitmaps.size() == 1) {
            Bitmap bitmap = bitmaps.get(0);
            Point location = getSinglePosition(bitmap);
            canvas.drawBitmap(bitmap, location.x, location.y, paint);
            return;
        }
        // must have at least two images
        Bitmap left = bitmaps.get(0);
        Bitmap right = bitmaps.get(1);
        Point[] locations = getTwoPositions(left, right);
        for (int i = 0; i < locations.length; i++) {
            canvas.drawBitmap(bitmaps.get(i), locations[i].x, locations[i].y, paint);
        }
    }

    private Point getSinglePosition(Bitmap bitmap) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Point point = new Point();
        point.x = (screenWidth - imageWidth) / 2;
        point.y = (screenHeight - imageHeight) / 2;
        return point;
    }

    private Point[] getTwoPositions(Bitmap left, Bitmap right) {
        int leftWidth = left.getWidth();
        int leftHeight = left.getHeight();
        int rightWidth = right.getWidth();
        int rightHeight = right.getHeight();
        int gap = 24;
        int fullWidth = leftWidth + rightWidth + gap;

        Point leftStart = new Point();
        leftStart.x = (screenWidth - fullWidth) / 2;
        leftStart.y = (screenHeight - leftHeight) / 2;

        Point rightStart = new Point();
        rightStart.x = leftStart.x + leftWidth + gap;
        rightStart.y = (screenHeight - rightHeight) / 2;

        return new Point[] {leftStart, rightStart};
    }
}
