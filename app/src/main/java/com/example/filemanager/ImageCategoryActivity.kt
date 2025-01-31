package com.example.filemanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class ImageCategoryActivity : AppCompatActivity() {
    private lateinit var backPress:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_image_category)
        val rvCategories = findViewById<RecyclerView>(R.id.categoryRecyclerView)

        backPress=findViewById(R.id.backPress)
        backPress.setOnClickListener{
            onBackPressed()
        }
        val layoutChange=findViewById<ImageView>(R.id.layoutChange)
        var isIconClicked = false

        layoutChange.setOnClickListener {
            // Toggle the state and set the corresponding icon
            if (isIconClicked) {
                // If the icon is already clicked, revert to the default icon
                layoutChange.setImageResource(R.drawable.image4)
                rvCategories.layoutManager=LinearLayoutManager(this)
                val categorizedImages = fetchImagesCategorized()
                val adapter = CategoryAdapter(categorizedImages,true) { categoryName ->
                    val intent = Intent(this, ImageDisplayActivity::class.java)
                    intent.putExtra("categoryName", categoryName)
                    startActivity(intent)
                }
                rvCategories.adapter = adapter

            } else {
                // If the icon is not clicked, change to the clicked icon
                layoutChange.setImageResource(R.drawable.image2)
                rvCategories.layoutManager = GridLayoutManager(this, 3)
                val categorizedImages = fetchImagesCategorized()
                val adapter = CategoryAdapter(categorizedImages,false) { categoryName ->
                    val intent = Intent(this, ImageDisplayActivity::class.java)
                    intent.putExtra("categoryName", categoryName)
                    startActivity(intent)
                }
                rvCategories.adapter = adapter
            }

            // Toggle the state
            isIconClicked = !isIconClicked

        }

        rvCategories.layoutManager = GridLayoutManager(this, 3)
        val categorizedImages = fetchImagesCategorized()
        val adapter = CategoryAdapter(categorizedImages,false) { categoryName ->
            val intent = Intent(this, ImageDisplayActivity::class.java)
            intent.putExtra("categoryName", categoryName)
            startActivity(intent)
        }
        rvCategories.adapter = adapter


        // Use GridLayoutManager with spanCount of 2




    }

    private fun fetchImagesCategorized(): Map<String, List<Uri>> {
        val imageMap = mutableMapOf<String, MutableList<Uri>>()
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(uri, projection, null, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val bucketName = cursor.getString(bucketColumn) ?: "Unknown"
                val imageUri = Uri.withAppendedPath(uri, id.toString())

                if (!imageMap.containsKey(bucketName)) {
                    imageMap[bucketName] = mutableListOf()
                }
                imageMap[bucketName]?.add(imageUri)
            }
        }
        return imageMap
    }
}




