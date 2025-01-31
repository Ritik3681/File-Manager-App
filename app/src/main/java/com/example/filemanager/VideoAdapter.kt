package com.example.filemanager

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoAdapter(
    private val context: Context,
    private val videoList: List<VideoData>,
    private val layoutType: Boolean,
    private var checkBoxVisibility: Boolean,
    private val onSelectedItemCountChanged: (Set<Uri>) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private val selectedItems = mutableSetOf<Int>()

    private val selectedUris = mutableSetOf<Uri>()
    private val selectionState = mutableMapOf<Uri, Boolean>()


    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.videoThumbnail)
        val title: TextView = view.findViewById(R.id.videoTitle)
        val duration: TextView = view.findViewById(R.id.videoDuration)
        val videoSize: TextView = view.findViewById(R.id.videoSize)
        val date: TextView = view.findViewById(R.id.date)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)

        fun bind(
            video: VideoData,
            context: Context,
            checkBoxVisibility: Boolean,
            isSelected: Boolean,
            onCheckboxClicked: (Boolean) -> Unit
        ) {
            val thumbnailBitmap = getVideoThumbnail(context, video.uri)
            if (thumbnailBitmap != null) {
                thumbnail.setImageBitmap(thumbnailBitmap)
            } else {
                Glide.with(context)
                    .load(video.thumbnails)
                    .frame(1000000)
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(thumbnail)
            }

            title.text = video.name
            duration.text = formatDuration(video.duration)
            val videoSizeInMB = video.size / (1024.0 * 1024.0)
            videoSize.text = String.format("%.2f MB", videoSizeInMB)
            date.text = formatDate(video.date)

            checkBox.visibility = if (checkBoxVisibility) View.VISIBLE else View.GONE
            checkBox.isChecked = isSelected

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckboxClicked(isChecked)
            }
        }

        private fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
            return try {
                val id = ContentUris.parseId(uri)
                MediaStore.Video.Thumbnails.getThumbnail(
                    context.contentResolver,
                    id,
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun formatDuration(durationMillis: Long): String {
            val seconds = (durationMillis / 1000) % 60
            val minutes = (durationMillis / (1000 * 60)) % 60
            val hours = durationMillis / (1000 * 60 * 60)

            return if (hours > 0)
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else
                String.format("%02d:%02d", minutes, seconds)
        }

        private fun formatDate(timestamp: Long): String {
            val date = java.util.Date(timestamp * 1000)
            val format = java.text.SimpleDateFormat("dd MMM ", java.util.Locale.getDefault())
            return format.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = if (!layoutType) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_vedio2, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_vedio, parent, false)
        }
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videoList[position]
        val uri=video.uri
        val isSelected = selectedUris.contains(uri)



        holder.bind(video, context, checkBoxVisibility, isSelected) { isChecked ->
            if (isChecked) {
               selectedUris.add(uri)
                selectionState[uri]=false
            } else {
                selectedUris.remove(uri)
                selectionState[uri]=true

            }
            onSelectedItemCountChanged(selectedUris)
        }
    }

    override fun getItemCount(): Int = videoList.size

    fun updateCheckBoxVisibility(visible: Boolean) {
        checkBoxVisibility = visible
        notifyDataSetChanged()
    }

    fun getSelectedItemCount(): Int = selectedItems.size

    fun clearSelectionsAndHideCheckBoxes() {
        selectedItems.clear()
        checkBoxVisibility = false
        notifyDataSetChanged()
    }

    fun selectAllItems() {
        selectedUris.clear()
        for (video in videoList) {
            selectedUris.add(video.uri)
        }
        notifyDataSetChanged()
        onSelectedItemCountChanged(selectedUris)
    }
    fun getSelectedItemCount1(): Int {
        return selectedUris.size
    }

//    fun clearAllSelections() {
//        selectedItems.clear()
//        notifyDataSetChanged()
//        onSelectedItemCountChanged(selectedItems.size)
//    }
}

