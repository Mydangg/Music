package com.example.musicstream

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.adapter.SongsListAdapter
import com.example.musicstream.databinding.ActivitySearchBinding
import com.example.musicstream.models.SongModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeBtn.setOnClickListener {
            startActivity(Intent(this@SearchActivity, MainActivity::class.java))
        }
        binding.playlistBtn.setOnClickListener {
            startActivity(Intent(this@SearchActivity, PlaylistActivity::class.java))
        }
        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this@SearchActivity, FavouriteActivity::class.java))
        }
//        binding.searchBtn.setOnClickListener {
//            startActivity(Intent(this@SearchActivity, SearchActivity::class.java))
//        }



        binding.searchSongName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchSongInFirestore(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


    }


    private fun searchSongInFirestore(songName: String) {
        val foundSongs = mutableListOf<SongModel>()

        db.collection("songs")
            .orderBy("title").startAt(songName).endAt(songName+"\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val song = document.toObject(SongModel::class.java)
                    song?.let { foundSongs.add(it) }
                }
                if (foundSongs.isEmpty()) {
                    binding.songsListRecyclerView.visibility = View.GONE
                    Toast.makeText(this, "Không tìm thấy  bài hát", Toast.LENGTH_SHORT).show()
                } else {
                    binding.songsListRecyclerView.visibility = View.VISIBLE
                    val songsListAdapter = SongsListAdapter(foundSongs.map { it.id })
                    binding.songsListRecyclerView.layoutManager = LinearLayoutManager(this)
                    binding.songsListRecyclerView.adapter = songsListAdapter
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
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