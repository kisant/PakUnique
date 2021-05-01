package com.unique.pak

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*

class FragmentSplash : Fragment(R.layout.fragment_splash) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scope.launch {
            startFragment()
        }

    }

    private suspend fun startFragment() {
        delay(4000)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, FragmentMenu.create())
            .commit()
    }

    override fun onDestroyView() {
        scope.cancel()
        Log.d("Splash screen", "onDestroyView.")
        super.onDestroyView()
    }

    companion object {
        fun create() = FragmentSplash()
    }
}