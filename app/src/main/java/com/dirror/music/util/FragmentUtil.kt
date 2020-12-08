package com.dirror.music.util

import androidx.fragment.app.Fragment
import com.dirror.music.ui.fragment.HomeFragment
import com.dirror.music.ui.fragment.MyFragment

object FragmentUtil {

    private val myFragment: MyFragment = MyFragment()

    // private val myFragment by lazy { MyFragment() }
    private val homeFragment: HomeFragment = HomeFragment()

    init {

    }

//    private val myFragment by lazy { MyFragment() }
//    private val homeFragment by lazy { HomeFragment() }

    fun getFragment(id: Int): Fragment {
        return when (id) {
            0 -> myFragment
            else -> homeFragment
        }
    }

    fun startLoginActivity() {
        myFragment.startLoginActivity()
    }
}