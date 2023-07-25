package com.example.firststoreapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

/*        Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()*/

        val itemsList=findViewById<RecyclerView>(R.id.itemsList)
        val items= arrayListOf<Item>()

        items.add(Item(1,"PlayStation2","Best console of 6 generation","Великая и популярная игровая приставка Sony PlayStation 2 проникла в умы миллионов геймеров как одна из самых передовых игровых платформ, которая подарила нам множество разнообразных легендарных игр. Теперь каждый взрослый человек (и не только) может позволить скачать себе игру для PS2 через торрент.",4999.99,"ps2"))
        items.add(Item(2,"PlayStation3","Best console for using BLU-RAY","PlayStation 3 от Sony перевернула страничку шестого поколения приставок и буквально с ноги ворвалась в эру седьмого поколения. Множество игр, графика и невероятные приключенческие эксклюзивы.",12999.99,"ps3"))
        items.add(Item(3,"PlayStation Portable","Playstation in your hand","Популярная портативная приставка от Sony была знаменита даже на фоне своего большого собрата PS3, и снискала свою славу за счет множество игр и хороших эксклюзивов.",2999.99,"psp"))
        items.add(Item(4,"PlayStation Vita","Playstation in your hand with OLED sensors screen","Новая портативная консоль от Sony после PSP предлагала геймерам улучшенное управление и современное железо.",7999.99,"psvita"))
        items.add(Item(5,"PlayStation","First console of family PlayStation","Популярные классические игры для Playstation 1, которые вы так же сможете скачать через торрент. Выберите понравившуюся вам игру и окунитесь в ностальгию по былым временам, и вспомните времена из детства.",3999.99,"psone"))
        itemsList.layoutManager=LinearLayoutManager(this)
        itemsList.adapter=ItemsAdapter(items,this)
    }
}