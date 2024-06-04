package com.example.musicstream.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.MyExoplayer
import com.example.musicstream.PlayerActivity
import com.example.musicstream.PlaylistDetails
import com.example.musicstream.SongsListActivity
import com.example.musicstream.databinding.SongListItemRecyclerRowBinding
import com.example.musicstream.models.SongModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SongListAdapter(private val songIdList: List<String>, private val playlistId: String,private val playlistName: String) :
    RecyclerView.Adapter<SongListAdapter.SongViewHolder>(){
    private val db = FirebaseFirestore.getInstance()
    class SongViewHolder(private val binding: SongListItemRecyclerRowBinding, private val db: FirebaseFirestore) : RecyclerView.ViewHolder(binding.root){
        fun bindData(id: String, playlistId: String,playlistName: String){
            db.collection("songs")
                .document(id).get()
                .addOnSuccessListener {
                    val song = it.toObject(SongModel::class.java)
                    song?.apply {
                        binding.songTitleTextView.text = title
                        binding.songSubtitleTextView.text = subtitle
                        Glide.with(binding.songCoverImageView).load(coverUrl)
                            .apply(
                                RequestOptions().transform(RoundedCorners(32)) // bo tro goc anh
                            )
                            .into(binding.songCoverImageView)
                        binding.root.setOnClickListener {
                            addToPlaylist(id, playlistId, playlistName )
                        }
                    }
                }
        }
        private fun addToPlaylist(id : String, playlistId: String ,playlistName:String) {
            if (playlistId.isNotEmpty()) {
                db.collection("playlists").document(playlistId)
                    .update("songs", FieldValue.arrayUnion(id))
                    .addOnSuccessListener {
                        // Song added to playlist
                        Log.d("AddToPlaylist", "Song added to playlist: $id")
                        val intent = Intent(binding.root.context, PlaylistDetails::class.java)
                        intent.putExtra("playlistId", playlistId)
                        intent.putExtra("playlistName", playlistName)
                        binding.root.context.startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        // Log the exception or show some feedback to the user
                        Log.e("AddToPlaylist", "Error adding song to playlist", e)
                    }
            } else {
                Log.e("AddToPlaylist", "Playlist ID is empty")
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongListAdapter.SongViewHolder {
        val binding = SongListItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongListAdapter.SongViewHolder(binding, db)
    }

    override fun getItemCount(): Int {
        return songIdList.size
    }

    override fun onBindViewHolder(holder: SongListAdapter.SongViewHolder, position: Int) {
//        holder.bindData(songIdList[position], playlistId)
        val songId = songIdList[position]
        holder.bindData(songId, playlistId,playlistName)

        holder.itemView.setOnClickListener {
            // Truyền ID của bài hát khi người dùng bấm vào
            val intent = Intent(holder.itemView.context, PlaylistDetails::class.java)
            intent.putExtra("songId", songId)
            holder.itemView.context.startActivity(intent)
        }
    }
}