package com.dirror.music.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.Transformation;
import com.dirror.music.MyApplication;
import com.dirror.music.R;
import com.dirror.music.util.TopLevelFuncationKt;
//import g.a.a.a.c.b2;
//import g.a.a.a.c.n0;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/* compiled from: MusicApp */
public class LyricsBackgroundLayerView extends View {
    public static final String TAG = LyricsBackgroundLayerView.class.getSimpleName();
    public static final int C = MyApplication.context.getResources().getConfiguration().densityDpi;
    public static final float D = (C >= 420 ? 24.0f : 16.0f);
    public static final float E = (C >= 420 ? 72.0f : 48.0f);
    public static final Bitmap F = Bitmap.createBitmap(new int[]{-16777216}, 1, 1, Bitmap.Config.ARGB_8888);
    public float A;
    public final float f;
    public Bitmap bitmapG;
    public Bitmap h;
    public final Map<Pair<Integer, Integer>, Pair<Bitmap, Bitmap>> i;
    public Bitmap j;
    public BitmapShader k;
    public Paint l;
    public BitmapShader m;
    public Paint n;
    public Matrix o;
    public Path p;
    public float[] q;
    public AlphaAnimation r;
    public Transformation s;
    public ValueAnimator t;
    public ValueAnimator u;
    public ValueAnimator v;
    public boolean w;
    public int x;
    public boolean y;
    public float z;

    static {

    }

    public LyricsBackgroundLayerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public void a(boolean z2) {
        this.y = z2;
        if (z2) {
            pause();
            return;
        }
        resume();
        invalidate();
    }

    public final void pause() {
        if (this.t.isStarted()) {
            this.t.pause();
            this.u.pause();
            this.v.pause();
        }
    }

    public final void resume() {
        if (this.t.isStarted() && this.t.isPaused()) {
            this.t.resume();
            this.u.resume();
            this.v.resume();
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (Pair next : this.i.values()) {
            ((Bitmap) next.first).recycle();
            ((Bitmap) next.second).recycle();
        }
        this.i.clear();
    }

    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas) {
        Bitmap bitmap;
        Bitmap bitmap2;
        Bitmap bitmap3;
        Canvas canvas2 = canvas;
        Bitmap bitmap4 = this.bitmapG;
        if (bitmap4 == null || bitmap4.isRecycled() || getWidth() == 0 || getHeight() == 0) {
            this.r.cancel();
            this.r.reset();
            this.t.cancel();
            this.u.cancel();
            this.v.cancel();
            return;
        }
        if (this.k == null || (this.t.isStarted() && !this.t.isPaused())) {
            long currentTimeMillis = System.currentTimeMillis();
            Bitmap bitmap5 = this.bitmapG;
            float f2 = this.A;
            float f3 = this.z;
            int round = Math.round((((float) getWidth()) * 1.3f) / f3);
            int round2 = Math.round((((float) getHeight()) * 1.3f) / f3);
            Pair pair = new Pair(Integer.valueOf(round), Integer.valueOf(round2));
            if (this.i.containsKey(pair)) {
                Pair pair2 = this.i.get(pair);
                bitmap = (Bitmap) pair2.first;
                bitmap2 = (Bitmap) pair2.second;
            } else {
                bitmap = Bitmap.createBitmap(round, round2, Bitmap.Config.ARGB_8888);
                Bitmap createBitmap = Bitmap.createBitmap(round, round2, Bitmap.Config.ARGB_8888);
                this.i.put(pair, new Pair(bitmap, createBitmap));
                bitmap2 = createBitmap;
            }
            if (this.j != bitmap) {
                bitmap2 = bitmap;
            }
            this.j = bitmap2;
            Canvas canvas3 = new Canvas(this.j);
            float round3 = (float) Math.round(((float) Math.max(round, round2)) * 1.3f);
            float height = round3 / ((float) bitmap5.getHeight());
            float f4 = (float) round;
            float f5 = (-(round3 - f4)) / 2.0f;
            float f6 = (float) round2;
            float f7 = (-(round3 - f6)) / 2.0f;
            float floatValue = this.t.isStarted() ? ((Float) this.t.getAnimatedValue()).floatValue() : 0.0f;
            Matrix matrix = new Matrix();
            matrix.setScale(height, height);
            float f8 = round3 / 2.0f;
            matrix.postRotate(floatValue, f8, f8);
            matrix.postTranslate(f5, f7);
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(f2);
            ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
            Paint paint = new Paint(7);
            paint.setColorFilter(colorMatrixColorFilter);
            canvas3.drawBitmap(bitmap5, matrix, paint);
            float floatValue2 = this.u.isStarted() ? ((Float) this.u.getAnimatedValue()).floatValue() : 0.0f;
            Matrix matrix2 = new Matrix();
            matrix2.setScale(height, height);
            matrix2.postRotate(floatValue2, f8, f8);
            matrix2.postTranslate(f5, f7);
            matrix2.postTranslate(-0.95f * f4, -0.7f * f6);
            canvas3.drawBitmap(bitmap5, matrix2, paint);
            float floatValue3 = this.v.isStarted() ? ((Float) this.v.getAnimatedValue()).floatValue() : 0.0f;
            Matrix matrix3 = new Matrix();
            matrix3.setScale(height, height);
            matrix3.postRotate(floatValue3, f8, f8);
            matrix3.postTranslate(f5, f7);
            matrix3.postTranslate(-0.5f * f4, 0.7f * f6);
            matrix3.postRotate(floatValue3, f4 / 2.0f, f6 / 2.0f);
            canvas3.drawBitmap(bitmap5, matrix3, paint);
            Pair pair3 = new Pair(this.j, canvas3);
            if (this.w) {
                Bitmap bitmap6 = (Bitmap) pair3.first;
                Canvas canvas4 = (Canvas) pair3.second;
                float[] fArr = this.q;
                int width = bitmap6.getWidth();
                int height2 = bitmap6.getHeight();
                float[] fArr2 = new float[72];
                int i2 = 0;
                while (true) {
                    int i3 = 5;
                    if (i2 > 5) {
                        break;
                    }
                    int i4 = 0;
                    while (i4 <= i3) {
                        int i5 = (i4 * 2) + (i2 * 12);
                        int i6 = i5 + 1;
                        fArr2[i5] = fArr[i5] * ((float) width);
                        fArr2[i6] = fArr[i6] * ((float) height2);
//                        i5 + ":   mesh: " + fArr[i5] + "   vertex: " + fArr2[i5];
//                        i6 + ":   mesh: " + fArr[i6] + "   vertex: " + fArr2[i6];
                        i4++;
                        i3 = 5;
                        width = width;
                    }
                    int i7 = width;
                    i2++;
                }
                canvas4.drawBitmapMesh(bitmap6, 5, 5, fArr2, 0, (int[]) null, 0, (Paint) null);
                pair3 = new Pair(bitmap6, canvas4);
            }
            if (this.bitmapG != F) {
                Pair<Bitmap, Canvas> a = a((Bitmap) pair3.first, (Canvas) pair3.second, aa.a(getContext(), R.color.dso_color_lyrics_back), aa.a(getContext(), R.color.white_alpha_10));
                getContext();
                bitmap3 = (Bitmap) a.first;
                // n0.a(bitmap3, 25);
            } else {
                bitmap3 = (Bitmap) pair3.first;
            }
            float width2 = (float) bitmap3.getWidth();
            float height3 = (float) bitmap3.getHeight();
            Matrix matrix4 = new Matrix(this.o);
            matrix4.preTranslate((-(width2 - (width2 / 1.3f))) / 2.0f, (-(height3 - (height3 / 1.3f))) / 2.0f);
            Shader.TileMode tileMode = Shader.TileMode.MIRROR;
            BitmapShader bitmapShader = new BitmapShader(bitmap3, tileMode, tileMode);
            bitmapShader.setLocalMatrix(matrix4);
            this.k = bitmapShader;
            if (!this.w && this.t.isStarted() && !this.t.isPaused()) {
                long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
                if (currentTimeMillis2 > 15) {
                    // g.c.b.a.a.b("create shader load overage duration (ms): ", currentTimeMillis2);
                    this.x++;
                } else {
                    if (this.x > 0) {
                        // g.c.b.a.a.b("create shader duration (ms) back under threshold: ", currentTimeMillis2);
                    }
                    this.x = 0;
                }
                if (this.x > 3) {
                    post(new LyricsBackgroundRunnable(this));
                }
            }
            this.l.setShader(this.k);
        }
        if (this.m != null) {
            canvas2.drawPath(this.p, this.n);
        }
        canvas2.drawPath(this.p, this.l);
        if (this.r.hasStarted() && !this.r.hasEnded()) {
            this.r.getTransformation(System.currentTimeMillis(), this.s);
            this.l.setAlpha((int) ((1.0f - this.s.getAlpha()) * 255.0f));
            postInvalidateDelayed(42);
        } else if (!this.t.isStarted()) {
            this.n.setAlpha(255);
            this.l.setAlpha(255);
            Bitmap bitmap7 = this.h;
            if (bitmap7 != null) {
                this.h = null;
                setArtwork(bitmap7);
            } else if (!this.w && !this.y && this.bitmapG != F) {
                this.t.end();
                this.u.end();
                this.v.end();
                this.t.start();
                this.u.start();
                this.v.start();
                invalidate();
            }
        } else if (!this.t.isPaused()) {
            postInvalidateDelayed(42);
        }
    }

    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        this.p = new Path();
        float f2 = this.f;
        this.p.addRoundRect(new RectF(0.0f, 0.0f, (float) i2, (float) i3), new float[]{f2, f2, f2, f2, 0.0f, 0.0f, 0.0f, 0.0f}, Path.Direction.CW);
        Bitmap bitmap = this.h;
        if (bitmap != null) {
            this.h = null;
            setArtwork(bitmap);
            return;
        }
        Bitmap bitmap2 = this.bitmapG;
        if (bitmap2 != null) {
            setArtwork(bitmap2);
        }
    }

    public void onVisibilityChanged(View view, int i2) {
        super.onVisibilityChanged(view, i2);
        if (i2 == 0) {
            resume();
            invalidate();
            return;
        }
        pause();
    }

    public void setArtwork(Bitmap bitmap) {
        float[] a = new float[0];
        Bitmap bitmap2 = this.bitmapG;
        if (bitmap != bitmap2 && bitmap != null && bitmap.sameAs(bitmap2)) {
            return;
        }
        if (!this.r.hasStarted() || this.r.hasEnded()) {
            this.r.cancel();
            this.r.reset();
            this.t.cancel();
            this.u.cancel();
            this.v.cancel();
            this.bitmapG = bitmap;
            this.m = this.k;
            this.n.setShader(this.m);
            this.k = null;
            Enum[] values = c0.values();
            int ordinal = values[0].ordinal();
            int nextInt = new Random().nextInt((values[values.length - 1].ordinal() - ordinal) + 1) + ordinal;
            // g.c.b.a.a.b("random Mesh enum ordinal: ", nextInt);
            int length = values.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    // g.c.b.a.a.b("No Mesh enum found for ordinal: ", nextInt);
                    a = c0.M1.a();
                    break;
                }
                Enum enumR = (c0) values[i2];
                if (nextInt == enumR.ordinal()) {
                    a = c0.M1.a();
                    // a = enumR.notify();
                    break;
                }
                i2++;
            }
            this.q = a;
            this.r.start();
            this.r.getTransformation(System.currentTimeMillis(), this.s);
            this.l.setAlpha(0);
            this.x = 0;
            this.w = false;
            invalidate();
            return;
        }
        this.h = bitmap;
    }

    public void setReducedEffects(boolean z2) {
        float f2;
        float f3;
        if (z2) {
            f2 = E;
            f3 = 3.5f;
        } else {
            f2 = D;
            f3 = 2.5f;
        }
        if (f2 != this.z || f3 != this.A) {
            pause();
            this.z = f2;
            this.A = f3;
            this.o.reset();
            Matrix matrix = this.o;
            float f4 = this.z;
            matrix.setScale(f4, f4);
            setArtwork(this.bitmapG);
        }
    }

//    public void setViewLifecycleOwner(t tVar) {
//        // b2.h.observe(tVar, new a(this));
//    }

    public LyricsBackgroundLayerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LyricsBackgroundLayerView(Context context, AttributeSet attributeSet, int i2) {
        this(context, attributeSet, i2, 0);
    }

    public LyricsBackgroundLayerView(Context context, AttributeSet attributeSet, int i2, int i3) {
        super(context, attributeSet, i2, i3);
        this.f = TopLevelFuncationKt.dp2px(16); // b2.f(getContext()) ? 0.0f : getContext().getResources().getDimension(0x7f070229);
        this.i = new HashMap();
        this.w = false;
        this.x = 0;
        this.z = D;
        this.A = 2.5f;
        this.o = new Matrix();
        Matrix matrix = this.o;
        float f2 = D;
        matrix.setScale(f2, f2);
        this.l = new Paint(7);
        this.n = new Paint(7);
        this.l.setAlpha(0);
        this.n.setAlpha(255);
        this.s = new Transformation();
        this.r = new AlphaAnimation(1.0f, 0.0f);
        this.r.setDuration(1000);
        this.r.setInterpolator(new PathInterpolator(0.0f, 0.0f, 0.3f, 1.0f));
        this.r.setAnimationListener(new LyricsBackgroundAnimationListener(this));
        this.t = ValueAnimator.ofFloat(0.0f, -360.0f);
        this.t.setDuration(120000);
        this.t.setInterpolator(new LinearInterpolator());
        this.t.setRepeatCount(-1);
        this.u = ValueAnimator.ofFloat(0.0f, 360.0f);
        this.u.setDuration(90000);
        this.u.setInterpolator(new LinearInterpolator());
        this.u.setRepeatCount(-1);
        this.v = ValueAnimator.ofFloat(0.0f, 360.0f);
        this.v.setDuration(70000);
        this.v.setInterpolator(new LinearInterpolator());
        this.v.setRepeatCount(-1);
    }

    public static Pair<Bitmap, Canvas> a(Bitmap bitmap, Canvas canvas, int... iArr) {
        for (int color : iArr) {
            Paint paint = new Paint(7);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawPaint(paint);
        }
        return new Pair<>(bitmap, canvas);
    }

    public boolean a() {
        return this.bitmapG == F;
    }

    public static List<Bitmap> a(Bitmap bitmap, int i2, int i3, long j2, int i4, Context context) {
        int i5 = i2;
        int i6 = i3;
        Context context2 = context;
        ArrayList arrayList = new ArrayList();
        float f2 = 1000.0f / ((float) i4);
        float f3 = 2.0f;
        int round = Math.round(TopLevelFuncationKt.dp2px(60) / 2.0f);; // Math.round(context.getResources().getDimension(0x7f07022c) / 2.0f);
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, round, round, true);
        StringBuilder b = new StringBuilder(); //g.c.b.a.a.b("createScaledBitmap: result bitmap ");
        b.append(createScaledBitmap.getWidth());
        b.append("/ ");
        b.append(createScaledBitmap.getHeight());
        b.toString();
        long j3 = 0;
        while (j3 < j2) {
            StringBuilder b2 = new StringBuilder(); // g.c.b.a.a.b("createStaticBackground: thread = ");
            b2.append(Thread.currentThread().getName());
            b2.toString();
            float f4 = D;
            int round2 = Math.round((((float) i5) * 1.3f) / f4);
            int round3 = Math.round((((float) i6) * 1.3f) / f4);
            Bitmap createBitmap = Bitmap.createBitmap(round2, round3, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            float round4 = (float) Math.round(((float) Math.max(round2, round3)) * 1.3f);
            float height = round4 / ((float) createScaledBitmap.getHeight());
            float f5 = (float) round2;
            float f6 = (-(round4 - f5)) / f3;
            float f7 = (float) round3;
            float f8 = f2;
            float f9 = (-(round4 - f7)) / f3;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, -360.0f});
            ofFloat.setDuration(120000);
            ofFloat.setInterpolator(new LinearInterpolator());
            ofFloat.setRepeatCount(-1);
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 360.0f});
            ofFloat2.setDuration(90000);
            ofFloat2.setInterpolator(new LinearInterpolator());
            ofFloat2.setRepeatCount(-1);
            ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[]{0.0f, 360.0f});
            float f10 = f7;
            float f11 = f5;
            ofFloat3.setDuration(70000);
            ofFloat3.setInterpolator(new LinearInterpolator());
            ofFloat3.setRepeatCount(-1);
            ofFloat.setCurrentPlayTime(j3);
            float floatValue = ((Float) ofFloat.getAnimatedValue()).floatValue();
            Matrix matrix = new Matrix();
            matrix.setScale(height, height);
            float f12 = round4 / 2.0f;
            matrix.postRotate(floatValue, f12, f12);
            matrix.postTranslate(f6, f9);
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(2.5f);
            ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
            Paint paint = new Paint(7);
            paint.setColorFilter(colorMatrixColorFilter);
            canvas.drawBitmap(createScaledBitmap, matrix, paint);
            ofFloat2.setCurrentPlayTime(j3);
            float floatValue2 = ((Float) ofFloat2.getAnimatedValue()).floatValue();
            Matrix matrix2 = new Matrix();
            matrix2.setScale(height, height);
            matrix2.postRotate(floatValue2, f12, f12);
            matrix2.postTranslate(f6, f9);
            matrix2.postTranslate(-0.95f * f11, -0.7f * f10);
            canvas.drawBitmap(createScaledBitmap, matrix2, paint);
            ofFloat3.setCurrentPlayTime(j3);
            float floatValue3 = ((Float) ofFloat3.getAnimatedValue()).floatValue();
            Matrix matrix3 = new Matrix();
            matrix3.setScale(height, height);
            matrix3.postRotate(floatValue3, f12, f12);
            matrix3.postTranslate(f6, f9);
            matrix3.postTranslate(-0.5f * f11, f10 * 0.7f);
            matrix3.postRotate(floatValue3, f11 / 2.0f, f10 / 2.0f);
            canvas.drawBitmap(createScaledBitmap, matrix3, paint);
            Pair pair = new Pair(createBitmap, canvas);
            Context context3 = context;
            Bitmap bitmap2 = (Bitmap) a((Bitmap) pair.first, (Canvas) pair.second, aa.a(context3, 0x7f0600b8), aa.a(context3, 0x7f0600b9)).first;
            // n0.a(bitmap2, 25);
            float width = (float) bitmap2.getWidth();
            float height2 = (float) bitmap2.getHeight();
            Matrix matrix4 = new Matrix();
            float f13 = D;
            matrix4.setScale(f13, f13);
            Matrix matrix5 = new Matrix(matrix4);
            matrix5.preTranslate((-(width - (width / 1.3f))) / 2.0f, (-(height2 - (height2 / 1.3f))) / 2.0f);
            int i7 = i2;
            int i8 = i3;
            Bitmap createBitmap2 = Bitmap.createBitmap(i7, i8, Bitmap.Config.ARGB_8888);
            new Canvas(createBitmap2).drawBitmap(bitmap2, matrix5, new Paint(7));
            arrayList.add(createBitmap2);
            j3 += (long) Math.round(f8);
            if (Build.VERSION.SDK_INT >= 26) {
                long nativeHeapSize = Debug.getNativeHeapSize();
                float nativeHeapFreeSize = ((float) ((nativeHeapSize - Debug.getNativeHeapFreeSize()) * 100)) / ((float) nativeHeapSize);
                // "at time: " + j3 + "    Mem % used: " + nativeHeapFreeSize + "%";
                if (nativeHeapFreeSize > 99.0f) {
                    break;
                }
            } else {
                long freeMemory = ((Runtime.getRuntime().freeMemory() + (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory())) / 1024) / 1024;
                StringBuilder a = new StringBuilder(); // g.c.b.a.a.a("at time: ", j3, "    Mem available: ");
                a.append(freeMemory);
                a.append(" MB");
                a.toString();
                if (freeMemory <= 10) {
                    break;
                }
            }
            f3 = 2.0f;
            i5 = i7;
            i6 = i8;
            Context context4 = context3;
            f2 = f8;
        }
        return arrayList;
    }
}

