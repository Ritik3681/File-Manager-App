package com.example.filemanager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AudioAdapter(
    private val context: Context,
    private val audioList: List<AudioData>,
) : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {
    var selectionMode: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audio = audioList[position]
        holder.bind(audio,context,selectionMode)

    }

    override fun getItemCount(): Int = audioList.size

    class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.audioTitle)
        private val audioSize: TextView = itemView.findViewById(R.id.audioSize)
        private val audioDate: TextView = itemView.findViewById(R.id.date_of_audio)
        private val checkbox:CheckBox=itemView.findViewById(R.id.checkbox)

        fun bind(audio: AudioData,context: Context,selectionMode:Boolean) {
            titleTextView.text = audio.title

            val audioSizeInMB = audio.size / (1024.0 * 1024.0)
            audioSize.text = String.format("%.2f MB", audioSizeInMB)

            audioDate.text = formatDate(audio.dateAdded)

            itemView.setOnClickListener {
                openAudioFile(audio.uri, context)
            }
            checkbox.visibility = if (selectionMode) View.VISIBLE
            else{
                View.GONE
            }


        }

        private fun formatDate(timestamp: Long): String {
            val date = java.util.Date(timestamp * 1000)
            val format = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())
            return format.format(date)
        }
        private fun openAudioFile(uri: Uri, context: Context) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "audio/*") // Specify the MIME type as audio
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(intent, "Open audio file with:"))
        }
    }
}
