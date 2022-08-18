package com.example.e_commerceapp

import android.content.res.Resources
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter( val listener: productItemClicked): RecyclerView.Adapter<ViewHolder>() {
    val items:ArrayList<Product> = ArrayList()
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout , parent , false )
        val viewholder = ViewHolder(view);
        view.setOnClickListener {
            listener.onItemClicked(viewholder.adapterPosition)
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = items[position]
        holder.name.text = p.productName
        holder.realPrice.text = p.productPriceReal
        holder.realPrice.setPaintFlags(holder.realPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        holder.discountPrice.text = p.discountProductPrice
        holder.percentageOff.text = p.percentageOff + "% Off"
        holder.status.text = p.productStatus
        holder.description.text = p.productDescription
        holder.rating.rating = 4F
        Glide.with(holder.itemView.context).load(p.imageUrl).into(holder.imageview)


    }

    fun onUpdateProduct(newProduct : Product) {
        items.add(newProduct)
        notifyDataSetChanged()
    }

    fun onRemoveProduct(removeProduct : Product) {
         items.remove(removeProduct)
        notifyDataSetChanged()
    }

    fun onUpdateExistingProduct(position:Int , updatedproduct : Product) {
        items.set( position , updatedproduct)
        notifyDataSetChanged()
    }

}

class ViewHolder(items : View) : RecyclerView.ViewHolder(items) {
    var imageview:ImageView = items.findViewById(R.id.productImageView)
    var name : TextView = items.findViewById(R.id.productname)
    var realPrice : TextView = items.findViewById(R.id.real_product_price)
    var discountPrice :TextView = items.findViewById(R.id.discount_product_price)
    var percentageOff : TextView = items.findViewById(R.id.discount_percentage)
    var status  : TextView = items.findViewById(R.id.product_status)
    var description  : TextView = items.findViewById(R.id.product_description)
    var rating :RatingBar = items.findViewById(R.id.ratingBar)

}

interface productItemClicked {
    fun onItemClicked(position:Int) {

    }
}