package com.example.filemanager

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(
    private val imageUris: List<Uri>,
    private val checkBoxVisibility: Boolean,
    private val onSelectionChanged: (MutableSet<Uri>) -> Unit,
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val selectedItems = mutableSetOf<Uri>()

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(imageUri: Uri) {
            Glide.with(imageView.context)
                .load(imageUri)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView)

            checkBox.visibility = if (checkBoxVisibility) View.VISIBLE else View.GONE
            checkBox.isChecked = selectedItems.contains(imageUri)

            // Handle checkbox selection changes
            checkBox.setOnCheckedChangeListener(null)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(imageUri)
                } else {
                    selectedItems.remove(imageUri)
                }
                onSelectionChanged(selectedItems)
            }

            // Handle image click for suggestions
            imageView.setOnClickListener {
                openImageWithSuggestions(itemView.context, imageUri)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUris[position])
    }

    override fun getItemCount(): Int = imageUris.size

    fun selectAll() {
        selectedItems.clear()
        selectedItems.addAll(imageUris)
        notifyItemRangeChanged(0, imageUris.size)
        onSelectionChanged(selectedItems)
    }

    fun getSelectedCount(): Int = selectedItems.size

    fun clearAllSelections() {
        selectedItems.clear()
        notifyDataSetChanged()
        onSelectionChanged(selectedItems)
    }

    fun getSelectedItems(): Set<Uri> = selectedItems

    private fun openImageWithSuggestions(context: android.content.Context, imageUri: Uri) {
        try {
            // Intent for viewing the image
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                data = imageUri
                type = "image/*" // Ensures only image-viewing apps are shown
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant permission to access URI
            }

            // Create a chooser to show suggestion items
            val chooserIntent = Intent.createChooser(viewIntent, "Open image with")

            // Start the chooser
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            Log.e("ImageAdapter", "Error opening image: ${e.message}")
        }
    }

}
