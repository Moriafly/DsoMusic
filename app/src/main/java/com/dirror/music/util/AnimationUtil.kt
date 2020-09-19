package com.dirror.music.util

import android.animation.ObjectAnimator
import android.view.View
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

    // fun clickAlpha(view: View)
}