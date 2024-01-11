package com.example.musicplayerbasics

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlayerFragment : BottomSheetDialogFragment(),ServiceConnection,MediaPlayer.OnCompletionListener {


    lateinit var fontAnim: AnimatorSet
    lateinit var backAnim: AnimatorSet
    var isFont = true

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }

        lateinit var musicListPA: ArrayList<Music>
        var songPosition: Int = 0

        //var mediaPlayer:MediaPlayer?=null
        var isPlaying: Boolean = false
        var musicService: MusicSevice? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding

        var repeat: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min45: Boolean = false
        var min60: Boolean = false
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityPlayerBinding.inflate(inflater, container, false)

        //service
        val intent = Intent(requireContext(), MusicSevice::class.java)
        requireContext().bindService(intent, this, BIND_AUTO_CREATE)
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

        binding.albumIMG.setOnClickListener {
            if (isFont) {
                fontAnim.setTarget(binding.albumIMG)
                backAnim.setTarget(binding.albumIMGback)
                fontAnim.start()
                backAnim.start()
                isFont = false
            } else {
                fontAnim.setTarget(binding.albumIMGback)
                backAnim.setTarget(binding.albumIMG)
                fontAnim.start()
                backAnim.start()
                isFont = true
            }
        }
        val scale = requireContext().resources.displayMetrics.density
        binding.albumIMG.cameraDistance = 8000 * scale
        binding.albumIMGback.cameraDistance = 8000 * scale
        fontAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.font) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.back) as AnimatorSet

        songInitialization()

        binding.btnPAUSEPLAY.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        binding.btnPREVIOUS.setOnClickListener {
            musicNextPrev(false)
        }
        binding.btnNEXT.setOnClickListener {
            musicNextPrev(true)
        }

        binding.SeekBarDuration.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
        binding.repeatBTN.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.repeatBTN.setImageResource(R.drawable.baseline_repeat_one_24)
            } else {
                repeat = false
                binding.repeatBTN.setImageResource(R.drawable.baseline_repeat_24)
            }
        }
        binding.equalizerBTN.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context?.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 13)
            } catch (e: Exception) {
                Toast.makeText(context, "Эквалайзер не поддерживается", Toast.LENGTH_LONG).show()
            }
        }

        binding.timerBTN.setOnClickListener {
            if (!min15 || !min30 || !min45 || !min60) {
                showSheetTimer()
            } else {
                Out()
            }
        }
        binding.shareBTN.setOnClickListener {
            val shareIntent=Intent()
            shareIntent.action=Intent.ACTION_SEND
            shareIntent.type="audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Поделиться композицией?"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //musicService!!.mediaPlayer!!.stop()
        //requireContext().unbindService(this)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }


    private fun songInitialization() {
        songPosition = arguments?.getInt("index", 0)!!
        val classType = arguments?.getString("class")

        if (songPosition != null && classType != null) {
            when (classType) {
                "MusicAdapter" -> {
                    musicListPA = ArrayList()
                    musicListPA.addAll(MainActivity.MusicListMA)
                    setLayout()
                    createMP()
                }

                "MainActivity" -> {
                    musicListPA = ArrayList()
                    musicListPA.addAll(MainActivity.MusicListMA)
                    musicListPA.shuffle()
                    setLayout()
                    createMP()
                }
            }
        }
    }

    private fun setLayout() {
        Glide.with(this)
            .load(musicListPA[songPosition].artURI)
            .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
            .into(binding.albumIMG)
        binding.songTITLE.text =
            musicListPA[songPosition].title + "\n" + musicListPA[songPosition].artist

        if (repeat) {
            repeat = true
            binding.repeatBTN.setImageResource(R.drawable.baseline_repeat_one_24)
        }
        if (min15 || min30 || min45 || min60)
            binding.timerBTN.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.highlightColor
                )
            )
    }

    private fun createMP() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
            musicService!!.showNotification(R.drawable.baseline_pause_24)

            binding.durationCURRENT.text =
                DurationFormat(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.durationEND.text =
                DurationFormat(musicService!!.mediaPlayer!!.duration.toLong())
            binding.SeekBarDuration.progress = 0
            binding.SeekBarDuration.max = musicService!!.mediaPlayer!!.duration

            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        } catch (ex: Exception) {
            return
        }
    }

    private fun playMusic() {
        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
        musicService!!.showNotification(R.drawable.baseline_pause_24)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_play_arrow_24)
        musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun musicNextPrev(increment: Boolean) {
        if (increment) {
            songPosition(true)
            setLayout()
            createMP()
        } else {
            songPosition(false)
            setLayout()
            createMP()
        }
    }

    //Service
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicSevice.MyBinder
        musicService = binder.currentService()
        createMP()
        musicService!!.seekBarSetup()

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    //song completed
    override fun onCompletion(mp: MediaPlayer?) {
        songPosition(increment = true)
        createMP()
        try {
            setLayout()
            if (isFont == false) {
                fontAnim.setTarget(binding.albumIMGback)
                backAnim.setTarget(binding.albumIMG)
                fontAnim.start()
                backAnim.start()
                isFont = true
            }
        } catch (ex: Exception) {
            return
        }
    }

    //
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || requestCode == RESULT_OK) {
            return
        }
    }

    //timer
    private fun showSheetTimer() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.timer_sheet)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min15)?.setOnClickListener {
            Toast.makeText(
                context,
                "Воспроизведение остановится через 15 минут",
                Toast.LENGTH_SHORT
            ).show()

            binding.timerBTN.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.highlightColor
                )
            )
            min15 = true
            Thread {
                Thread.sleep(5000)
                if (min15) exitApp()
            }.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min30)?.setOnClickListener {
            Toast.makeText(
                context,
                "Воспроизведение остановится через 30 минут",
                Toast.LENGTH_SHORT
            ).show()
            binding.timerBTN.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.highlightColor
                )
            )
            min30 = true
            Thread {
                Thread.sleep(30*60000)
                if (min30) exitApp()
            }.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min45)?.setOnClickListener {
            Toast.makeText(
                context,
                "Воспроизведение остановится через 45 минут",
                Toast.LENGTH_SHORT
            ).show()
            binding.timerBTN.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.highlightColor
                )
            )
            min45 = true
            Thread {
                Thread.sleep(45*60000)
                if (min45) exitApp()
            }.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min60)?.setOnClickListener {
            Toast.makeText(context, "Воспроизведение остановится через 1 час", Toast.LENGTH_SHORT)
                .show()
            binding.timerBTN.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.highlightColor
                )
            )
            min60 = true
            Thread {
                Thread.sleep(60*60000)
                if (min60) exitApp()
            }.start()

            dialog.dismiss()
            dialog.dismiss()
        }
    }

    fun Out() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Время вышло")
        builder.setMessage("Вы действительно хотите остановить таймер?")
        builder.setPositiveButton("Да") { dialogInterface, i ->
            min15 = false
            min30 = false
            min45 = false
            min60 = false
            binding.timerBTN.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
        }
        builder.setNegativeButton("Нет") { dialogInterface, i ->
        }
        builder.show()
    }
}


