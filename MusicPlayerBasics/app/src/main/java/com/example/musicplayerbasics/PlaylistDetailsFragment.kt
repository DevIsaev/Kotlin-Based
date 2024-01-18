package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.ActivityPlaylistDetailsFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

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
        PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist= playlistCheck(PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist)
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

        adapter= AdapterMusicList(requireContext(),PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist,playlistDetails = true)
        binding.playlistDetailsRV.adapter= adapter


        binding.shuffleBtnPD.setOnClickListener {
            val bottomSheet = PlayerFragment.newInstance()
            val bundle = Bundle()
            bundle.putInt("index", 0)
            bundle.putString("class", "PlaylistShuffle")
            bottomSheet.arguments = bundle
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(requireContext(), SelectionToPlaylistActivity::class.java))
        }
        binding.removeAllPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Удаление")
                .setMessage("Вы хотите удалить все композиции?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

        }
    }

    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text = PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfoPD.text = "Всего ${adapter.itemCount} композиций.\n\n" +
                "Created On:\n${PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" +
                "  -- ${PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if(adapter.itemCount > 0)
        {
            Glide.with(this)
                .load(PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artURI)
                .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
                .into(binding.playlistImgPD)
            binding.shuffleBtnPD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()

        //сохранение
        val editor = requireActivity().getSharedPreferences("FAVOURITES", AppCompatActivity.MODE_PRIVATE).edit()
        val jsonStringPL = GsonBuilder().create().toJson(PlaylistsActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPL)
        editor.apply()
    }
    override fun onDestroy() {
        super.onDestroy()


    }
}