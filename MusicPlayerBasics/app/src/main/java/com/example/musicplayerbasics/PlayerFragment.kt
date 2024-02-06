package com.example.musicplayerbasics

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.media.AudioManager
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.renderscript.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder


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

        var nowPlayingId:String=""

        var isFavourite:Boolean=false
        var fIndex:Int=-1
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activity?.window?.apply {
            decorView.systemUiVisibility = (  View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                decorView.windowInsetsController?.hide(WindowInsets.Type.statusBars())
            }
        }

        // Устанавливаем флаги для прозрачности и full screen
        dialog?.window?.apply {
            setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        //setStyle(STYLE_NORMAL,MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivityPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                decorView.windowInsetsController?.show(WindowInsets.Type.statusBars())
            }
        }

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

        try {
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

            songInitialization()

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
            binding.SeekBarDuration.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
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
                    val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                    eqIntent.putExtra(
                        AudioEffect.EXTRA_AUDIO_SESSION,
                        musicService!!.mediaPlayer!!.audioSessionId
                    )
                    eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context?.packageName)
                    eqIntent.putExtra(
                        AudioEffect.EXTRA_CONTENT_TYPE,
                        AudioEffect.CONTENT_TYPE_MUSIC
                    )
                    startActivityForResult(eqIntent, 13)
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
                startActivity(Intent.createChooser(shareIntent, "Поделиться композицией?"))
            }
            //избранное
            binding.favouriteBTN.setOnClickListener {
                fIndex = favouriteCheck(musicListPA[songPosition].id)
                if (isFavourite) {
                    isFavourite = false
                    binding.favouriteBTN.setImageResource(R.drawable.baseline_favorite_border_24)
                    FavouritesActivity.favSong.removeAt(fIndex)
                } else {
                    isFavourite = true
                    binding.favouriteBTN.setImageResource(R.drawable.baseline_favorite_24)
                    FavouritesActivity.favSong.add(musicListPA[songPosition])
                    Toast.makeText(requireContext(),"Композиция добавлена в Избранное",Toast.LENGTH_SHORT).show()
                }
            }
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
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
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
        }
        catch (ex:Exception){
            NowPlaying.binding.songNP.text="1"
        }
    }

    //инициализация
    private fun songInitialization() {
        songPosition = arguments?.getInt("index", 0)!!
        val classType = arguments?.getString("class")

        if (songPosition != null && classType != null) {
            when (classType) {
                "MusicAdapter" -> {
                    //service
                    val intent = Intent(requireContext(), MusicSevice::class.java)
                    requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                    musicListPA = ArrayList()
                    musicListPA.addAll(MainActivity.MusicListMA)
                    setLayout()
                    //createMP()
                }
                "MusicAdapterSearch" -> {
                    //service
                    val intent = Intent(requireContext(), MusicSevice::class.java)
                    requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                    musicListPA = ArrayList()
                    musicListPA.addAll(MainActivity.MusicListSearch)
                    setLayout()
                    //createMP()
                }
                "MainActivity" -> {
                    //service
                    val intent = Intent(requireContext(), MusicSevice::class.java)
                    requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                    musicListPA = ArrayList()
                    musicListPA.addAll(MainActivity.MusicListMA)
                    musicListPA.shuffle()
                    setLayout()
                    //createMP()
                }
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
                }
                "Favourite"->{
                    val intent = Intent(requireContext(), MusicSevice::class.java)
                    requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                    musicListPA = ArrayList()
                    musicListPA.addAll(FavouritesActivity.favSong)
                    setLayout()
                }
                "FavouriteShuffle"->{
                    val intent = Intent(requireContext(), MusicSevice::class.java)
                    requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                    musicListPA = ArrayList()
                    musicListPA.addAll(FavouritesActivity.favSong)
                    musicListPA.shuffle()
                    setLayout()
                }
                "AdapterMusicListPlaylist"->{
                    val intent = Intent(requireContext(), MusicSevice::class.java)
                    requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                    musicListPA = ArrayList()
                    musicListPA.addAll(PlaylistsActivity.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist)
                    setLayout()
                }
                "PlaylistShuffle"->{
                    val intent = Intent(requireContext(), MusicSevice::class.java)
                    requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                    musicListPA = ArrayList()
                    musicListPA.addAll(PlaylistsActivity.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist)
                    musicListPA.shuffle()
                    setLayout()
                }
            }
        }
    }

    //вид
    private fun setLayout() {
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

        val metadataText = retrieveMetadata()
        binding.metadata.text = metadataText


//        val img = getImage(musicListPA[songPosition].path)
//        val image = if (img != null) {
//            BitmapFactory.decodeByteArray(img, 0, img.size)
//        } else {
//            BitmapFactory.decodeResource(resources, R.drawable.icon)
//        }
//        val bgColor = getMainColor(image)
//
//        val gradient = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(bgColor, bgColor))
//        gradient.cornerRadius = resources.getDimension(R.dimen.corner_radius)
//        gradient.setStroke(resources.getDimensionPixelSize(R.dimen.stroke_width), Color.BLACK)
//        binding.bgPlayer.background = gradient

        val img = getImage(musicListPA[songPosition].path)
        if (img != null) {
            // Создаем Bitmap из полученных данных изображения
            val albumArtBitmap = BitmapFactory.decodeByteArray(img, 0, img.size)

            // Применяем размытие к изображению альбома
            val blurredBitmap1 = blurBitmap(albumArtBitmap, 25f, requireContext())
            val blurredBitmap2 = blurBitmap(blurredBitmap1, 25f, requireContext())
            val blurredBitmap3 = blurBitmap(blurredBitmap2, 25f, requireContext())
            val blurredBitmap4 = blurBitmap(blurredBitmap3, 25f, requireContext())
            val blurredBitmap5 = blurBitmap(blurredBitmap4, 25f, requireContext())
            val blurredBitmap6 = blurBitmap(blurredBitmap5, 25f, requireContext())
            val blurredBitmap = blurBitmap(blurredBitmap6, 25f, requireContext())

            // Создаем Drawable из размытого изображения
            val drawable = BitmapDrawable(resources, blurredBitmap)

            // Создаем GradientDrawable для установки углов и обводки
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = 110f // Здесь можно установить радиус скругления углов
            //gradientDrawable.setStroke(5, Color.WHITE) // Устанавливаем обводку

            // Объединяем Drawable с GradientDrawable
            val layersDrawable = LayerDrawable(arrayOf(drawable, gradientDrawable))

            // Устанавливаем созданный Drawable в качестве фона
            binding.bgPlayer.background = layersDrawable
        } else {
            // Если изображение не получено, устанавливаем стандартный фон
            binding.bgPlayer.setBackgroundResource(R.drawable.grad)
        }


    }


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
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
            musicService!!.showNotification(R.drawable.baseline_pause_24)

            binding.durationCURRENT.text =
                DurationFormat(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.durationEND.text = DurationFormat(musicService!!.mediaPlayer!!.duration.toLong())
            binding.SeekBarDuration.progress = 0
            binding.SeekBarDuration.max = musicService!!.mediaPlayer!!.duration

            musicService!!.mediaPlayer!!.setOnCompletionListener(this)

            nowPlayingId= musicListPA[songPosition].id
        } catch (ex: Exception) {
            return
        }
    }

    //воспроизведение
    private fun playMusic() {
        isPlaying = true
        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
        musicService!!.showNotification(R.drawable.baseline_pause_24,)
        musicService!!.mediaPlayer!!.start()
    }

    //пауза
    private fun pauseMusic() {
        isPlaying = false
        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_play_arrow_24)
        musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
        musicService!!.mediaPlayer!!.pause()
    }

    //след\пред музыка
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

    //song completed
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

    fun retrieveMetadata(): String {
        val retriever = MediaMetadataRetriever()
        var path = retriever.setDataSource(musicListPA[songPosition].path)

        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Неизвестное название"
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Неизвестный исполнитель"
        val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Неизвестный альбом"
        val bitrateT = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toDoubleOrNull()?.div(1000)?.toInt()
        val sampleRateT = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)?.toDoubleOrNull()?.div(1000)
        val genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: "Неизвестный жанр"

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

        return "Альбом: $album\n" +
                "Скорость передачи бит. : $bitrateT Кбит/сек\n" +
                "Частота дискретизации: $sampleRateT КГц\n" +
                "Разрядность бит. : ${bitDepth} бит\n" +
                "Жанр(ы): $genre\n" +
                "Каналы: $channels\n" +
                "Кодек: $codec\n" +
                "Формат: ${fileExtension.toUpperCase()}"
    }
}