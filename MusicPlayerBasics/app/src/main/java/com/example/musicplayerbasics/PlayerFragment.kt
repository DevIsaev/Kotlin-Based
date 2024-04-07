package com.example.musicplayerbasics

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.media.AudioManager
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.ActivityPlayerBinding
import com.example.musicplayerbasics.databinding.AudioBoosterBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder


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

        var isPlaying: Boolean = false
        var musicService: MusicSevice? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding

        var repeat: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min45: Boolean = false
        var min60: Boolean = false

        var nowPlayingId:String=""

        var isFavourite:Boolean=false
        var fIndex:Int=-1

        lateinit var loudnessEnhancer: LoudnessEnhancer

        private lateinit var audioEffectManager: AudioEffectManager
        private lateinit var audioEffectViewHelper: AudioEffectViewHelper
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activity?.window?.apply {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
        // прозрачность и полный экран
        dialog?.window?.apply {
            setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        binding = ActivityPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
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
                override fun onStateChanged(bottomSheet: View, newState: Int) {}
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        try {
            songInitialization()

            //изображение альбома
            binding.albumFont.setOnClickListener {
                if (isFont) {
                    fontAnim.setTarget(binding.albumFont)
                    backAnim.setTarget(binding.albumBack)
                    fontAnim.start()
                    backAnim.start()
                    isFont = false
                } else {
                    fontAnim.setTarget(binding.albumBack)
                    backAnim.setTarget(binding.albumFont)
                    fontAnim.start()
                    backAnim.start()
                    isFont = true
                }
            }
            val scale = requireContext().resources.displayMetrics.density
            binding.albumFont.cameraDistance = 8000 * scale
            binding.albumBack.cameraDistance = 8000 * scale
            fontAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.font) as AnimatorSet
            backAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.back) as AnimatorSet


            //воспроизведение\пауза
            binding.btnPAUSEPLAY.setOnClickListener {
                try {

                    if (isPlaying) {
                        pauseMusic()
                    } else {
                        playMusic()
                    }
                } catch (ex: Exception) {
                    binding.songTITLE.text = ex.toString()
                }
            }
            //назад
            binding.btnPREVIOUS.setOnClickListener {
                musicNextPrev(false)
            }
            //далее
            binding.btnNEXT.setOnClickListener {
                musicNextPrev(true)
            }
            //длительность
            binding.SeekBarDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser){
                        musicService!!.mediaPlayer!!.seekTo(progress)
                        musicService!!.showNotification(if(isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

            })
            //повтор
            binding.repeatBTN.setOnClickListener {
                if (!repeat) {
                    repeat = true
                    binding.repeatBTN.setImageResource(R.drawable.baseline_repeat_one_24)
                } else {
                    repeat = false
                    binding.repeatBTN.setImageResource(R.drawable.baseline_repeat_24)
                }
            }

            //эквалайзер
            binding.equalizerBTN.setOnClickListener {
                try {
                        openEqualizerDialog()
                } catch (e: Exception) {
                    Toast.makeText(context, "Эквалайзер не поддерживается", Toast.LENGTH_LONG)
                        .show()
                }
            }
            //таймер
            binding.timerBTN.setOnClickListener {
                val timer = min15 || min30 || min60
                if (!timer)
                    showSheetTimer()
                else {
                    val builder = MaterialAlertDialogBuilder(requireContext())
                    builder.setTitle("Время вышло")
                        .setMessage("Хотите остановить таймер?")
                        .setPositiveButton("Yes") { _, _ ->
                            min15 = false
                            min30 = false
                            min45 = false
                            min60 = false
                            binding.timerBTN.setColorFilter(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green
                                )
                            )
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                }
            }
            //поделиться
            binding.shareBTN.setOnClickListener {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "audio/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
                startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))
            }
            //избранное
            binding.favouriteBTN.setOnClickListener {
                fIndex = favouriteCheck(musicListPA[songPosition].id)
                if (isFavourite) {
                    isFavourite = false
                    binding.favouriteBTN.setImageResource(R.drawable.baseline_favorite_border_24)
                    FavouritesFragment.favSong.removeAt(fIndex)
                } else {
                    isFavourite = true
                    binding.favouriteBTN.setImageResource(R.drawable.baseline_favorite_24)
                    FavouritesFragment.favSong.add(musicListPA[songPosition])
                    Toast.makeText(requireContext(),"Композиция добавлена в Избранное",Toast.LENGTH_SHORT).show()
                }
            }
            //shuffle
            binding.shuffleBTN.setOnClickListener {
                musicListPA.shuffle()
                songPosition = 0
                setLayout()
                createMP()
            }

            //увеличение громкости
            binding.bassBTN.setOnClickListener {
               showSheetBass()
            }

            //свайпы и жесты
            view.setOnTouchListener(object : View.OnTouchListener {
                private val MIN_DISTANCE = 200
                private var x1 = 0f
                private var x2 = 0f
                private var screenWidth = 0

                private var lastTapTimeLeft: Long = 0
                private var lastTapTimeRight: Long = 0
                private val DOUBLE_TAP_DELAY: Long = 500 // Время задержки для двойного нажатия (в миллисекундах)
                private val MAX_SWIPES = 2 // Количество свайпов для множественного нажатия
                private var swipeCountLeft = 1
                private var swipeCountRight = 1

                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            x1 = event.rawX
                            return true
                        }
                        MotionEvent.ACTION_UP -> {

                            val time = System.currentTimeMillis()

                            // Проверка для двойного нажатия влево
                            if (x2 < x1 && time - lastTapTimeLeft < DOUBLE_TAP_DELAY) {
                                swipeCountLeft++
                                if (swipeCountLeft >= MAX_SWIPES) {
                                    // Выполнить перемотку влево на 5 секунд
                                    seekBackward(5000)
                                    Toast.makeText(requireContext(), "backward", Toast.LENGTH_SHORT).show()
                                    swipeCountLeft = 0
                                }
                            } else {
                                swipeCountLeft = 1
                            }
                            lastTapTimeLeft = time

                            // Проверка для двойного нажатия вправо
                            if (x2 > x1 && time - lastTapTimeRight < DOUBLE_TAP_DELAY) {
                                swipeCountRight++
                                if (swipeCountRight >= MAX_SWIPES) {
                                    // Выполнить перемотку вправо на 5 секунд
                                    seekForward(5000)
                                    Toast.makeText(requireContext(), "forward", Toast.LENGTH_SHORT).show()
                                    swipeCountRight = 0
                                }
                            } else {
                                swipeCountRight = 1
                            }
                            lastTapTimeRight = time


                            x2 = event.rawX
                            val deltaX = x2 - x1
                            val threshold = MIN_DISTANCE.coerceAtMost(screenWidth / 2)
                            if (Math.abs(deltaX) > threshold) {
                                // Обработка свайпа
                                if (x2 < x1) {
                                    // Свайп влево
                                    Toast.makeText(requireContext(), "right", Toast.LENGTH_SHORT).show()
                                    musicNextPrev(true)
                                }
                                else {
                                    // Свайп вправо
                                    Toast.makeText(requireContext(), "left", Toast.LENGTH_SHORT).show()
                                    musicNextPrev(false)
                                }
                                return true
                            }
                        }
                    }
                    return false
                }
            })
        }
        catch (ex:Exception){
            binding.songTITLE.text=ex.toString()
            Toast.makeText(requireContext(),ex.toString(),Toast.LENGTH_SHORT).show()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        //musicService!!.mediaPlayer!!.stop()
        //requireContext().unbindService(this)
        try {
            if (musicService != null) {
                NowPlaying.binding.root.visibility = View.VISIBLE
                NowPlaying.binding.songNP.isSelected = true
                Glide.with(requireContext())
                    .load(musicListPA[songPosition].artURI)
                    .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
                    .into(NowPlaying.binding.albumNP)
                NowPlaying.binding.songNP.text = musicListPA[songPosition].title
                NowPlaying.binding.artistNP.text= musicListPA[songPosition].artist
                if (isPlaying) {
                    NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_pause_24)
                } else {
                    NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_play_arrow_24)
                }
            }
            //FavouritesFragment.adapter.updateFavourites(FavouritesFragment.favSong)
        }
        catch (ex:Exception){
            NowPlaying.binding.songNP.text=ex.toString()
        }
        val editor = requireContext().getSharedPreferences("FAVOURITES", Context.MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouritesFragment.favSong)
        editor.putString("FavouriteSongs", jsonString)
        editor.apply()
    }

    //инициализация
    private fun songInitialization() {
        songPosition = arguments?.getInt("index", 0)!!
        val classType = arguments?.getString("class")

        if (songPosition != null && classType != null) {
            when (classType) {

                "NowPlaying" -> {
                    setLayout()
                    binding.durationCURRENT.text= DurationFormat(musicService!!.mediaPlayer!!.currentPosition.toLong())
                    binding.durationEND.text = DurationFormat(musicService!!.mediaPlayer!!.duration.toLong())
                    binding.SeekBarDuration.progress= musicService!!.mediaPlayer!!.currentPosition
                    binding.SeekBarDuration.max= musicService!!.mediaPlayer!!.duration
                    if (isPlaying){
                        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
                    }
                    else{
                        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_play_arrow_24)
                    }
                    //createMP()
                    NowPlaying.binding.root.visibility = View.VISIBLE
                    NowPlaying.binding.songNP.isSelected = true
                    Glide.with(requireContext())
                        .load(musicListPA[songPosition].artURI)
                        .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
                        .into(NowPlaying.binding.albumNP)
                    NowPlaying.binding.songNP.text = musicListPA[songPosition].title
                    NowPlaying.binding.artistNP.text= musicListPA[songPosition].artist
                    if (isPlaying) {
                        NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_pause_24)
                    } else {
                        NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_play_arrow_24)
                    }

                }
                "MusicAdapterSearch"-> initServiceAndPlaylist(MainActivity.MusicListSearch, shuffle = false)
                "MusicAdapter" -> initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = false)
                "MainActivity" -> initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = true)
                "Favourite"-> initServiceAndPlaylist(FavouritesFragment.favSong, shuffle = false)
                "MainActivity"-> initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = true)
                "FavouriteShuffle"-> initServiceAndPlaylist(FavouritesFragment.favSong, shuffle = true)
                "AdapterMusicListPlaylist"-> initServiceAndPlaylist(PlaylistsFragment.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist, shuffle = false)
                "PlaylistShuffle"-> initServiceAndPlaylist(PlaylistsFragment.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist, shuffle = true)
            }
        }
    }

    //вид
    fun setLayout() {
        fIndex= favouriteCheck(musicListPA[songPosition].id)
        Glide.with(this)
            .load(musicListPA[songPosition].artURI)
            .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
            .into(binding.albumIMGFont)
        binding.songTITLE.text = musicListPA[songPosition].title + "\n" + musicListPA[songPosition].artist

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
        if(isFavourite){
            binding.favouriteBTN.setImageResource(R.drawable.baseline_favorite_24)
        }
        else{
            binding.favouriteBTN.setImageResource(R.drawable.baseline_favorite_border_24)
        }

        retrieveMetadata()
        //binding.metadata.text = metadataText
        BG()
    }


    //задний фон
    fun BG(){
        val img = getImage(musicListPA[songPosition].path)
        if (img != null) {
            // Создаем Bitmap из полученных данных изображения
            val albumArtBitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
            // Получение ширины и высоты изображения
            val width = albumArtBitmap.width
            val height = albumArtBitmap.height
            // Вывод ширины и высоты через Toast
            //Toast.makeText(requireContext(), "Ширина: $width, Высота: $height", Toast.LENGTH_LONG).show()

            // Проверка на длину и ширину больше 600
            val shouldBlurMore = width > 600 || height > 600

            // Применяем размытие к изображению альбома через цикл for
            var blurredBitmap = albumArtBitmap
            val blurAmount = 25f
            val iterations = if (shouldBlurMore) 15 else 7
            for (i in 1..iterations) {
                blurredBitmap = blurBitmap(blurredBitmap, blurAmount, requireContext())
            }

            // Создаем Drawable из размытого изображения
            val drawable = BitmapDrawable(resources, blurredBitmap)
            // Создаем GradientDrawable для установки углов и обводки
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = 110f // Здесь можно установить радиус скругления углов
            // Объединяем Drawable с GradientDrawable
            val layersDrawable = LayerDrawable(arrayOf(drawable, gradientDrawable))
            // Устанавливаем созданный Drawable в качестве фона
            binding.bgPlayer.background = layersDrawable


            //
            val image = if (img != null) {
                BitmapFactory.decodeByteArray(img, 0, img.size)
            } else {
                BitmapFactory.decodeResource(resources, R.drawable.icon)
            }
            val bgColor = getMainColor(image)
            val gradient = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(bgColor, bgColor))
            gradient.cornerRadius = resources.getDimension(R.dimen.corner_radius)
            binding.albumIMGBack.background = gradient
        } else {
            // Если изображение не получено, устанавливаем стандартный фон
            binding.bgPlayer.setBackgroundResource(R.drawable.gradient)
        }
    }
    //блюр
    fun blurBitmap(bitmap: Bitmap, radius: Float, context: Context): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)

        output.copyTo(bitmap)
        return bitmap
    }

    //вызов медиаплеера
    private fun createMP() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            //musicService!!.mediaPlayer!!.start()
//            isPlaying = true
//            binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
//            musicService!!.showNotification(R.drawable.baseline_pause_24)

            binding.durationCURRENT.text = DurationFormat(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.durationEND.text = DurationFormat(musicService!!.mediaPlayer!!.duration.toLong())

            binding.SeekBarDuration.progress = 0
            binding.SeekBarDuration.max = musicService!!.mediaPlayer!!.duration

            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId= musicListPA[songPosition].id
            playMusic()
            loudnessEnhancer = LoudnessEnhancer(musicService!!.mediaPlayer!!.audioSessionId)
            loudnessEnhancer.enabled = true


        } catch (ex: Exception) {
            return
        }
    }

    //воспроизведение
    fun playMusic() {
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
        musicService!!.showNotification(R.drawable.baseline_pause_24,)
        NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_pause_24)
    }

    //пауза
    fun pauseMusic() {
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_play_arrow_24)
        musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
        NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_play_arrow_24)
    }

    //след\пред музыка
    private fun musicNextPrev(increment: Boolean) {
        if (!isFont) {
            fontAnim.setTarget(binding.albumBack)
            backAnim.setTarget(binding.albumFont)
            fontAnim.start()
            backAnim.start()
            isFont = true
        }
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

    // Service
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if(musicService == null){
            val binder = service as MusicSevice.MyBinder
            musicService = binder.currentService()
            musicService!!.audioManager = requireContext().getSystemService(AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
        createMP()
        musicService!!.seekBarSetup()
    }
    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    //завершение песни
    override fun onCompletion(mp: MediaPlayer?) {
        songPosition(increment = true)
        createMP()
        try {
            setLayout()
            if (isFont == false) {
                fontAnim.setTarget(binding.albumBack)
                backAnim.setTarget(binding.albumFont)
                fontAnim.start()
                backAnim.start()
                isFont = true
            }
//            NowPlaying.binding.root.visibility = View.VISIBLE
//            NowPlaying.binding.songNP.isSelected = true
//            Glide.with(requireContext())
//                .load(musicListPA[songPosition].artURI)
//                .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
//                .into(NowPlaying.binding.albumNP)
//            NowPlaying.binding.songNP.text = musicListPA[songPosition].title
//            NowPlaying.binding.artistNP.text= musicListPA[songPosition].artist
//            if (isPlaying) {
//                NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_pause_24)
//            } else {
//                NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_play_arrow_24)
//            }
        } catch (ex: Exception) {
            binding.songTITLE.text=ex.toString()
        }
    }

    //
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || requestCode == RESULT_OK) {
            return
        }
    }

    //таймер
    private fun showSheetTimer() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.timer_sheet)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min15)?.setOnClickListener {
            Toast.makeText(context,  "Воспроизведение закончится через 15 минут", Toast.LENGTH_SHORT).show()
            binding.timerBTN.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
            min15 = true
            Thread{Thread.sleep((5000).toLong())
                if(min15) exitApp()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min30)?.setOnClickListener {
            Toast.makeText(context,  "Воспроизведение закончится через 30 минут", Toast.LENGTH_SHORT).show()
            binding.timerBTN.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
            min30 = true
            Thread{Thread.sleep((30*60000).toLong())
                if(min30) exitApp()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min45)?.setOnClickListener {
            Toast.makeText(context,  "Воспроизведение закончится через 45 минут", Toast.LENGTH_SHORT).show()
            binding.timerBTN.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
            min45 = true
            Thread{Thread.sleep((45*60000).toLong())
                if(min45) exitApp()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min60)?.setOnClickListener {
            Toast.makeText(context,  "Воспроизведение закончится через 1 час", Toast.LENGTH_SHORT).show()
            binding.timerBTN.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
            min60 = true
            Thread{Thread.sleep((60*60000).toLong())
                if(min60) exitApp()}.start()
            dialog.dismiss()
        }
    }

    //увеличение громкости
    private fun showSheetBass() {
        try {
            val customDialogB = LayoutInflater.from(requireContext()).inflate(R.layout.audio_booster, binding.root, false)
            val bindingB = AudioBoosterBinding.bind(customDialogB)
            val dialogB = MaterialAlertDialogBuilder(requireContext()).setView(customDialogB)
                .setOnCancelListener { playMusic() }
                .setPositiveButton("OK"){self, _ ->
                    loudnessEnhancer.setTargetGain(bindingB.svsLevelView.value * 100)
                    playMusic()
                    self.dismiss()
                }
                .setBackground(ColorDrawable(Color.GREEN))
                .create()
            dialogB.show()

            bindingB.svsLevelView.value = loudnessEnhancer.targetGain.toInt() / 100
            bindingB.progressText.text = "Audio Boost\n${loudnessEnhancer.targetGain.toInt() / 10} %"
            bindingB.svsLevelView.setOnBoxedPointsChangeListener(object :
                com.ss.svsdemo.SegmentedVerticalSeekBar.OnValuesChangeListener {
                override fun onProgressChanged(segmentedPointsSeekBar: com.ss.svsdemo.SegmentedVerticalSeekBar?, progress: Int) {
                    bindingB.progressText.text = "Audio Boost\n${progress * 10} %"
                }

                override fun onStartTrackingTouch(segmentedPointsSeekBar: com.ss.svsdemo.SegmentedVerticalSeekBar?) {}

                override fun onStopTrackingTouch(segmentedPointsSeekBar: com.ss.svsdemo.SegmentedVerticalSeekBar?) {
                    loudnessEnhancer.setTargetGain(segmentedPointsSeekBar?.value ?: 0)
                }
            })

            setDialogBtnBackground(requireContext(), dialogB)
        }
        catch (ex:Exception){
            binding.songTITLE.text=ex.toString()
        }
    }

    //метаданные
    fun retrieveMetadata() {
        val retriever = MediaMetadataRetriever()
        var path = retriever.setDataSource(musicListPA[songPosition].path)
        val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "-"
        val bitrateT = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toDoubleOrNull()?.div(1000)?.toInt()
        val sampleRateT = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)?.toDoubleOrNull()?.div(1000)
        val genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: "-"
        val mex = MediaExtractor()
        mex.setDataSource(musicListPA[songPosition].path)
        val mf = mex.getTrackFormat(0)
        val channels: Int = mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        val codec = mf.getString(MediaFormat.KEY_MIME)
        val bitDepthString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE)

        retriever.release()

        val bitDepth = bitDepthString?.toIntOrNull() ?: 16
        // Проверка разрядности и частоты дискретизации
        val isHighResolution = bitDepth >= 24 && sampleRateT != null && sampleRateT >= 48.0
        // Отображение логотипа в зависимости от условий
        binding.hiresLogo.visibility = if (isHighResolution) View.VISIBLE else View.GONE

        // Извлечение расширения файла из пути
        val filePath = musicListPA[songPosition].path
        val fileExtension = filePath.substringAfterLast(".", "Неизвестно")


        binding.AlbumMETA.text="Альбом: $album"
        binding.AlbumMETA.isSelected=true
        binding.GenreMETA.text="Жанр(ы): $genre"
        binding.BitrateMETA.text= "Битрейт: $bitrateT Кбит/сек"
        binding.SampleRateMETA.text="Частота сэмплов: ${sampleRateT} КГц"
        binding.ChannelsMETA.text=  "Каналы: $channels"
        binding.DepthMETA.text="Разрядность: ${bitDepth} бит"
        //binding.CodecMETA.text="Кодек: $codec"

        when (fileExtension.toUpperCase()){
            "FLAC"-> binding.formatLogo.setImageResource(R.drawable.flac)
            "MP3"-> binding.formatLogo.setImageResource(R.drawable.mp3logo)
            "M4A"-> binding.formatLogo.setImageResource(R.drawable.alac)
            "WAV"-> binding.formatLogo.setImageResource(R.drawable.wav)
            "DTS"-> binding.formatLogo.setImageResource(R.drawable.dts)
            "AC3"-> binding.formatLogo.setImageResource(R.drawable.dolbylogo)
        }

    }

    //перемотка назад
    private fun seekForward(milliseconds: Int) {
        val newPosition = musicService!!.mediaPlayer!!.currentPosition + milliseconds
        if (newPosition < musicService!!.mediaPlayer!!.duration) {
            musicService!!.mediaPlayer!!.seekTo(newPosition)
        } else {
            // Перемотка до конца композиции
            musicService!!.mediaPlayer!!.seekTo(musicService!!.mediaPlayer!!.duration)
        }
    }
    //перемотка вперед
    private fun seekBackward(milliseconds: Int) {
        val newPosition = musicService!!.mediaPlayer!!.currentPosition - milliseconds
        if (newPosition > 0) {
            musicService!!.mediaPlayer!!.seekTo(newPosition)
        } else {
            // Перемотка до начала композиции
            musicService!!.mediaPlayer!!.seekTo(0)
        }
    }

    private fun openEqualizerDialog() {
        try {
            val audioSessionId = musicService!!.mediaPlayer!!.audioSessionId
            audioEffectManager = AudioEffectManager(audioSessionId)
            audioEffectViewHelper = AudioEffectViewHelper(
                requireContext(),
                requireActivity().supportFragmentManager,
                audioEffectManager,
            )
            audioEffectViewHelper.showAsDialog()
    }
    catch (ex:Exception){
        Toast.makeText(requireContext(),ex.toString(),Toast.LENGTH_SHORT).show()
    }
    }

    override fun onResume() {
        super.onResume()
        setLayout()
        BG()
    }

    private fun initServiceAndPlaylist(playlist: ArrayList<Music>, shuffle: Boolean){
        val intent = Intent(requireContext(), MusicSevice::class.java)
        requireContext().bindService(intent, this, BIND_AUTO_CREATE)
        requireContext().startService(intent)
        musicListPA = ArrayList()
        musicListPA.addAll(playlist)
        if(shuffle) musicListPA.shuffle()
        setLayout()
    }
}