package com.example.travel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.databinding.CategoryLayoutBinding
import com.example.travel.dataclass.CategoryDataModel

class CategoryAdapter(val context: Context,val itemList:ArrayList<CategoryDataModel>):RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: CategoryLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CategoryLayoutBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val data=itemList[position]
        holder.binding.apply {
            categoryImage.setImageResource(data.image)
            textviewTitleCategory.text=data.title
        }
    }
}