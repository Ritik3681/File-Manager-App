package com.example.filemanager

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categorizedImages: Map<String, List<Uri>>,
    private val layoutType:Boolean,
    private val onClick: (String) -> Unit,


) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = if (layoutType==false){
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
        }else{
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category_new, parent, false)
        }
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryName = categorizedImages.keys.toList()[position]
        val imageList = categorizedImages[categoryName] ?: emptyList()
        holder.bind(categoryName, imageList)
    }

    override fun getItemCount(): Int = categorizedImages.size
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCategoryImage: ImageView = itemView.findViewById(R.id.ivCategoryImage)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val tvImageCount: TextView = itemView.findViewById(R.id.tvImageCount)

        fun bind(categoryName: String, imageList: List<Uri>) {
            // Display the first image of the category
            if (imageList.isNotEmpty()) {
                ivCategoryImage.setImageURI(imageList[0])
            } else {
                ivCategoryImage.setImageResource(R.drawable.ic_launcher_foreground) // Fallback
            }

            // Set the category name and image count
            tvCategoryName.text = categoryName
            tvImageCount.text = " ${imageList.size}"

            // Handle click
            itemView.setOnClickListener { onClick(categoryName) }
        }
    }

}

