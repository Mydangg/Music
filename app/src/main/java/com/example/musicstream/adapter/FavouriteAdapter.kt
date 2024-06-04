package com.example.musicstream.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.MyExoplayer
import com.example.musicstream.PlayerActivity
import com.example.musicstream.databinding.SongListItemRecyclerRowBinding
import com.example.musicstream.models.CategoryModel
import com.example.musicstream.models.SongModel
import com.example.musicstream.models.favourite
import com.google.firebase.firestore.FirebaseFirestore

class FavouriteAdapter(private val songIdList: List<favourite>) :
    RecyclerView.Adapter<FavouriteAdapter.MyViewHolder>(){
    class MyViewHolder(private val binding: SongListItemRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(favourites : favourite){

            FirebaseFirestore.getInstance().collection("songs")
                . whereEqualTo("id", favourites.id_song).get()
                .addOnSuccessListener {querySnapshot->
                    for (document in querySnapshot) {
                        val song = document.toObject(SongModel::class.java)
                        song?.apply {
                            binding.songTitleTextView.text = title
                            binding.songSubtitleTextView.text = subtitle
                            Glide.with(binding.songCoverImageView).load(coverUrl)
                                .apply(
                                    RequestOptions().transform(RoundedCorners(32)) // Rounded corners
                                )
                                .into(binding.songCoverImageView)
                            binding.root.setOnClickListener {
                                MyExoplayer.startPlaying(binding.root.context, song)
                                it.context.startActivity(
                                    Intent(it.context, PlayerActivity::class.java)
                                )
                            }
                        }
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SongListItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songIdList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(songIdList[position])
    }


}
