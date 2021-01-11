package com.shareimages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.shareimages.R
import com.shareimages.SharedImage
import com.shareimages.model.MyImage
import java.util.*

class GaleryAdapter(private val context: Context,
                    private val imagens: ArrayList<MyImage>) :
    RecyclerView.Adapter<GaleryAdapter.Holder>() {

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewCard = itemView.findViewById<ImageView>(R.id.imageViewCard)
        val buttonShared: ImageButton = itemView.findViewById<ImageButton>(R.id.buttonShared)
        fun add(image: MyImage){
            imageViewCard.setImageBitmap(image.icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false))
    }

    override fun getItemCount(): Int {
        return imagens.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.add(imagens[position])
        holder.buttonShared.setOnClickListener {
            SharedImage(context, imagens[position].file)
                .shared()
        }
    }
}
