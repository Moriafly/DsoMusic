package com.dirror.music.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.concurrent.Executor;

/* compiled from: MusicApp */
public class aa {
    public static final Object a = new Object();

    static {
    }

    public aa(Handler handler) {

    }

    public static boolean a(Context context, Intent[] intentArr, Bundle bundle) {
        int i = Build.VERSION.SDK_INT;
        context.startActivities(intentArr, bundle);
        return true;
    }

    public static File[] b(Context context, String str) {
        int i = Build.VERSION.SDK_INT;
        return context.getExternalFilesDirs(str);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable c(Context context, int i) {
        int i2 = Build.VERSION.SDK_INT;
        return context.getDrawable(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static File d(Context context) {
        int i = Build.VERSION.SDK_INT;
        return context.getNoBackupFilesDir();
    }

    public static void a(Context context, Intent intent, Bundle bundle) {
        int i = Build.VERSION.SDK_INT;
        context.startActivity(intent, bundle);
    }

    public static File[] b(Context context) {
        int i = Build.VERSION.SDK_INT;
        return context.getExternalCacheDirs();
    }

    public static Executor c(Context context) {
        if (Build.VERSION.SDK_INT >= 28) {
            return context.getMainExecutor();
        }
        return (Executor) new aa(new Handler(context.getMainLooper()));
    }

    public static int a(Context context, int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getColor(i);
        }
        return context.getResources().getColor(i);
    }

    public static ColorStateList b(Context context, int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getColorStateList(i);
        }
        return context.getResources().getColorStateList(i);
    }

    public static int a(Context context, String str) {
        if (str != null) {
            return context.checkPermission(str, Process.myPid(), Process.myUid());
        }
        throw new IllegalArgumentException("permission is null");
    }

    public static Context a(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            return context.createDeviceProtectedStorageContext();
        }
        return null;
    }

    public static void a(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
