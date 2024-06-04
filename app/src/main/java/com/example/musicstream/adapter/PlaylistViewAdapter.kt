package com.example.musicstream.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.musicstream.PlaylistActivity
import com.example.musicstream.PlaylistDetails
import com.example.musicstream.SongList
import com.example.musicstream.databinding.PlaylistViewBinding
import com.example.musicstream.models.Playlists
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore


class PlaylistViewAdapter(
    private val  context: Context,
    private var list:ArrayList<Playlists>,
    private var tempList: ArrayList<String>
) : RecyclerView.Adapter<PlaylistViewAdapter.MyHolder>() {
    class MyHolder(binding: PlaylistViewBinding) :RecyclerView.ViewHolder(binding.root){
        val image = binding.playlistImg
        val name = binding.playlistName
        val deleteBtn = binding.playlistDeleteBtn
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
//        return playlistList.size
        return list.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
//        holder.name.text = playlistList[position]
        holder.name.text = list[position].playlistName.toString()
        holder.name.isSelected = true

        holder.deleteBtn.setOnClickListener {
            val playlistId = list[position].playlistId // Lấy ID của tài liệu từ tempList
            val db = FirebaseFirestore.getInstance()

            // Xóa tài liệu từ Firestore
            if (playlistId != null) {
                db.collection("playlists").document(playlistId).delete()
                    .addOnSuccessListener {
                        // Xóa ID khỏi tempList
                        list.removeAt(holder.adapterPosition)
                        // Thông báo cho Adapter là có sự thay đổi dữ liệu
                        notifyItemRemoved(holder.adapterPosition)
                    }
                    .addOnFailureListener { e ->
                        // Xử lý nếu việc xóa thất bại
                        Log.e(TAG, "Error deleting document", e)
                    }
            }


        }

        holder.root.setOnClickListener {
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index", position)
//            intent.putExtra("playlistId", playlistID[position])
//            intent.putExtra("playlistName", playlistList[position])
            intent.putExtra("playlistId", list[position].playlistId)
            intent.putExtra("playlistName", list[position].playlistName)
//            Log.d("testing",playlistList.toString())
            ContextCompat.startActivity(context,intent, null)
        }
//        holder.root.setOnClickListener {
//            // Tạo Intent và truyền playlistId vào đó
//            val playlistId = tempList[position] // Lấy playlistId từ tempList
//            val intent = Intent(context, SongList::class.java)
//            intent.putExtra("playlistId", playlistId)
//            context.startActivity(intent)
//        }


    }
}