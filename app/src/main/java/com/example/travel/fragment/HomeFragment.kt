package com.example.travel.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.example.travel.Detail
import com.example.travel.R
import com.example.travel.adapter.CategoryAdapter
import com.example.travel.adapter.PopularAdapter
import com.example.travel.databinding.FragmentHomeBinding
import com.example.travel.dataclass.CategoryDataModel
import com.example.travel.dataclass.ItemDataModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment(), PopularAdapter.ItemClickListner {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularAdapter: PopularAdapter
    private lateinit var itemClickListner: PopularAdapter.ItemClickListner
    private lateinit var itemPopular: ArrayList<ItemDataModel>
    private lateinit var itemCategory: ArrayList<CategoryDataModel>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dataModel: ItemDataModel
    private lateinit var auth: FirebaseAuth
    private lateinit var name:String
    private lateinit var profilePic:String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemPopular=ArrayList()
        auth=Firebase.auth
        fetchProfileFromRD()

        //start the animation
         binding.shimmerLayoutPopular.startShimmerAnimation()
        itemClickListner = this
        binding.recyclerViewpopular.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        popularAdapter = PopularAdapter(requireContext(), itemPopular, itemClickListner)
        binding.recyclerViewpopular.adapter = popularAdapter
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerViewpopular)
        addDataToCategoryRecyclerView()
        binding.recyclerViewCategory.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter(requireContext(), itemCategory)
        binding.recyclerViewCategory.adapter = categoryAdapter

        fetchTheData()

    }

    private fun fetchTheData() {
        databaseReference=FirebaseDatabase.getInstance().getReference("Travel").child("Popular")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                itemPopular.clear()

                for (data in snapshot.children){
                    if(data!=null){
                        dataModel= data.getValue(ItemDataModel::class.java)!!
                        itemPopular.add(dataModel)
                    }

                }
               //  Hide the shimmer layout and show the RecyclerView
                binding.shimmerLayoutPopular.stopShimmerAnimation()
                binding.shimmerLayoutPopular.visibility = View.GONE

                // Notify the adapter that data has changed
                popularAdapter.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun addDataToCategoryRecyclerView() {
        itemCategory = ArrayList()
        itemCategory.add(CategoryDataModel("Beaches", R.drawable.cat1))
        itemCategory.add(CategoryDataModel("Camps", R.drawable.cat2))
        itemCategory.add(CategoryDataModel("Forest", R.drawable.cat3))
        itemCategory.add(CategoryDataModel("Desert", R.drawable.cat4))
        itemCategory.add(CategoryDataModel("Mountain", R.drawable.cat5))


    }



    fun goToDetailActivity(dataModel: ItemDataModel) {
        val intent = Intent(context, Detail::class.java)
        intent.putExtra("dataModel", dataModel)
        startActivity(intent)
    }

    override fun onItemClick(dataModel: ItemDataModel) {
        super.onItemClick(dataModel)
        goToDetailActivity(dataModel)
    }

    fun fetchProfileFromRD(){
        databaseReference= FirebaseDatabase.getInstance().getReference("Travel").child("Users").child(auth.currentUser?.uid.toString())
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    name=snapshot.child("name").value.toString()
                    profilePic=snapshot.child("profile").value.toString()
                    binding.textViewName.text=name

                    if(profilePic!=""){

                        val profileUri=profilePic.toUri()
                        Glide
                            .with(requireContext())
                            .load(profileUri)
                            .into(binding.profileImage)
                    }



                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}

