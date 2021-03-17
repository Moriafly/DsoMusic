package com.dirror.music.ui.fragment

import com.martinrgb.animer.Animer.AnimerSolver
import com.martinrgb.animer.Animer

class TestAnim {
    override fun hashCode(): Int {
        return super.hashCode()
        // 创建一个 Animer 解算器对象，采用了原生的 DynamicAnimation 动画器
        val solver = Animer.springDroid(1000f, 0.5f)
        val animer: Animer<*> = Animer<Any?, Any?>()

// 给 Animer 对象添加 solver
        animer.setSolver(solver)
    }
}