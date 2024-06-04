package com.example.musicstream

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.adapter.CategoryAdapter
import com.example.musicstream.adapter.SectionSongListAdapter
import com.example.musicstream.databinding.ActivityMainBinding
import com.example.musicstream.models.CategoryModel
import com.example.musicstream.models.SongModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var categoryAdapter: CategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCategories()
        setupSection(
            "section_1",
            binding.section1MainLayout,
            binding.section1Title,
            binding.section1RecyclerView
        )
        setupSection(
            "section_2",
            binding.section2MainLayout,
            binding.section2Title,
            binding.section2RecyclerView
        )
        setupSection(
            "section_3",
            binding.section3MainLayout,
            binding.section3Title,
            binding.section3RecyclerView
        )
        setupSection(
            "section_4",
            binding.section4MainLayout,
            binding.section4Title,
            binding.section4RecyclerView
        )
        setupMostlyPlayed(
            "mostly_player",
            binding.mostlyPlayedMainLayout,
            binding.mostlyPlayedTitle,
            binding.mostlyPlayedRecyclerView
        )

        binding.optionBtn.setOnClickListener {
            showPopupMenu()
        }




//        binding.homeBtn.setOnClickListener {
//            startActivity(Intent(this@MainActivity, MainActivity::class.java))
//        }
        binding.playlistBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlaylistActivity::class.java))

        }
        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavouriteActivity::class.java))
        }
        binding.searchBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }

        ActionViewFilper();

    }

    fun showPopupMenu(){
        val popupMenu = PopupMenu(this,binding.optionBtn)
        val inflator = popupMenu.menuInflater
        inflator.inflate(R.menu.option_menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.logout -> {
                    logout()
                    true
                }
            }
            false
        }
    }
//hello
    fun logout(){
        MyExoplayer.getInstance()?.release()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
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

//Categories
    fun getCategories(){
        FirebaseFirestore.getInstance().collection("category")
            .get().addOnSuccessListener {
                val categoryList = it.toObjects(CategoryModel::class.java)
                setupCategoryRecyclerView(categoryList)
            }
    }

    fun setupCategoryRecyclerView(categoryList : List<CategoryModel>){
        categoryAdapter = CategoryAdapter(categoryList)
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,false)
        binding.categoriesRecyclerView.adapter = categoryAdapter
    }

    //Sections
    fun setupSection(id : String, mainLayout : RelativeLayout, titleView : TextView, recyclerView: RecyclerView){
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener {
                val section = it.toObject(CategoryModel::class.java)
                section?.apply {
                    mainLayout.visibility = View.VISIBLE
                    titleView.text = name
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                    recyclerView.adapter = SectionSongListAdapter(songs)
                    mainLayout.setOnClickListener {
                        SongsListActivity.category = section
                        startActivity(Intent(this@MainActivity, SongsListActivity::class.java))
                    }
                }
            }
    }

    fun setupMostlyPlayed(id : String, mainLayout : RelativeLayout, titleView : TextView, recyclerView: RecyclerView){
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener {
                //get most played songs
                FirebaseFirestore.getInstance().collection("songs")
                    .orderBy("count", Query.Direction.DESCENDING)
                    .limit(5)
                    .get().addOnSuccessListener {songListSnapshot-> // lay id bai hat co luot nghe cao nhat
                        val songModelList = songListSnapshot.toObjects<SongModel>()
                        val songIdList = songModelList.map {
                            it.id
                        }.toList()
                        val section = it.toObject(CategoryModel::class.java)
                        section?.apply {
                            section.songs = songIdList
                            mainLayout.visibility = View.VISIBLE
                            titleView.text = name
                            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                            recyclerView.adapter = SectionSongListAdapter(songs)
                            mainLayout.setOnClickListener {
                                SongsListActivity.category = section
                                startActivity(Intent(this@MainActivity, SongsListActivity::class.java))
                            }
                        }
                    }

            }
    }

    private fun ActionViewFilper() {
        Log.d("MyFragment", "ActionViewFilper() called")

        val slide_in: Animation = AnimationUtils.loadAnimation(this , R.anim.slider_home_in_right)
        val slide_out: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_home_out_left)
        binding.viewLipper.inAnimation = slide_in
        binding.viewLipper.outAnimation = slide_out

        val quangcao: ArrayList<Int> = arrayListOf(
            R.drawable.slide1,
            R.drawable.slide2,
            R.drawable.slide3,
            R.drawable.slide4,
            R.drawable.slide5,
            R.drawable.slide6

        )

        for (drawableId in quangcao) {
            val imageView = ImageView(applicationContext)
            val drawable = ContextCompat.getDrawable(this, drawableId)
            imageView.setImageDrawable(drawable)

            imageView.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            imageView.scaleType = ImageView.ScaleType.FIT_XY

            binding.viewLipper.addView(imageView)
        }
        binding.viewLipper.flipInterval = 3000
        binding.viewLipper.isAutoStart = true
    }
}

