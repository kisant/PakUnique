package com.unique.pak

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment

class FragmentSetting : Fragment(R.layout.fragment_setting){
    private lateinit var close: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(parent = view)
    }

    private fun findViews(parent: View) {
        close = parent.findViewById(R.id.iv_close_sttng)
        close.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.persistent_container,FragmentMenu.create())
                ?.commit()
        }
    }

    companion object {
        fun create() = FragmentSetting()
    }
}