package com.example.firststoreapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(var items:List<Item>,var context:Context):RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val image:ImageView=view.findViewById(R.id.ItemImage)
        val title:TextView=view.findViewById(R.id.ItemTitle)
        val desc:TextView=view.findViewById(R.id.ItemDesc)
        val price:TextView=view.findViewById(R.id.ItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_picture,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  items.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text=items[position].title
        holder.desc.text=items[position].desc
        holder.price.text=items[position].price.toString()+" руб."

        var imgID=context.resources.getIdentifier(
            items[position].image,
            "drawable",
            context.packageName
        )
        holder.image.setImageResource(imgID)
    }
}