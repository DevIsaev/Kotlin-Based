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

        Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()

        val itemsList=findViewById<RecyclerView>(R.id.itemsList)
        val items= arrayListOf<Item>()

        items.add(Item(
            1,
            "PlayStation One",
            "Первая консоль семейства PlayStation",
            "PSone сохранила полную совместимость со всем программным обеспечением (играми и программами типа «Взломщик кодов») PlayStation. Новый вариант консоли имел всего три отличия от «классической» модели: первое — косметические изменение внешнего вида, второе — изменения во встроенном в приставку графическом интерфейсе меню управления картой памяти и CD-проигрывателем, и третье — переработка разводки материнской платы, из-за чего стало невозможным использование старых мод-чипов. Кроме того, PSone потерял последовательный порт, который позволял объединить несколько консолей для многопользовательской игры.",
            4999.99,
            "psone"
        ))
        items.add(Item(
            2,
            "PlayStation 2",
            "Вторая консоль семейства PlayStation",
            "вторая игровая приставка, выпущенная компанией Sony, наследница PlayStation и предшественница PlayStation 3. О начале разработки было объявлено в марте 1999 г., продажа консоли в Японии началась 4 марта 2000 г., в Северной Америке — 26 октября 2000 г., в Европе — 24 ноября 2000 года, в России — 7 ноября 2002 года.\n" +
                    "\n" +
                    "Игровая приставка стала наиболее быстро продаваемой и самой популярной игровой консолью в истории. На третий квартал 2011 года в мире продано почти 155 миллионов экземпляров PS2",
            4999.99,
            "ps2"
        ))
        itemsList.layoutManager=LinearLayoutManager(this)
        itemsList.adapter=ItemsAdapter(items,this)
    }
}