package com.unique.pak

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlinx.coroutines.delay


class FragmentMenu : Fragment(R.layout.fragment_main_screen), Animator.AnimatorListener {
    private lateinit var play: ImageView
    private lateinit var rules: ImageView
    private lateinit var setting: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(parent = view)
        translateAnimation(play)
        translateAnimation(rules)
        translateAnimation(setting)
    }

    private fun findViews(parent: View) {
        rules = parent.findViewById(R.id.iv_rules)
        rules.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.persistent_container, FragmentRules.create())
                ?.commit()
        }
        setting = parent.findViewById(R.id.iv_setting)
        setting.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.persistent_container, FragmentSetting.create())
                ?.commit()
        }
        play = parent.findViewById(R.id.iv_play)
        play.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.persistent_container, FragmentPlay.create())
                ?.commit()
        }
    }

    fun translateAnimation(view: View?) {
        val translator = AnimatorInflater.loadAnimator(requireContext(), R.animator.translator)
        translator.interpolator = FastOutSlowInInterpolator()
        translator.setTarget(view)
        translator.addListener(this)
        translator.start()
    }

    fun scaleAnimation(view: View?) {
        val scale = AnimatorInflater.loadAnimator(requireContext(), R.animator.scale)
        scale.interpolator = AccelerateDecelerateInterpolator()
        scale.setTarget(view)
        scale.addListener(this)
        scale.start()
    }

    override fun onAnimationStart(animator: Animator?) {}

    override fun onAnimationEnd(animator: Animator?) {}

    override fun onAnimationCancel(animator: Animator?) {}

    override fun onAnimationRepeat(animator: Animator?) {}

    companion object {
        fun create() = FragmentMenu()
    }
}