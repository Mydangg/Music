package com.example.musicstream

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.adapter.PlaylistViewAdapter
import com.example.musicstream.databinding.ActivityPlaylistBinding
import com.example.musicstream.models.Playlists
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore


class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter

    private val tempList = ArrayList<String>()
    private val list_Playlist = ArrayList<Playlists>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeBtn.setOnClickListener {
            startActivity(Intent(this@PlaylistActivity, MainActivity::class.java))
        }
//        binding.playlistBtn.setOnClickListener {
//            startActivity(Intent(this@PlaylistActivity, PlaylistActivity::class.java))
//        }
        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this@PlaylistActivity, FavouriteActivity::class.java))
        }
        binding.searchBtn.setOnClickListener {
            startActivity(Intent(this@PlaylistActivity, SearchActivity::class.java))
        }

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this@PlaylistActivity,2)
        adapter = PlaylistViewAdapter(
            context = this,
            list = list_Playlist,
            tempList = tempList
        )
        binding.playlistRV.adapter = adapter


        binding.addPlaylistBtn.setOnClickListener {
            customAlertDialog()
        }
        val userId = getUserId()
        db.collection("playlists").whereEqualTo("userId", userId).get().addOnSuccessListener { result ->
            list_Playlist.clear()
            for (document in result) {
                val playlistName = document.getString("playlistName")
                val playlistId = document.getString("playlistId")
                val playlists = Playlists(
                        playlistId = playlistId,
                        playlistName = playlistName
                    )

                list_Playlist.add(playlists)
            }
            adapter.notifyDataSetChanged()
        }

    }

    fun getUserId(): String?{
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPref.getString("userId", null)
    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(this@PlaylistActivity).inflate(R.layout.add_playlist_dialog, binding.root, false) as View
        val builder = MaterialAlertDialogBuilder(this)

        val playlistNameInput = customDialog.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.playlistName)
        val yourNameInput = customDialog.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.yourName)
        val addButton = customDialog.findViewById<Button>(R.id.addButton)

        val alertDialog = builder.setView(customDialog)
            .setTitle("Playlist Details")
            .show()

        addButton.setOnClickListener {
            val playlistName = playlistNameInput.text.toString()
            val yourName = yourNameInput.text.toString()
            val userId = getUserId()
            val data = hashMapOf(
                "playlistName" to playlistName,
                "yourName" to yourName,
                "userId" to userId
            )

            db.collection("playlists").add(data).addOnSuccessListener { documentReference ->
                val docId = documentReference.id
                documentReference.update("playlistId", docId)
                val new_playlist = Playlists(
                    playlistId = docId,
                    playlistName = playlistName
                )
                list_Playlist.add(new_playlist)
                adapter.notifyDataSetChanged()
                Log.d("docId", docId.toString())
                alertDialog.dismiss() // thoat dialog
            }

        }


    }

    override fun onResume() {
        super.onResume()
        showPlayerView()
    }
    private fun showPlayerView(){
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