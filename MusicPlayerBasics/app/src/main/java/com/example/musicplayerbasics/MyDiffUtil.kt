package com.example.musicplayerbasics

import androidx.recyclerview.widget.DiffUtil

class MyDiffUtil(private val oldList: List<Music>,
                 private val newList: List<Music>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Здесь можно добавить дополнительную логику для сравнения содержимого элементов,
        // если данные обновляются не только по идентификатору, но и по содержимому
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    // Метод для определения изменений в содержимом элементов, если areContentsTheSame() вернул false
    // Вы можете переопределить этот метод для оптимизации обновления только определенных частей ViewHolder
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Например, если у вас есть специфичные данные, которые нужно обновить
        // Этот метод вызывается только если areContentsTheSame() вернул false
        // Возвращаем данные, которые нужно обновить
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}