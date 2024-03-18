package com.example.musicplayerbasics

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class AudioEffectViewHelper(private val context: Context, private val fragmentManager: FragmentManager, private val audioEffectManager: AudioEffectManager, ) {
    /**
     * Create an instance of [AudioEffectView] from given [parent]
     * @author xeinebiu
     */


    /**
     * Display [AudioEffectView] on a [AudioEffectViewDialogFragment]
     */
    fun showAsDialog(): DialogFragment {
        val dialog = AudioEffectViewDialogFragment()
        dialog.onCreateViewListener = { _, viewGroup ->
            createView(dialog.childFragmentManager, viewGroup).createView()
        }
        dialog.show(fragmentManager, null)
        return dialog
    }

    private fun createView(
        fragmentManager: FragmentManager,
        parent: ViewGroup?,
    ): AudioEffectView =
        AudioEffectView(fragmentManager, context, parent, audioEffectManager)
}