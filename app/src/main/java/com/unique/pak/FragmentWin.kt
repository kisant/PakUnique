package com.unique.pak

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class FragmentWin : Fragment(R.layout.fragment_win) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun create() = FragmentWin()
    }
}