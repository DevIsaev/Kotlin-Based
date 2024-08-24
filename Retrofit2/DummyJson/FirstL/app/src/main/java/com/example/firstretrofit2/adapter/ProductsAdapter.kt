package com.example.firstretrofit2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstretrofit2.R
import com.example.firstretrofit2.databinding.ProductItemBinding
import com.example.firstretrofit2.retrofit.Product

class ProductsAdapter:ListAdapter<Product, ProductsAdapter.Holder>(Comparator()) {
    class Holder(view: View):RecyclerView.ViewHolder(view){
        private var binding=ProductItemBinding.bind(view)

        fun bind(product: Product)= with(binding){
            title.text=product.title
            desc.text=product.rating.toString()
            price.text=product.price.toString()
        }
    }
    class Comparator:DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem==newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.product_item,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}