package com.jacky.foundation.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.Objects;

public class DisplayUtil {
    public static int dp2px(float dp, Resources resources) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public static int dp2px(float dp) {
        Resources resources = Objects.requireNonNull(AppGlobals.INSTANCE.get()).getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public static int sp2px(float dp) {
        Resources resources = Objects.requireNonNull(AppGlobals.INSTANCE.get()).getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, resources.getDisplayMetrics());
    }


    public static int getDisplayWidthInPx(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
        return 0;

    }

    public static int getDisplayHeightInPx(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.y;
        }
        return 0;
    }

    public static int getStatusBarDimensionPx() {
        int statusBarHeight = 0;
        Resources res = Objects.requireNonNull(AppGlobals.INSTANCE.get()).getResources();
        @SuppressLint({"InternalInsetResource", "DiscouragedApi"}) int resourceId =
                res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
