package com.dirror.music.util

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import com.dirror.music.R


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
}