package com.example.travel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travel.databinding.PopularLayoutBinding
import com.example.travel.dataclass.ItemDataModel

class PopularAdapter(val context: Context,val itemList:ArrayList<ItemDataModel>,val itemClickListner:ItemClickListner):RecyclerView.Adapter<PopularAdapter.ViewHolder>() {
    class ViewHolder(val binding:PopularLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(PopularLayoutBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
       return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data=itemList[position]
        holder.binding.apply {


            tvTitle.text=data.title
            tvlocation.text=data.location
            textViewrating.text= data.score.toString()
            val imageUrl=data.pic.toUri()
            Glide.with(context)
                .load(imageUrl)
                .into(imagePopular)


        }

        holder.itemView.setOnClickListener{
            itemClickListner.onItemClick(data)
        }
    }

    interface ItemClickListner{
        fun onItemClick(dataModel: ItemDataModel){
        }
    }
}


