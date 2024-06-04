package com.example.musicstream

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicstream.adapter.SongListAdapter
import com.example.musicstream.adapter.SongsListAdapter
import com.example.musicstream.databinding.ActivitySongListBinding
import com.google.firebase.firestore.FirebaseFirestore

class SongList : AppCompatActivity() {
    lateinit var binding: ActivitySongListBinding
    private val db = FirebaseFirestore.getInstance()
    private val songIdList = mutableListOf<String>()
    private lateinit var adapter: SongListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val playlistName  = intent.getStringExtra("playlistName")


        val playlistId = intent.getStringExtra("playlistId") ?: ""
        adapter = SongListAdapter(songIdList, playlistId,playlistName.toString())

        binding.songListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.songListRecyclerView.adapter = adapter

        db.collection("songs")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    songIdList.add(document.id)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Log the exception or show some feedback to the user
            }


    }

}