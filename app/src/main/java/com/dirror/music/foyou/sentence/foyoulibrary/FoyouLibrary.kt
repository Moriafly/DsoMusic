package com.dirror.music.foyou.sentence.foyoulibrary

import com.dirror.music.foyou.sentence.OptimizeHitokoto
import com.dirror.music.foyou.sentence.SentenceData

/**
 * Foyou 库
 */
object FoyouLibrary {

    const val VERSION = "4.0.3"

    fun getSentence(): SentenceData {
        val sentence = when ((1..3).random()) {
            1 -> FoyouPoetry.getFoyouPoetry()
            2 -> FoyouLiterature.getLiterature()
            3 -> FoyouOthers.getFoyouOthers()
            else -> FoyouPoetry.getFoyouPoetry()
        }
        val text = OptimizeHitokoto.sentenceTextNewLine(sentence.text)
        return SentenceData(text, sentence.author, sentence.source)
    }

}