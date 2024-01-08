package com.example.musicplayerbasics

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlayerFragment : BottomSheetDialogFragment(),ServiceConnection {


    lateinit var fontAnim:AnimatorSet
    lateinit var backAnim:AnimatorSet
    var isFont=true

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }

        lateinit var musicListPA:ArrayList<Music>
        var songPosition:Int=0
        //var mediaPlayer:MediaPlayer?=null
        var isPlaying:Boolean=false
        var musicService:MusicSevice?=null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityPlayerBinding.inflate(inflater, container, false)
        //service
        val intent = Intent(requireContext(), MusicSevice::class.java)
        requireContext().bindService(intent, this, BIND_AUTO_CREATE)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet: FrameLayout = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
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

        binding.albumIMG.setOnClickListener {
            if(isFont){
                fontAnim.setTarget(binding.albumIMG)
                backAnim.setTarget(binding.albumIMGback)
                fontAnim.start()
                backAnim.start()
                isFont=false
            }
            else{
                fontAnim.setTarget(binding.albumIMGback)
                backAnim.setTarget(binding.albumIMG)
                fontAnim.start()
                backAnim.start()
                isFont=true
            }
        }

        val scale = requireContext().resources.displayMetrics.density
        binding.albumIMG.cameraDistance = 8000 * scale
        binding.albumIMGback.cameraDistance = 8000 * scale
        fontAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.font) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.back) as AnimatorSet
       songInitialization()


        binding.btnPAUSEPLAY.setOnClickListener {
            if (isPlaying){
                pauseMusic()
            }
            else{
                playMusic()
            }
        }
        binding.btnPREVIOUS.setOnClickListener {
            musicNextPrev(false)
        }
        binding.btnNEXT.setOnClickListener {
            musicNextPrev(true)
        }
    }
    private fun songInitialization(){
    songPosition= arguments?.getInt("index",0)!!
    val classType = arguments?.getString("class")

    if (songPosition != null && classType != null) {
        when (classType) {
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
                createMP()
            }
            "MainActivity"->{
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
                createMP()
            }
        }
    }
}
    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artURI)
            .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
            .into(binding.albumIMG)
        binding.songTITLE.text= musicListPA[songPosition].title +"\n"+musicListPA[songPosition].artist
        binding.durationEND.text= DurationFormat(musicListPA[songPosition].duration)

    }
    private fun createMP(){
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying=true
            binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
            musicService!!.showNotification(R.drawable.baseline_pause_24)
        }
        catch (ex:Exception){
            return
        }
    }

    private fun playMusic(){
        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
        musicService!!.showNotification(R.drawable.baseline_pause_24)
        isPlaying=true
        musicService!!.mediaPlayer!!.start()
    }
    private fun pauseMusic(){
        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_play_arrow_24)
        musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
        isPlaying=false
        musicService!!.mediaPlayer!!.pause()
    }
    private fun musicNextPrev(increment:Boolean){
        if(increment){
            songPosition(true)
            setLayout()
            createMP()
        }
        else{
            songPosition(false)
            setLayout()
            createMP()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        musicService!!.mediaPlayer!!.stop()
        //requireContext().unbindService(this)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
    //Service
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder=service as MusicSevice.MyBinder
        musicService=binder.currentService()
        createMP()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService=null
    }

}


