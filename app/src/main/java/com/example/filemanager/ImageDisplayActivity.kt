package com.example.filemanager

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ImageDisplayActivity : AppCompatActivity() {

    private var selectedUris: Set<Uri> = emptySet()
    private lateinit var adapter: ImageAdapter // Class-level adapter
    private var imageList: MutableList<Uri> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        val text = findViewById<TextView>(R.id.text)
        val backPress = findViewById<ImageView>(R.id.backPress)
        backPress.setOnClickListener {
            onBackPressed()
        }

        val rvImages = findViewById<RecyclerView>(R.id.recyclerview)
        rvImages.layoutManager = GridLayoutManager(this, 3)


        val categoryName = intent.getStringExtra("categoryName") ?: return
        imageList = fetchImagesForCategory(categoryName)
        text.text = categoryName


        adapter = ImageAdapter(imageList, false) { selectedCount ->
            this.selectedUris = selectedCount
            findViewById<TextView>(R.id.select_item_count).text = "${selectedCount.size} selected"
        }

        rvImages.adapter = adapter

        val selectMultipelPhoto = findViewById<ImageView>(R.id.openCheckBox)
        val header2 = findViewById<LinearLayout>(R.id.header_text1)
        val header = findViewById<ConstraintLayout>(R.id.header_text)
        val bottomSheet = findViewById<ConstraintLayout>(R.id.bottomSheet)
        val cancel = findViewById<TextView>(R.id.cancle)
        val selectAll = findViewById<TextView>(R.id.select_All)
        val selectCount = findViewById<TextView>(R.id.select_item_count)
        val shareButton = findViewById<LinearLayout>(R.id.share)
        val deleteButton = findViewById<LinearLayout>(R.id.delet)

        selectMultipelPhoto.setOnClickListener {
            header2.visibility = View.VISIBLE
            bottomSheet.visibility = View.VISIBLE
            header.visibility = View.GONE
            adapter = ImageAdapter(imageList, true) { selectedCount ->
                this.selectedUris = selectedCount
                val countItem = selectedCount.size
                selectCount.text = "$countItem selected"
            }
            rvImages.adapter = adapter
        }

        cancel.setOnClickListener {
            adapter.clearAllSelections()
            header2.visibility = View.GONE
            header.visibility = View.VISIBLE
            bottomSheet.visibility = View.GONE
            selectCount.text = "Select items"
            adapter = ImageAdapter(imageList, false) { }
            rvImages.adapter = adapter
        }

        selectAll.setOnClickListener {
            adapter.selectAll()
            selectCount.text = "Selected: ${adapter.getSelectedCount()}"
        }

        shareButton.setOnClickListener {
            if (selectedUris.isNotEmpty()) {
                shareItems(selectedUris)
            }
        }

        deleteButton.setOnClickListener {
            if (selectedUris.isNotEmpty()) {
                showDeleteDialog(selectedUris)
            }
        }
    }

    private fun deleteItemsPermanently(selectedItems: Set<Uri>) {
        for (uri in selectedItems) {
            try {
                val contentResolver = applicationContext.contentResolver
                val deletedRows = contentResolver.delete(uri, null, null)
                if (deletedRows > 0) {
                    Log.d("Delete", "Successfully deleted: $uri")
                } else {
                    Log.d("Delete", "Failed to delete: $uri")
                }
            } catch (e: Exception) {
                Log.e("Delete", "Error deleting item: $uri", e)
            }
        }

        imageList.removeAll(selectedItems.toList())
        val count =findViewById<TextView>(R.id.select_item_count)
        count.text="Select items"
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "${selectedItems.size} items deleted", Toast.LENGTH_SHORT).show()
    }

    private fun fetchImagesForCategory(categoryName: String): MutableList<Uri> {
        val imageList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(categoryName)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val imageUri = Uri.withAppendedPath(uri, id.toString())
                imageList.add(imageUri)
            }
        }
        return imageList
    }

    private fun shareItems(selectedItems: Set<Uri>) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            val uris = ArrayList<Uri>(selectedItems)
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        }
        startActivity(Intent.createChooser(shareIntent, "Share images"))
    }
    private fun showDeleteDialog(selectedItems: Set<Uri>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_for_delete, null)

        val dialog = Dialog(this).apply {
            setContentView(dialogView)
            setCancelable(false)
        }

        val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_delete)
        val confirmButton = dialogView.findViewById<TextView>(R.id.delete_button)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        confirmButton.setOnClickListener {
            val count =findViewById<TextView>(R.id.select_item_count)
            count.text="Select items"
            deleteItemsPermanently(selectedItems)
            dialog.dismiss()
        }

        dialog.show()
    }





}


