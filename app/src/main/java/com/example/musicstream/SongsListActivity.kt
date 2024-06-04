package com.example.musicstream

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.adapter.SongsListAdapter
import com.example.musicstream.databinding.ActivitySongsListBinding
import com.example.musicstream.models.CategoryModel

class SongsListActivity : AppCompatActivity() {

    companion object{
        lateinit var category : CategoryModel
    }

    lateinit var binding: ActivitySongsListBinding
    lateinit var songsListAdapter: SongsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeBtn.setOnClickListener {
            startActivity(Intent(this@SongsListActivity, MainActivity::class.java))
        }
        binding.playlistBtn.setOnClickListener {
            startActivity(Intent(this@SongsListActivity, PlaylistActivity::class.java))
        }
        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this@SongsListActivity, FavouriteActivity::class.java))
        }
        binding.searchBtn.setOnClickListener {
            startActivity(Intent(this@SongsListActivity, SearchActivity::class.java))
        }

        binding.nameTextView.text = category.name

        Glide.with(binding.coverImageView).load(category.coverUrl)
            .apply(
                RequestOptions().transform(RoundedCorners(32)) // bo tro goc anh
            )
            .into(binding.coverImageView)

        setupSongsListRecyclerView()
    }

    fun setupSongsListRecyclerView(){
        songsListAdapter = SongsListAdapter(category.songs)
        binding.songsListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.songsListRecyclerView.adapter = songsListAdapter
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