package com.unique.pak


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent.extras?.let {
            FragmentCasino.post()
            FragmentCasino.FCM_DATA = it
            FragmentCasino.fcmData()
            for (key in it.keySet()) {
                val value = intent.extras?.get(key)
                Log.d(TAG, "Key: $key Value: $value")
            }
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.persistent_container, FragmentMenu.create())
            .add(R.id.fragments_container, FragmentCasino.create())
            .commit()
    }

    fun screenTapped(view: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.persistent_container, FragmentMenu.create())
            .commit()
    }

    companion object {
        private const val TAG = "Firebase"
        private const val LOG_PREFIX = "Token is: "
    }
}

//private fun startFragment(fragment: Fragment) {
//    supportFragmentManager.beginTransaction()
//        .replace(R.id.container, fragment)
//        .commit()
//}
//
//fun screenTapped(view: View) {
//    supportFragmentManager.beginTransaction()
//        .replace(R.id.container, FragmentMenu.create())
//        .commit()
//}
//
//companion object {
//    private const val TAG = "Firebase"
//    private const val LOG_PREFIX = "Token is: "
//}