package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.ActivityPlaylistDetailsFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaylistDetailsFragment : BottomSheetDialogFragment() {

    @SuppressLint("StaticFieldLeak")
    lateinit var binding: ActivityPlaylistDetailsFragmentBinding

    companion object{
        fun newInstance(): PlaylistDetailsFragment {
            return PlaylistDetailsFragment()
        }

        var currentPlaylistPos:Int=-1
        lateinit var adapter:AdapterMusicList
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityPlaylistDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet: FrameLayout =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        // Height of the view
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        // Behavior of the bottom sheet
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            peekHeight = resources.displayMetrics.heightPixels // Pop-up height
            state = BottomSheetBehavior.STATE_EXPANDED // Expanded state
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        currentPlaylistPos= arguments?.getInt("index", 0)!!
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager=LinearLayoutManager(context)
        PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist.addAll(MainActivity.MusicListMA)
        adapter= AdapterMusicList(requireContext(),PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist,playlistDetails = true)
        binding.playlistDetailsRV.adapter= adapter

        binding.shuffleBtnPD.setOnClickListener {

        }
    }

    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text=PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfoPD.text="Всего композиций: ${adapter.itemCount}. \n\n"+
                "Создано: ${PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}. \n\n"+
                "--${PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if(adapter.itemCount>0){
            Glide.with(requireContext())
                .load(PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artURI)
                .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
                .into(binding.playlistImgPD)

            binding.shuffleBtnPD.visibility=View.VISIBLE
        }
    }
    override fun onDestroy() {
        super.onDestroy()

    }

}