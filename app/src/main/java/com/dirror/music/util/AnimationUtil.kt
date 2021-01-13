package com.dirror.music.util

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener


object AnimationUtil {

    fun startRotateAlways(view: View) {
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(view, "rotation", view.rotation, view.rotation + 360f)
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 25000
        objectAnimator.repeatCount = -1
        objectAnimator.start()

//        val rotateAnim: Animation = AnimationUtils.loadAnimation(view.context, R.anim.anim_rotate_always)
//        rotateAnim.interpolator = LinearInterpolator()
//        view.startAnimation(rotateAnim)
    }

    fun stop(view: View) {
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(view, "rotation", view.rotation, view.rotation + 360f)
        objectAnimator.pause()
//        objectAnimator.interpolator = LinearInterpolator()
//        objectAnimator.duration = 25000
//        objectAnimator.repeatCount = -1

        // view.clearAnimation()
    }

    /**
     * 淡出
     * @param gone 是否消失
     */
    fun fadeOut(view: View, gone: Boolean) {
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(view, "alpha", view.alpha, 0f)
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = (300 * view.alpha / 1.0f).toLong()
        // objectAnimator.repeatCount = 1
        objectAnimator.start()
        objectAnimator.addListener({
            if (gone) {
                view.visibility = View.GONE
            }
        })
    }

    /**
     * 淡入
     */
    fun fadeIn(view: View) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        }
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(view, "alpha", view.alpha, 1.0f)
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = (300 * (1.0f - view.alpha) / 1.0f).toLong()
        // objectAnimator.repeatCount = 1
        objectAnimator.start()
    }

    /**
     * @param smooth 平滑
     */
    fun fadeIn(view: View, duration: Int, smooth: Boolean) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        }
        val startAlpha = if (smooth) {
            view.alpha
        } else {
            0f
        }
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(view, "alpha", startAlpha, 1.0f)
        objectAnimator.interpolator = LinearInterpolator()
        if (smooth) {
            objectAnimator.duration = (duration * (1.0f - view.alpha) / 1.0f).toLong()
        } else {
            objectAnimator.duration = duration.toLong()
        }
        objectAnimator.start()
    }

    fun click(view: View) {
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.5f, 1f)

        // objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 300
        // objectAnimator.repeatCount = 1
        objectAnimator.start()

        val objectAnimator2: ObjectAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.5f, 1f)

        // objectAnimator.interpolator = LinearInterpolator()
        objectAnimator2.duration = 300
        // objectAnimator.repeatCount = 1
        objectAnimator2.start()
    }


    fun moveToRight(view: View) {
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, dp2px(300f))
        objectAnimator.interpolator = DecelerateInterpolator()
        objectAnimator.duration = 250
        objectAnimator.start()
    }

    fun moveToLeft(view: View) {
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(view, "translationX", dp2px(300f), 0f)
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 250
        objectAnimator.start()
    }

}