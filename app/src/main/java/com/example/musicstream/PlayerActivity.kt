package com.example.musicstream

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.musicstream.databinding.ActivityPlayerBinding
import com.google.firebase.firestore.FirebaseFirestore

class PlayerActivity : AppCompatActivity() {

    lateinit var binding: ActivityPlayerBinding
    lateinit var exoPlayer: ExoPlayer
    lateinit var id_song : String

    var playerListener = object : Player.Listener{
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            showGif(isPlaying)
        }

    }

    @OptIn(UnstableApi::class) override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MyExoplayer.getCurrentSong()?.apply {
            binding.songTitleTextView.text = title
            binding.songSubtitleTextView.text = subtitle
            Glide.with(binding.songCoverImageView).load(coverUrl)
                .circleCrop()
                .into(binding.songCoverImageView)
            Glide.with(binding.songGifImageView).load(R.drawable.playing1)
                .circleCrop()
                .into(binding.songGifImageView)
            exoPlayer = MyExoplayer.getInstance()!!
            binding.playerView.player = exoPlayer
            binding.playerView.showController()
            exoPlayer.addListener(playerListener)
            id_song = id

        }

        checkFavourite(id_song)

        binding.favouriteBtn.setOnClickListener{
            insertFavorite(id_song)
        }
    }

    private fun checkFavourite( id_song: String) {
        val userId = getUserId()
        Log.d(TAG, "checkIsFavorite: Checking if book is in fav or not")
        FirebaseFirestore.getInstance().collection("favourite")
            .whereEqualTo("id_song", id_song)
            .whereEqualTo("id_user", userId)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.size() != 0){
                    binding.favouriteBtn.setImageResource(R.drawable.baseline_favorite_24)
                }
            }
    }

    private fun insertFavorite( id_song: String){
        val userId = getUserId()
        Log.d(TAG, "checkIsFavorite: Checking if book is in fav or not")
        FirebaseFirestore.getInstance().collection("favourite")
            .whereEqualTo("id_song", id_song)
            .whereEqualTo("id_user", userId)
            .get()
            .addOnSuccessListener { documents ->

                if(documents.size() != 0){
                    for (document in documents) {
                        FirebaseFirestore.getInstance().collection("favourite").document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                binding.favouriteBtn.setImageResource(R.drawable.baseline_favorite_border_24)
                                Toast.makeText(this,"Cảm ơn bạn đã từng yêu thích ",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                    e ->
                                Toast.makeText(this,"Lỗi không yêu thích",Toast.LENGTH_SHORT).show()
                                Log.w(TAG, "Error deleting document", e)
                            }
                    }
                }else{
                    val data = hashMapOf(
                        "id_song" to id_song,
                        "id_user" to userId,
                        "likes" to true
                    )

                    FirebaseFirestore.getInstance().collection("favourite")
                        .add(data)
                        .addOnSuccessListener { documentReference ->
                            binding.favouriteBtn.setImageResource(R.drawable.baseline_favorite_24)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this,"Yêu thích thất bại",Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "Yêu thích thất bại ",e)
                        }
                }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"That bai",Toast.LENGTH_SHORT).show()

            }

    }
    fun getUserId(): String?{
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPref.getString("userId", null)
    }
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.removeListener(playerListener)

    }

    fun showGif(show : Boolean){
        if (show)
            binding.songGifImageView.visibility = View.VISIBLE
        else
            binding.songGifImageView.visibility = View.INVISIBLE
    }


}