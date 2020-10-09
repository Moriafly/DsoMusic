//package com.dirror.music.widget;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.ValueAnimator;
//import android.animation.ValueAnimator.AnimatorUpdateListener;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.res.Resources;
//import android.content.res.TypedArray;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Paint.Align;
//import android.graphics.Paint.Style;
//import android.graphics.Path;
//import android.graphics.PorterDuff.Mode;
//import android.graphics.PorterDuffColorFilter;
//import android.graphics.Rect;
//import android.graphics.Typeface;
//import android.os.Build.VERSION;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.text.StaticLayout;
//import android.text.StaticLayout.Builder;
//import android.text.TextPaint;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.MotionEvent;
//import android.view.VelocityTracker;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.view.animation.DecelerateInterpolator;
//import android.view.animation.OvershootInterpolator;
//// import com.banqu.music.api.lyric.LyricInfo;
//// import com.banqu.music.api.lyric.LyricInfo.LineInfo;
//// import com.banqu.music.f;
//// import com.banqu.music.m.b;
//import com.dirror.music.widget.lyric.LyricInfo;
//import com.dirror.music.widget.lyric.LyricInfo.*;
//// import com.banqu.music.ui.widget.lyric.-$.Lambda.LyricView.ULN337dPP45J3DKJLzbOeXoK53E;
//// import com.banqu.music.utils.ALog;
//// import com.banqu.music.utils.r;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//
//class LyricView extends View {
//    private int Bm = Color.parseColor("#EFEFEF");
//    private int DA;
//    private int DC = 0;
//    private LyricInfo DG;
//    private String DH = f.H(2131886627);
//    private boolean Vv = false;
//    private int YX;
//    private int YY;
//    private Typeface YZ;
//    private float ZA;
//    private float ZB;
//    private List<Float> ZC = new ArrayList();
//    private a ZD;
//    private boolean ZE;
//    Runnable ZF = new Runnable() {
//        @Override
//        public void run() {
//            LyricView.this.ZE = true;
//            // ALog.d("LyricView", new Object[]{"onLongPress"});
//            if (LyricView.this.ZD != null) {
//                LyricView.this.ZD.onLongPress();
//            }
//        }
//    };
//    @SuppressLint({"HandlerLeak"})
//    Handler ZG = new Handler() {
//        @Override
//        public void handleMessage(@NotNull Message message) {
//            super.handleMessage(message);
//            switch (message.what) {
//                case 343:
//                    LyricView.this.ZG.sendEmptyMessageDelayed(344, 1200);
//                    LyricView.this.f(false, true);
//                    LyricView.this.nj();
//                    break;
//                case 344:
//                    break;
//                default:
//                    return;
//            }
//            LyricView.this.a(LyricView.this.cb(LyricView.this.DC), 640, false);
//            LyricView.this.nj();
//        }
//    };
//    Runnable ZH = new Runnable() {
//        @Override
//        public void run() {
//            Log.d("ggg", "hideIndicator");
//            LyricView.this.f(false, true);
//            LyricView.this.nj();
//        }
//    };
//    private float Za = 1.2f;
//    private float Zb;
//    private int Zc;
//    private Paint Zd;
//    private final int Ze = 344;
//    private final int Zf = 343;
//    private boolean Zg = false;
//    private ValueAnimator Zh;
//    private float Zi = 0.0f;
//    private float Zj = 0.0f;
//    private float Zk = 0.0f;
//    private boolean Zl;
//    private int Zm = 0;
//    private boolean Zn;
//    private float Zo;
//    private boolean Zp = false;
//    private int Zq;
//    private int Zr = 0;
//    private Rect Zs = new Rect();
//    private Rect Zt;
//    private int Zu = 1;
//    private int Zv;
//    private int Zw;
//    private float Zx = 0.0f;
//    private String Zy = "00:00";
//    private boolean Zz;
//    private Bitmap bitmap;
//    private int mDefaultColor;
//    private float mDownX;
//    private float mDownY;
//    private int mHintColor;
//    private int mLineColor = Color.parseColor("#366b81");
//    private Paint mLinePaint;
//    private int mTextHeight;
//    private TextPaint mTextPaint;
//    private int mTextSize;
//    private float mVelocity = 0.0f;
//    private VelocityTracker mVelocityTracker;
//
//    public @interface Alignment {
//    }
//
//    public interface a {
//        void c(long j, String str);
//
//        void onLongPress();
//
//        void uE();
//    }
//
//    private void f(MotionEvent motionEvent) {
//        Log.d("ggg", "actionUp delay hideIndicator");
//        releaseVelocityTracker();
//        postDelayed(this.ZH, 3000);
//        this.ZG.sendEmptyMessageDelayed(343, 3000);
//        if (wG()) {
//            float wB = wB();
//            if (!wA() || this.Zi >= wB) {
//                wB = wC();
//                if (wA() && this.Zi > wB) {
//                    a(wB, 640, false);
//                } else if (Math.abs(this.mVelocity) > 1600.0f) {
//                    k(this.mVelocity);
//                } else if (this.Zn && i(motionEvent)) {
//                    if (this.Zr != this.DC) {
//                        this.Zn = false;
//                        if (this.ZD != null) {
//                            f(false, true);
//                            this.ZD.c(((LineInfo) this.DG.songLines.get(this.Zr)).start, ((LineInfo) this.DG.songLines.get(this.Zr)).content);
//                        }
//                    } else {
//                        removeCallbacks(this.ZH);
//                        this.ZG.removeMessages(343);
//                        this.ZH.run();
//                        this.ZG.sendEmptyMessage(343);
//                    }
//                } else if (!h(motionEvent)) {
//                    a((this.Zi + (this.ZB + (((LineInfo) this.DG.songLines.get(this.Zr)).height / 2.0f))) - (((float) getHeight()) / 2.0f), 220, false);
//                } else if (this.Zn && !this.Zg) {
//                    int i;
//                    f(false, true);
//                    wB = getLineStartX();
//                    this.Zn = false;
//                    float f = this.ZB - (this.Zk / 2.0f);
//                    float f2 = (this.ZB + ((LineInfo) this.DG.songLines.get(this.Zr)).height) + (this.Zk / 2.0f);
//                    if (motionEvent.getY() < f) {
//                        i = this.Zr - 1;
//                        while (i >= 0) {
//                            f -= ((LineInfo) this.DG.songLines.get(i)).height + this.Zk;
//                            if (motionEvent.getY() >= f) {
//                                break;
//                            }
//                            i--;
//                        }
//                        i = -1;
//                        if (i == -1) {
//                            removeCallbacks(this.ZH);
//                            this.ZG.removeMessages(343);
//                            this.ZH.run();
//                            this.ZG.sendEmptyMessage(343);
//                            return;
//                        }
//                    } else if (motionEvent.getY() > f2) {
//                        i = this.Zr + 1;
//                        while (i < this.DA) {
//                            f2 = (f2 + ((LineInfo) this.DG.songLines.get(i)).height) + this.Zk;
//                            if (motionEvent.getY() <= f2) {
//                                break;
//                            }
//                            i++;
//                        }
//                        i = -1;
//                        if (i == -1) {
//                            removeCallbacks(this.ZH);
//                            this.ZG.removeMessages(343);
//                            this.ZH.run();
//                            this.ZG.sendEmptyMessage(343);
//                            return;
//                        }
//                    } else if (this.Zr == this.DC) {
//                        removeCallbacks(this.ZH);
//                        this.ZG.removeMessages(343);
//                        this.ZH.run();
//                        this.ZG.sendEmptyMessage(343);
//                        return;
//                    } else {
//                        f = (float) cp(((LineInfo) this.DG.songLines.get(this.Zr)).content);
//                        if (motionEvent.getX() >= wB) {
//                            if (motionEvent.getX() <= wB + f) {
//                                i = this.Zr;
//                            }
//                        }
//                        removeCallbacks(this.ZH);
//                        this.ZG.removeMessages(343);
//                        this.ZH.run();
//                        this.ZG.sendEmptyMessage(343);
//                        return;
//                    }
//                    j(i, true);
//                    this.ZD.c(((LineInfo) this.DG.songLines.get(i)).start, ((LineInfo) this.DG.songLines.get(i)).content);
//                }
//            } else {
//                a(wB, 640, false);
//            }
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        if (wG()) {
//            switch (this.YY) {
//                case 0:
//                    this.mTextPaint.setTextAlign(Align.LEFT);
//                    break;
//                case 1:
//                    this.mTextPaint.setTextAlign(Align.CENTER);
//                    break;
//                case 2:
//                    this.mTextPaint.setTextAlign(Align.RIGHT);
//                    break;
//                default:
//                    break;
//            }
//            if (this.Zn) {
//                g(canvas);
//            }
//            for (int i = 0; i < this.DA; i++) {
//                float lineStartX = getLineStartX();
//                float b = b(i, this.Zi);
//                if (ca(i) + b >= 0.0f) {
//                    if (b <= ((float) getHeight())) {
//                        float f;
//                        StaticLayout build;
//                        if (i == this.DC) {
//                            f = this.Zn ? 1.0f : this.Za;
//                            this.mTextPaint.setColor(this.YX);
//                            this.ZA = b;
//                        } else if (a(i, b) && this.Zn) {
//                            this.ZB = b;
//                            this.Zr = i;
//                            this.mTextPaint.setColor(this.YX);
//                            f = 1.0f;
//                        } else {
//                            if (i == this.Zm) {
//                                f = this.Zb;
//                            } else if (i == this.DC) {
//                                f = this.Za;
//                            } else {
//                                this.mTextPaint.setTextSize((float) this.mTextSize);
//                                f = 1.0f;
//                            }
//                            if (this.Zl) {
//                                int alpha = Color.alpha(this.mDefaultColor) - (Math.min(Math.abs(i - (this.DC - 1)), this.Zn ? Math.abs(i - (this.Zr - 1)) : Integer.MAX_VALUE) * 18);
//                                if (alpha < 40) {
//                                    alpha = 40;
//                                }
//                                this.mTextPaint.setColor(Color.argb(alpha, Color.red(this.mDefaultColor), Color.green(this.mDefaultColor), Color.blue(this.mDefaultColor)));
//                            } else {
//                                this.mTextPaint.setColor(this.mDefaultColor);
//                            }
//                        }
//                        LineInfo lineInfo = (LineInfo) this.DG.songLines.get(i);
//                        if (VERSION.SDK_INT >= 23) {
//                            build = Builder.obtain(lineInfo.content, 0, lineInfo.content.length(), this.mTextPaint, this.Zc).setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL).setLineSpacing(0.0f, 1.0f).setIncludePad(false).build();
//                        } else {
//                            StaticLayout staticLayout = new StaticLayout(lineInfo.content, this.mTextPaint, this.Zc, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//                        }
//                        canvas.save();
//                        canvas.scale(f, f);
//                        canvas.translate(lineStartX / f, b / f);
//                        build.draw(canvas);
//                        canvas.restore();
//                    } else {
//                        return;
//                    }
//                }
//            }
//            return;
//        }
//        this.mTextPaint.setTextAlign(Align.CENTER);
//        this.mTextPaint.setColor(this.mHintColor);
//        canvas.drawText(this.DH, (float) (getWidth() / 2), (float) (getHeight() / 2), this.mTextPaint);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent motionEvent) {
//        if (!this.Zz) {
//            return false;
//        }
//        if (this.mVelocityTracker == null) {
//            this.mVelocityTracker = VelocityTracker.obtain();
//        }
//        this.mVelocityTracker.addMovement(motionEvent);
//        switch (motionEvent.getAction()) {
//            case 0:
//                d(motionEvent);
//                return super.onTouchEvent(motionEvent);
//            case 1:
//                if (!this.Vv) {
//                    if (!this.Zn) {
//                        g(motionEvent);
//                        return true;
//                    }
//                }
//                this.Vv = false;
//                f(motionEvent);
//                return true;
//            case 2:
//                float y = motionEvent.getY() - this.mDownY;
//                float x = motionEvent.getX() - this.mDownX;
//                if (!this.Vv) {
//                    float scaledTouchSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
//                    if (Math.abs(x) < scaledTouchSlop && Math.abs(y) < scaledTouchSlop) {
//                        return super.onTouchEvent(motionEvent);
//                    }
//                }
//                if (!this.Vv) {
//                    removeCallbacks(this.ZF);
//                    this.Vv = true;
//                    if (Math.abs(motionEvent.getX() - this.mDownX) < Math.abs(motionEvent.getY() - this.mDownY)) {
//                        f(true, true);
//                        r.e("MotionEvent", "down");
//                    } else {
//                        f(true, false);
//                        r.e("MotionEvent", "lefttoright");
//                    }
//                }
//                e(motionEvent);
//                nj();
//                return true;
//            case 3:
//                this.ZE = true;
//                removeCallbacks(this.ZF);
//                this.Vv = false;
//                c(motionEvent);
//                break;
//            default:
//                break;
//        }
//        return super.onTouchEvent(motionEvent);
//    }
//
//    private void g(MotionEvent motionEvent) {
//        int i = this.ZE ^ 1;
//        this.ZE = true;
//        removeCallbacks(this.ZF);
//        f(false, true);
//        if (!(this.DG == null || this.ZD == null || this.Zg)) {
//            if (!this.Vv) {
//                int i2;
//                float lineStartX = getLineStartX();
//                float f = this.ZA - (this.Zk / 2.0f);
//                float f2 = (this.ZA + ((LineInfo) this.DG.songLines.get(this.DC)).height) + (this.Zk / 2.0f);
//                if (motionEvent.getY() < f) {
//                    i2 = this.DC - 1;
//                    while (i2 >= 0) {
//                        f -= ((LineInfo) this.DG.songLines.get(i2)).height + this.Zk;
//                        if (motionEvent.getY() >= f) {
//                            break;
//                        }
//                        i2--;
//                    }
//                    i2 = -1;
//                    if (i2 == -1) {
//                        if (i != 0) {
//                            this.ZD.uE();
//                        }
//                        return;
//                    }
//                } else if (motionEvent.getY() > f2) {
//                    for (int i3 = this.DC + 1; i3 < this.DA; i3++) {
//                        f2 = (f2 + ((LineInfo) this.DG.songLines.get(i3)).height) + this.Zk;
//                        if (motionEvent.getY() <= f2) {
//                            i2 = i3;
//                            break;
//                        }
//                    }
//                    i2 = -1;
//                    if (i2 == -1) {
//                        if (i != 0) {
//                            this.ZD.uE();
//                        }
//                        return;
//                    }
//                } else {
//                    float cp = (float) cp(((LineInfo) this.DG.songLines.get(this.DC)).content);
//                    if ((motionEvent.getX() < lineStartX || motionEvent.getX() > lineStartX + cp) && i != 0) {
//                        this.ZD.uE();
//                    }
//                    return;
//                }
//                f = (float) cp(((LineInfo) this.DG.songLines.get(i2)).content);
//                if (motionEvent.getX() >= lineStartX) {
//                    if (motionEvent.getX() <= lineStartX + f) {
//                        if (i != 0) {
//                            j(i2, true);
//                            this.ZD.c(((LineInfo) this.DG.songLines.get(i2)).start, ((LineInfo) this.DG.songLines.get(i2)).content);
//                        }
//                        return;
//                    }
//                }
//                if (i != 0) {
//                    this.ZD.uE();
//                }
//            }
//        }
//    }
//
//    public void setLyricInfo(LyricInfo lyricInfo) {
//        this.DG = lyricInfo;
//        if (this.DG == null || this.DG.getSongLines() == null || this.DG.getSongLines().size() <= 0) {
//            this.DH = getContext().getString(2131886627);
//        } else if (this.DG.getSongLines().size() == 1) {
//            this.DH = ((LineInfo) this.DG.getSongLines().get(0)).content;
//            this.DG = null;
//        } else {
//            this.DA = this.DG.getSongLines().size();
//            float f = 0.0f;
//            for (int i = 0; i < this.DA; i++) {
//                StaticLayout build;
//                LineInfo lineInfo = (LineInfo) this.DG.songLines.get(i);
//                if (VERSION.SDK_INT >= 23) {
//                    build = Builder.obtain(lineInfo.content, 0, lineInfo.content.length(), this.mTextPaint, this.Zc).setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL).setLineSpacing(0.0f, 1.0f).setIncludePad(false).build();
//                } else {
//                    StaticLayout staticLayout = new StaticLayout(((LineInfo) this.DG.songLines.get(i)).content, this.mTextPaint, this.Zc, android.text.Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//                }
//                float height = (float) build.getHeight();
//                float width = (float) build.getWidth();
//                f += height;
//                this.ZC.add(Float.valueOf(f));
//                lineInfo.line = build.getLineCount();
//                lineInfo.height = height;
//                lineInfo.originHeight = height;
//                lineInfo.width = width;
//                if (i <= this.Zu) {
//                    lineInfo.scrollY = 0.0f;
//                } else {
//                    int i2 = i - 1;
//                    lineInfo.scrollY = ((LineInfo) this.DG.songLines.get(i2)).scrollY + t(i, i2);
//                }
//            }
//            if (this.DA - this.Zw < 0) {
//                this.Zv = this.DA - 1;
//            } else {
//                this.Zv = (this.DA - 1) - this.Zw;
//            }
//            if (this.Zv < 0 || this.Zv > this.DA - 1) {
//                this.Zv = this.DA - 1;
//            }
//            this.Zx = t(this.Zv, this.Zu);
//        }
//        nj();
//    }
//
//    public LyricView(Context context, AttributeSet attributeSet) {
//        super(context);
//        getAttrs(context, attributeSet);
//        as(context);
//    }
//
//    public LyricView(Context context, AttributeSet attributeSet, int i) {
//        super(context);
//        getAttrs(context, attributeSet);
//        as(context);
//    }
//
//    public LyricView(Context context) {
//        super(context);
//        as(context);
//    }
//
//    private void getAttrs(Context context, AttributeSet attributeSet) {
//        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, b.LyricView);
//        this.Zl = obtainStyledAttributes.getBoolean(1, false);
//        this.Zu = obtainStyledAttributes.getInteger(3, 1);
//        this.Zw = obtainStyledAttributes.getInteger(2, 0);
//        this.DH = obtainStyledAttributes.getString(5) != null ? obtainStyledAttributes.getString(5) : getResources().getString(2131886627);
//        this.mHintColor = obtainStyledAttributes.getColor(6, Color.parseColor("#80000000"));
//        this.mDefaultColor = obtainStyledAttributes.getColor(10, Color.parseColor("#80000000"));
//        this.YX = obtainStyledAttributes.getColor(4, Color.parseColor("#000000"));
//        this.Zz = obtainStyledAttributes.getBoolean(0, true);
//        this.mTextSize = obtainStyledAttributes.getDimensionPixelSize(11, (int) c(2, 15.0f));
//        this.YZ = obtainStyledAttributes.getString(12) != null ? Typeface.create(obtainStyledAttributes.getString(12), 0) : Typeface.DEFAULT;
//        this.YY = obtainStyledAttributes.getInt(9, 1);
//        this.Zc = obtainStyledAttributes.getDimensionPixelSize(8, (int) c(1, 240.0f));
//        this.Zk = (float) obtainStyledAttributes.getDimensionPixelSize(7, (int) c(1, 22.0f));
//        this.bitmap = BitmapFactory.decodeResource(getResources(), 2131230894);
//        obtainStyledAttributes.recycle();
//    }
//
//    private void initPaint() {
//        this.mTextPaint = new TextPaint();
//        this.mTextPaint.setDither(true);
//        this.mTextPaint.setAntiAlias(true);
//        this.mTextPaint.setTypeface(this.YZ);
//        this.mLinePaint = new Paint();
//        this.mLinePaint.setDither(true);
//        this.mLinePaint.setAntiAlias(true);
//        this.mLinePaint.setColor(this.mLineColor);
//        this.mLinePaint.setAlpha(64);
//        this.mLinePaint.setStrokeWidth(1.0f);
//        this.mLinePaint.setStyle(Style.STROKE);
//        this.Zd = new Paint();
//        this.Zd.setDither(true);
//        this.Zd.setAntiAlias(true);
//        this.Zd.setColor(-1);
//        this.Zd.setTextAlign(Align.CENTER);
//        this.Zd.setTextSize(c(1, 8.0f));
//    }
//
//    private void c(MotionEvent motionEvent) {
//        this.ZG.sendEmptyMessage(343);
//        this.Zn = false;
//        releaseVelocityTracker();
//        f(false, true);
//        if (wG()) {
//            float wB = wB();
//            if (!wA() || this.Zi >= wB) {
//                wB = wC();
//                if (wA() && this.Zi > wB) {
//                    a(wB, 640, false);
//                } else if (Math.abs(this.mVelocity) > 1600.0f) {
//                    k(this.mVelocity);
//                }
//            } else {
//                a(wB(), 640, false);
//            }
//        }
//    }
//
//    private void d(MotionEvent motionEvent) {
//        removeCallbacks(this.ZH);
//        this.ZG.removeMessages(343);
//        this.ZG.removeMessages(344);
//        this.Zo = this.Zi;
//        this.mDownX = motionEvent.getX();
//        this.mDownY = motionEvent.getY();
//        if (this.Zh != null) {
//            this.Zh.cancel();
//            this.Zh = null;
//        }
//        if (!this.Zn) {
//            this.ZE = false;
//            postDelayed(this.ZF, (long) ViewConfiguration.getLongPressTimeout());
//        }
//    }
//
//    private void g(Canvas canvas) {
//        Path path = new Path();
//        this.Zs = new Rect(getMeasuredWidth() - this.bitmap.getWidth(), (int) ((((float) getMeasuredHeight()) * 0.5f) - (((float) this.bitmap.getHeight()) * 0.5f)), getMeasuredWidth(), (int) ((((float) getMeasuredHeight()) * 0.5f) + (((float) this.bitmap.getHeight()) * 0.5f)));
//        float sqrt = ((float) this.Zs.right) - (((float) this.Zs.left) + ((float) Math.sqrt(Math.pow((double) this.Zs.width(), 2.0d) - Math.pow((double) (((float) this.Zs.width()) * 0.5f), 2.0d))));
//        canvas.drawBitmap(this.bitmap, (float) this.Zs.left, (float) this.Zs.top, null);
//        canvas.drawText(wD(), ((float) this.Zs.left) + (((float) this.bitmap.getWidth()) * 0.6f), (((float) this.Zs.top) + (((float) this.bitmap.getHeight()) * 0.5f)) + (this.Zd.getTextSize() * 0.3f), this.Zd);
//        path = new Path();
//        path.moveTo((((float) this.Zs.left) - c(1, 10.0f)) - sqrt, ((float) getMeasuredHeight()) * 0.5f);
//        path.lineTo((((float) this.Zt.width()) + c(1, 2.0f)) + c(1, 10.0f), ((float) getHeight()) * 0.5f);
//        canvas.drawPath(path, this.mLinePaint);
//    }
//
//    private void resetView() {
//        this.DC = 0;
//        this.Zm = 0;
//        this.Zn = false;
//        this.DG = null;
//        nj();
//        this.DA = 0;
//        this.Zi = 0.0f;
//        this.ZC.clear();
//        this.Zv = 0;
//        if (this.Zh != null) {
//            this.Zh.cancel();
//        }
//    }
//
//    private void j(int i, boolean z) {
//        if (this.DC != i) {
//            this.Zm = this.DC;
//            this.DC = i;
//            if (!this.Zg && !this.Zp) {
//                float cb = cb(i);
//                if (z) {
//                    a(cb, 640, true);
//                    return;
//                }
//                this.Za = 1.2f;
//                this.Zb = 1.0f;
//                this.Zi = cb;
//                nj();
//            }
//        }
//    }
//
//    private void k(float f) {
//        f = Math.min(Math.max(wB(), this.Zi - ((f / Math.abs(f)) * (Math.abs(f) * 0.2f))), wC());
//        this.Zh = ValueAnimator.ofFloat(new float[]{this.Zi, f});
//        this.Zh.addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                LyricView.this.Zi = ((Float) valueAnimator.getAnimatedValue()).floatValue();
//                LyricView.this.nj();
//            }
//        });
//        this.Zh.addListener(new AnimatorListenerAdapter() {
//            public void onAnimationEnd(Animator animator) {
//                super.onAnimationEnd(animator);
//                if (LyricView.this.DG != null) {
//                    LyricView.this.a((LyricView.this.Zi + (LyricView.this.ZB + (((LineInfo) LyricView.this.DG.songLines.get(LyricView.this.Zr)).height / 2.0f))) - (((float) LyricView.this.getHeight()) / 2.0f), 220, false);
//                }
//            }
//
//            public void onAnimationStart(Animator animator) {
//                super.onAnimationStart(animator);
//                LyricView.this.mVelocity = 0.0f;
//                LyricView.this.Zg = true;
//            }
//
//            public void onAnimationCancel(Animator animator) {
//                super.onAnimationCancel(animator);
//            }
//        });
//        this.Zh.setDuration(420);
//        this.Zh.setInterpolator(new DecelerateInterpolator());
//        this.Zh.start();
//    }
//
//    private String wD() {
//        DecimalFormat decimalFormat = new DecimalFormat("00");
//        StringBuilder stringBuilder;
//        if (this.DG != null && this.DA > 0 && this.Zr - 1 < this.DA && this.Zr > 0) {
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(decimalFormat.format((((LineInfo) this.DG.songLines.get(this.Zr - 1)).start / 1000) / 60));
//            stringBuilder.append(":");
//            stringBuilder.append(decimalFormat.format((((LineInfo) this.DG.songLines.get(this.Zr - 1)).start / 1000) % 60));
//            return stringBuilder.toString();
//        } else if (this.DG != null && this.DA > 0 && this.Zr - 1 >= this.DA) {
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(decimalFormat.format((((LineInfo) this.DG.songLines.get(this.DA - 1)).start / 1000) / 60));
//            stringBuilder.append(":");
//            stringBuilder.append(decimalFormat.format((((LineInfo) this.DG.songLines.get(this.DA - 1)).start / 1000) % 60));
//            return stringBuilder.toString();
//        } else if (this.DG == null || this.DA <= 0 || this.Zr - 1 > 0) {
//            return this.Zy;
//        } else {
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(decimalFormat.format((((LineInfo) this.DG.songLines.get(0)).start / 1000) / 60));
//            stringBuilder.append(":");
//            stringBuilder.append(decimalFormat.format((((LineInfo) this.DG.songLines.get(0)).start / 1000) % 60));
//            return stringBuilder.toString();
//        }
//    }
//
//    private void a(float f, long j, boolean z) {
//        float f2 = this.Zi;
//        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
//        ofFloat.addUpdateListener(new ULN337dPP45J3DKJLzbOeXoK53E(this, f2, f, z));
//        ofFloat.addListener(new AnimatorListenerAdapter() {
//            public void onAnimationEnd(Animator animator) {
//                LyricView.this.Zg = false;
//                LyricView.this.nj();
//            }
//
//            public void onAnimationStart(Animator animator) {
//                LyricView.this.Zg = true;
//            }
//        });
//        ofFloat.setDuration(j);
//        ofFloat.setInterpolator(new OvershootInterpolator(0.5f));
//        ofFloat.start();
//    }
//
//    private float wC() {
//        float t = t(this.DG.songLines.size() - 1, 0);
//        float height = ((float) getHeight()) / 2.0f;
//        float startY = getStartY();
//        LineInfo lineInfo = (LineInfo) this.DG.songLines.get(this.DG.songLines.size() - 1);
//        return this.DC == this.DG.songLines.size() + -1 ? (t - (height - startY)) + ((lineInfo.height * 1.2f) / 2.0f) : (t - (height - startY)) + (lineInfo.height / 2.0f);
//    }
//
//    private float wB() {
//        float height = ((float) getHeight()) / 2.0f;
//        float startY = getStartY();
//        LineInfo lineInfo = (LineInfo) this.DG.songLines.get(0);
//        return this.DC == 0 ? (-(height - startY)) + ((lineInfo.height * 1.2f) / 2.0f) : (-(height - startY)) + (lineInfo.height / 2.0f);
//    }
//
//    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
//        super.onLayout(z, i, i2, i3, i4);
//        this.Zs.set((int) c(1, 7.0f), (int) ((((float) getHeight()) * 0.5f) - (c(2, 15.0f) * 0.5f)), (int) (c(2, 15.0f) + c(1, 7.0f)), (int) ((((float) getHeight()) * 0.5f) + (c(2, 15.0f) * 0.5f)));
//    }
//
//    private float b(int i, float f) {
//        float startY = ((getStartY() + bY(i)) - f) + bZ(i);
//        if (i > this.DC) {
//            return startY + ((((LineInfo) this.DG.songLines.get(this.DC)).height * 0.20000005f) / 2.0f);
//        }
//        return i == this.DC ? startY - ((((LineInfo) this.DG.songLines.get(this.DC)).height * 0.20000005f) / 2.0f) : startY;
//    }
//
//    private Bitmap b(Bitmap bitmap, int i) {
//        if (bitmap == null) {
//            return null;
//        }
//        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
//        Canvas canvas = new Canvas(createBitmap);
//        Paint paint = new Paint();
//        paint.setColorFilter(new PorterDuffColorFilter(i, Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
//        return createBitmap;
//    }
//
//    private void b(long j, boolean z) {
//        int i;
//        if (wG()) {
//            i = this.DA - 1;
//            while (i > -1) {
//                LineInfo lineInfo = (LineInfo) this.DG.songLines.get(i);
//                if (lineInfo != null && lineInfo.start <= j) {
//                    break;
//                }
//                i--;
//            }
//        }
//        i = 0;
//        j(i, z);
//    }
//
//    private void e(MotionEvent motionEvent) {
//        if (wG()) {
//            VelocityTracker velocityTracker = this.mVelocityTracker;
//            velocityTracker.computeCurrentVelocity(1000, (float) this.Zq);
//            this.Zi = (this.Zo + this.mDownY) - motionEvent.getY();
//            this.mVelocity = velocityTracker.getYVelocity();
//        }
//    }
//
//    private float getLineStartX() {
//        switch (this.YY) {
//            case 0:
//                return (float) getPaddingStart();
//            case 1:
//                return ((float) getWidth()) * 0.5f;
//            case 2:
//                return (float) (((getWidth() - 20) - this.Zt.width()) - 7);
//            default:
//                return (float) (this.Zs.width() + 17);
//        }
//    }
//
//    private void setLineSpace(float f) {
//        if (this.Zk != f) {
//            this.Zk = c(1, f);
//            wF();
//            this.Zi = cb(this.DC);
//            nj();
//        }
//    }
//
//    private void setRawTextSize(float f) {
//        if (f != this.mTextPaint.getTextSize()) {
//            this.mTextPaint.setTextSize(f);
//            wF();
//            this.Zi = cb(this.DC);
//            nj();
//        }
//    }
//
//    private float t(int i, int i2) {
//        return ((((Float) this.ZC.get(i)).floatValue() - ((LineInfo) this.DG.songLines.get(i)).height) - (((Float) this.ZC.get(i2)).floatValue() - ((LineInfo) this.DG.songLines.get(i2)).height)) + (((float) (i - i2)) * this.Zk);
//    }
//
//    private void wE() {
//        setRawTextSize((float) this.mTextSize);
//        setLineSpace(this.Zk);
//        wF();
//        this.Zt = new Rect();
//        this.Zd.getTextBounds(this.Zy, 0, this.Zy.length(), this.Zt);
//    }
//
//    private float c(int i, float f) {
//        Context context = getContext();
//        return TypedValue.applyDimension(i, f, (context == null ? Resources.getSystem() : context.getResources()).getDisplayMetrics());
//    }
//
//    private float cb(int i) {
//        if (!(this.DG == null || this.DG.songLines == null)) {
//            if (this.DG.songLines.size() != 0) {
//                float f = ((LineInfo) this.DG.songLines.get(i)).scrollY;
//                if (f > this.Zx) {
//                    f = this.Zx;
//                }
//                return f;
//            }
//        }
//        return 0.0f;
//    }
//
//    private void f(boolean z, boolean z2) {
//        if (z) {
//            this.Zp = true;
//            if (z2) {
//                this.Zn = true;
//                return;
//            }
//            return;
//        }
//        this.Zp = false;
//        this.Zn = false;
//    }
//
//    private boolean i(MotionEvent motionEvent) {
//        boolean z = false;
//        if (this.Zs == null || this.mDownX <= ((float) (this.Zs.left - 7)) || this.mDownX >= ((float) (this.Zs.right + 7)) || this.mDownY <= ((float) (this.Zs.top - 7)) || this.mDownY >= ((float) (this.Zs.bottom + 7))) {
//            return false;
//        }
//        float x = motionEvent.getX();
//        float y = motionEvent.getY();
//        if (x > ((float) (this.Zs.left - 7)) && x < ((float) (this.Zs.right + 7)) && y > ((float) (this.Zs.top - 7)) && y < ((float) (this.Zs.bottom + 7))) {
//            z = true;
//        }
//        return z;
//    }
//
//    private void releaseVelocityTracker() {
//        if (this.mVelocityTracker != null) {
//            this.mVelocityTracker.clear();
//            this.mVelocityTracker.recycle();
//            this.mVelocityTracker = null;
//        }
//    }
//
//    public void setBtnColor(int i) {
//        this.Bm = i;
//        this.mLineColor = Color.argb(180, Color.red(i), Color.green(i), Color.blue(i));
//        this.mLinePaint.setColor(this.mLineColor);
//        this.bitmap = b(this.bitmap, i);
//    }
//
//    private void as(Context context) {
//        this.Zq = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
//        initPaint();
//        wE();
//    }
//
//    private int cp(String str) {
//        Rect rect = new Rect();
//        this.mTextPaint.getTextBounds(str, 0, str.length(), rect);
//        return rect.width();
//    }
//
//    private void nj() {
//        if (Looper.getMainLooper() == Looper.myLooper()) {
//            invalidate();
//        } else {
//            postInvalidate();
//        }
//    }
//
//    private void wF() {
//        Rect rect = new Rect();
//        this.mTextPaint.getTextBounds(this.DH, 0, this.DH.length(), rect);
//        this.mTextHeight = rect.height();
//    }
//
//    public String getCurrentLine() {
//        return (this.DG == null || this.DC <= 0 || this.DG.songLines == null) ? null : ((LineInfo) this.DG.songLines.get(this.DC - 1)).content;
//    }
//
//    private boolean a(int i, float f) {
//        float height = ((float) getHeight()) / 2.0f;
//        return height >= f - (this.Zk / 2.0f) && height <= (f + ((LineInfo) this.DG.songLines.get(i)).height) + (this.Zk / 2.0f);
//    }
//
//    private float ca(int i) {
//        return i == 0 ? ((Float) this.ZC.get(0)).floatValue() : ((Float) this.ZC.get(i)).floatValue() - ((Float) this.ZC.get(i - 1)).floatValue();
//    }
//
//    private boolean h(MotionEvent motionEvent) {
//        float scaledTouchSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
//        return Math.abs(motionEvent.getY() - this.mDownY) < scaledTouchSlop && Math.abs(motionEvent.getX() - this.mDownX) < scaledTouchSlop;
//    }
//
//    public void co(String str) {
//        this.DH = str;
//        resetView();
//    }
//
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        com.banqu.music.player.b.a(this);
//    }
//
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        com.banqu.music.player.b.b(this);
//    }
//
//    public void setHighLightTextColor(int i) {
//        this.YX = i;
//        nj();
//    }
//
//    public void setHint(String str) {
//        this.DH = str;
//        nj();
//    }
//
//    private float bY(int i) {
//        return ((float) i) * this.Zk;
//    }
//
//    private float bZ(int i) {
//        return i < 1 ? 0.0f : ((Float) this.ZC.get(i - 1)).floatValue();
//    }
//
//    private boolean wA() {
//        return wG() && (this.Zi > wC() || this.Zi < wB());
//    }
//492863701
//    private boolean wG() {
//        return (this.DG == null || this.DG.songLines == null || this.DG.songLines.size() <= 0) ? false : true;
//    }
//
//    public LyricInfo getCurrentLyric() {
//        return this.DG;
//    }
//
//    public void setAlignment(int i) {
//        this.YY = i;
//    }
//
//    public void setCurrentTimeMillis(long j, boolean z) {
//        b(j, z);
//    }
//
//    public void setIndicatorShow(boolean z) {
//        this.Zn = z;
//    }
//
//    public void setOnPlayerClickListener(a aVar) {
//        this.ZD = aVar;
//    }
//
//    public void setTextSize(int i) {
//        setRawTextSize((float) ((((double) i) * 0.2d) + 35.0d));
//    }
//
//    private float getStartY() {
//        return 0.0f;
//    }
//
//    @Override
//    protected float getBottomFadingEdgeStrength() {
//        return 1.0f;
//    }
//
//    @Override
//    protected float getTopFadingEdgeStrength() {
//        return 1.0f;
//    }
//}
//
