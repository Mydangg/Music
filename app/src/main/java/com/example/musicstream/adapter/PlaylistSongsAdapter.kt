package com.example.musicstream.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.MyExoplayer
import com.example.musicstream.PlayerActivity
import com.example.musicstream.R
import com.example.musicstream.databinding.SongListItemRecyclerRowBinding
import com.example.musicstream.models.SongModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PlaylistSongsAdapter(private var songs: MutableList<SongModel>, private val playlistId: String) : RecyclerView.Adapter<PlaylistSongsAdapter.SongViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class SongViewHolder(private val binding: SongListItemRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: SongModel) {
            binding.songTitleTextView.text = song.title
            binding.songSubtitleTextView.text = song.subtitle
            Glide.with(binding.songCoverImageView).load(song.coverUrl)
                .apply(RequestOptions().transform(RoundedCorners(32)))
                .into(binding.songCoverImageView)

            binding.root.setOnClickListener {
                MyExoplayer.startPlaying(binding.root.context, song)
                val intent = Intent(binding.root.context, PlayerActivity::class.java)
                binding.root.context.startActivity(intent)
            }

            binding.option.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.menu_vert_icon, popup.menu)

                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.delete -> {
                            db.collection("playlists").document(playlistId)
                                .update("songs", FieldValue.arrayRemove(song.id))
                                .addOnSuccessListener {
                                    Log.d("RemoveSong", "bai hat da duoc xoa")
                                    val position = adapterPosition
                                    if (position != RecyclerView.NO_POSITION) {
                                        songs.removeAt(position)
                                        notifyItemRemoved(position)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("RemoveSong", "loi", e)
                                }
                            true
                        }
                        else -> false
                    }
                }

                popup.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = SongListItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }
}
