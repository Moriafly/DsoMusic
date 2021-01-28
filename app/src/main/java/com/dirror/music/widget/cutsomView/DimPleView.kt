package com.dirror.music.widget.cutsomView

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.system.measureTimeMillis

/**
 * Project:NetEasy
 * Created by MLX on 2020/7/26.
 */
class DimPleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mWidth = 0f;
    private var mHeight = 0f

    private var particleList = mutableListOf<Particle>()

    private var animator = ValueAnimator.ofFloat(0f, 1f)
    var paint = Paint()
    var path = Path()
    private val pathMeasure = PathMeasure()//路径，用于测量扩散圆某一处的X,Y值
    private var pos = FloatArray(2) //扩散圆上某一点的x,y
    private val tan = FloatArray(2)//扩散圆上某一点切线
    private val random = Random()
    private val particleNumber = 1000//粒子数量
    private val particleRadius = 2.3f//粒子半径
    private val diffusionRadius = 268f//扩散圆半径

    init {
        animator.duration = 5000
        animator.repeatCount = -1
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            updateParticle(it.animatedValue as Float)
            invalidate()
        }
        paint.color = Color.WHITE
        paint.isAntiAlias = true
    }

    private fun updateParticle(fl: Float) {
        particleList.forEachIndexed { index, particle ->
            if (particle.offSet > particle.maxOffSet) {
                particle.offSet = 0f
                particle.speed = random.nextInt(3) + 1.5f
                particle.maxOffSet = random.nextInt(250).toFloat()
            }
            particle.x = (mWidth / 2 + cos(particle.angle) * (diffusionRadius + particle.offSet)).toFloat()+particle.offSetX*particle.direction

            if (particle.y > mHeight / 2) {
                particle.y = (sin(particle.angle) * (diffusionRadius + particle.offSet) + mHeight / 2).toFloat()
            } else {
                particle.y = (mHeight / 2 - sin(particle.angle) * (diffusionRadius + particle.offSet)).toFloat()
            }

            particle.offSet += particle.speed
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val time = measureTimeMillis {
            particleList.forEachIndexed { index, particle ->
                if (particle.offSet > 5f) {
                    paint.alpha = ((1f - particle.offSet / particle.maxOffSet) * 0.8 * 225f).toInt()
                    canvas.drawCircle(particle.x, particle.y, particle.radius, paint)
                } else {
                    paint.alpha = 225
                }
                canvas.drawCircle(particle.x, particle.y, particle.radius, paint)
            }
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        path.addCircle(mWidth / 2, mHeight / 2, diffusionRadius, Path.Direction.CCW)
        pathMeasure.setPath(path, false)
        for (i in 0 .. particleNumber) {
            pathMeasure.getPosTan(i / particleNumber.toFloat() * pathMeasure.length, pos, tan)
            val offSet = random.nextInt(200)
            val speed = random.nextInt(2) + 0.5f
            val randomX = random.nextInt(6) - 3f
            val randomY = random.nextInt(6) - 3f
            val offSetX = random.nextInt(3)
            val direction = random.nextInt(3)-1.5f
            val angel = acos(((pos[0] - mWidth / 2) / diffusionRadius).toDouble())
            val maxOffSet = random.nextInt(250) + 0f
            particleList.add(
                Particle(
                    pos[0] + randomX,
                    pos[1] + randomY,
                    particleRadius,
                    offSetX.toFloat(),
                    offSet.toFloat(),
                    direction,
                    speed,
                    angel,
                    maxOffSet
                )
            )
        }
        animator.start()
    }


}