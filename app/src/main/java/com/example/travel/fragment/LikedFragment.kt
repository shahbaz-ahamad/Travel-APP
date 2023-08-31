package com.example.travel.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel.Detail
import com.example.travel.adapter.LikedAdapter
import com.example.travel.databinding.FragmentLikedBinding
import com.example.travel.dataclass.ItemDataModel
import com.google.firebase.database.*


class LikedFragment : Fragment(),LikedAdapter.ItemClickListner {

    private lateinit var binding: FragmentLikedBinding
    private lateinit var likedList: ArrayList<ItemDataModel>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var itemClickListner: LikedAdapter.ItemClickListner
    private lateinit var likedAdapter: LikedAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        likedList = ArrayList()
        itemClickListner=this
        binding.recyclerViewLiked.setHasFixedSize(true)
        binding.recyclerViewLiked.layoutManager = LinearLayoutManager(context)
        likedAdapter = LikedAdapter(requireContext(), likedList, itemClickListner)
        binding.recyclerViewLiked.adapter = likedAdapter
        fetchTheLikedData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLikedBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun fetchTheLikedData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Travel").child("Popular")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                likedList.clear()
                for (data in snapshot.children) {

                    val itemDataModel = data.getValue(ItemDataModel::class.java)

                    if (itemDataModel != null && itemDataModel.liked == true) {
                        likedList.add(itemDataModel)
                    }
                }
                likedAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onItemClick(dataModel: ItemDataModel) {
        super.onItemClick(dataModel)
        goToDetailActivity(dataModel)
    }

    private fun goToDetailActivity(dataModel: ItemDataModel) {
        val intent=Intent(context,Detail::class.java)
        intent.putExtra("dataModel",dataModel)
        startActivity(intent)
    }

}