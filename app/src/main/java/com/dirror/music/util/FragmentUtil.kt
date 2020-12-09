package com.dirror.music.util

import androidx.fragment.app.Fragment
import com.dirror.music.ui.fragment.HomeFragment
import com.dirror.music.ui.fragment.MyFragment

object FragmentUtil {

    private var myFragment: MyFragment = MyFragment()
    private val homeFragment: HomeFragment = HomeFragment()

    fun getFragment(id: Int): Fragment {
        return when (id) {
            0 -> myFragment
            else -> homeFragment
        }
    }

}