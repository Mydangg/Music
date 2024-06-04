package com.example.musicstream

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.adapter.PlaylistSongsAdapter
import com.example.musicstream.adapter.SongListAdapter
import com.example.musicstream.adapter.SongsListAdapter
import com.example.musicstream.databinding.ActivityPlaylistDetailsBinding
import com.example.musicstream.models.SongModel
import com.google.firebase.firestore.FirebaseFirestore

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDetailsBinding
    private val db = FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val playlistName = intent.getStringExtra("playlistName")
        binding.moreInfoPD.text = playlistName

        val playlistId = intent.getStringExtra("playlistId") ?: ""


        loadPlaylistSongs(playlistId)

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        binding.addBtnPD.setOnClickListener {
            val playlistId = intent.getStringExtra("playlistId")
            val playlistName = intent.getStringExtra("playlistName")
            val songId = intent.getStringExtra("songId")
            Log.d("testing", playlistId.toString())
            val intent = Intent(this, SongList::class.java)
            intent.putExtra("playlistId", playlistId)
            intent.putExtra("playlistName", playlistName)
            intent.putExtra("songId", songId)
            startActivity(intent)
        }
        binding.removeAllPD.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Xoá tất cả bài hát")
                .setMessage("Bạn muốn xóa tất cả các bài hát có trong playlist ?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create()

            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE)
            }

            alertDialog.show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val playlistId = intent.getStringExtra("playlistId")
                if (playlistId != null) {
                    db.collection("playlists").document(playlistId)
                        .update("songs", emptyList<String>())
                        .addOnSuccessListener {
                            Log.d("RemoveAllSongs", "xoa thanh cong")
                            loadPlaylistSongs(playlistId)

                        }
                        .addOnFailureListener { e ->
                            Log.e("RemoveAllSongs", "loi", e)
                        }
                }
                alertDialog.dismiss()
            }

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                alertDialog.dismiss()
            }
        }


    }

    private fun loadPlaylistSongs(playlistId: String) {
        if (playlistId.isNotEmpty()) {
            db.collection("playlists").document(playlistId)
                .get()
                .addOnSuccessListener { document ->
                    val songIdList = document.get("songs") as? List<String>
                    songIdList?.let {
                        Log.d("LoadPlaylistSongs", "Song IDs retrieved: $it")
                        val songs = mutableListOf<SongModel>()
                        for (id in it) {
                            db.collection("songs").document(id)
                                .get()
                                .addOnSuccessListener { songDocument ->
                                    val song = songDocument.toObject(SongModel::class.java)
                                    song?.let { s ->
                                        songs.add(s)
                                        if (songs.size == it.size) {
                                            val songsListAdapter = PlaylistSongsAdapter(songs, playlistId)
                                            binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
                                            binding.playlistDetailsRV.adapter = songsListAdapter


                                        }
                                    }
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LoadPlaylistSongs", "Error retrieving songs", e)
                }
        } else {

        }
    }

    override fun onResume() {
        super.onResume()
        showPlayerView()
    }
    fun showPlayerView(){
        binding.playerView.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        MyExoplayer.getCurrentSong()?.let {
            binding.playerView.visibility = View.VISIBLE
            binding.songTitleTextView.text = it.title
            Glide.with(binding.songCoverImageView).load(it.coverUrl)
                .apply(
                    RequestOptions().transform(RoundedCorners(32)) // bo tro goc anh
                ).into(binding.songCoverImageView)
        } ?: run{
            binding.playerView.visibility = View.GONE
        }
    }
}
