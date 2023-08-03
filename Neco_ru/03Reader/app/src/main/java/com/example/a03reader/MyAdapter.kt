package com.example.a03reader

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(listArr: ArrayList<ListItem>, context:Context):
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {


    var lArr=listArr
    var context=context

    class ViewHolder(view:View):RecyclerView.ViewHolder(view) {

        var img=view.findViewById<ImageView>(R.id.imgView)
        var title=view.findViewById<TextView>(R.id.tvName)
        var desc=view.findViewById<TextView>(R.id.tvDesc)

        fun bind(listItem: ListItem, context: Context){
            title.text=listItem.title
            desc.text=listItem.desc.substring(0,15)+"..."
            img.setImageResource(listItem.img)



            itemView.setOnClickListener {
                Toast.makeText(context,"test - ${title.text}",Toast.LENGTH_SHORT).show()

                var i= Intent(context,ItemActivity::class.java).apply{
                    putExtra("Title",title.text.toString())
                    putExtra("Desc",listItem.desc)
                    putExtra("Text",listItem.text)
                    putExtra("Image",listItem.img)
                }
                context.startActivity(i)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.item_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return lArr.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var listItem=lArr.get(position)
        holder.bind(listItem,context)
    }

    fun UpdateAdapter(listArray:List<ListItem>){
        lArr.clear()
        lArr.addAll(listArray)
        notifyDataSetChanged()
    }

}