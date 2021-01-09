package com.dirror.music.util

import androidx.fragment.app.Fragment
import com.dirror.music.ui.fragment.HomeFragment
import com.dirror.music.ui.fragment.MyFragment

object FragmentUtil {

    private var myFragment : MyFragment? = null
    private var homeFragment : HomeFragment? = null


    fun getFragment(id: Int): Fragment? {
        return when (id) {
            0 -> {
                if (myFragment != null) {
                    myFragment
                } else {
                    MyFragment()
                }
            }
            else -> {
                if (homeFragment != null) {
                    homeFragment
                } else {
                    HomeFragment()
                }
            }
        }
    }

}