package com.example.filemanager

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class VideoDisplayActivity : AppCompatActivity()
{

    private lateinit var videoRecyclerView: RecyclerView
    private val videoList = mutableListOf<VideoData>()
    private lateinit var adapter: VideoAdapter
    private var layoutType=true
   private var checkBoxVissibility=false
    private var selectedUris: Set<Uri> = emptySet()
    // Correct type here


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vedio_display)
        val count_Select_Item=findViewById<TextView>(R.id.select_item_count)

        videoRecyclerView = findViewById(R.id.recyclerview)
        videoRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter=VideoAdapter(this, videoList,layoutType,checkBoxVissibility){selectedItemCount ->
            this.selectedUris=selectedItemCount
            count_Select_Item.text = "Selected: ${selectedItemCount.size}"
        }
        videoRecyclerView.adapter = adapter

        var layoutChange=findViewById<ImageView>(R.id.layoutChange)
        layoutChange.setOnClickListener{

            if (layoutType){
                layoutChange.setImageResource(R.drawable.image2)
                videoRecyclerView.layoutManager = GridLayoutManager(this,4)
                adapter=VideoAdapter(this, videoList, layoutType,checkBoxVissibility){

                }
                videoRecyclerView.adapter = adapter

            }
            else{
                layoutChange.setImageResource(R.drawable.image4)

                videoRecyclerView.layoutManager = LinearLayoutManager(this)
                adapter=VideoAdapter(this, videoList, layoutType,checkBoxVissibility){

                }
                videoRecyclerView.adapter = adapter

            }
            layoutType=!layoutType
            adapter=VideoAdapter(this, videoList, layoutType,checkBoxVissibility){

            }
            videoRecyclerView.adapter = adapter

        }
        val openCheckBox=findViewById<ImageView>(R.id.openCheckBox)
        val header=findViewById<ConstraintLayout>(R.id.header_text)
        val header1=findViewById<LinearLayout>(R.id.header_text1)
        val bottomSheet=findViewById<ConstraintLayout>(R.id.bottomSheet)
        val cancleButton=findViewById<TextView>(R.id.cancle)
        val SelectAllItem=findViewById<TextView>(R.id.select_All)


        openCheckBox.setOnClickListener{
            header.visibility=View.GONE
            header1.visibility=View.VISIBLE
            bottomSheet.visibility=View.VISIBLE
            checkBoxVissibility=true
            adapter.updateCheckBoxVisibility(true)
            adapter.notifyDataSetChanged()
        }
        cancleButton.setOnClickListener{
            header.visibility=View.VISIBLE
            header1.visibility=View.GONE
            bottomSheet.visibility=View.GONE
            checkBoxVissibility=false
            adapter.updateCheckBoxVisibility(false)
            adapter.clearSelectionsAndHideCheckBoxes()
            adapter.notifyDataSetChanged()
            count_Select_Item.text=" Select items"
        }
        SelectAllItem.setOnClickListener{
            adapter.selectAllItems()
             //   count_Select_Item.text="Selected: ${adapter.getSelectedItemCount1()}"

        }
        val shareItems=findViewById<LinearLayout>(R.id.share)
        shareItems.setOnClickListener{
            if (selectedUris.isNotEmpty()) {
                if(selectedUris.size>10){
                    shareItems(selectedUris)
                }
                else{
                    Toast.makeText(this, "Select less than 10 for shareing", Toast.LENGTH_SHORT).show()
                }
                
            }
            else{
                Toast.makeText(this, "No item is selecting", Toast.LENGTH_SHORT).show()

            }
        }
        val deleteButton=findViewById<LinearLayout>(R.id.delet)
        deleteButton.setOnClickListener {
            if (selectedUris.isNotEmpty()) {
                showDeleteDialog(selectedUris)
            }
        }





        if (arePermissionsGranted()) {
            fetchVideos()
        } else {
            requestPermissions()
        }
    }

    private fun arePermissionsGranted(): Boolean {
        val readMediaVideoPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return readMediaVideoPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                REQUEST_PERMISSION_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchVideos()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchVideos() {
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED
        )

        val cursor = contentResolver.query(
            videoUri,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Video.Media._ID)
            val titleColumn = it.getColumnIndex(MediaStore.Video.Media.TITLE)
            val durationColumn = it.getColumnIndex(MediaStore.Video.Media.DURATION)
            val sizeColumn = it.getColumnIndex(MediaStore.Video.Media.SIZE)
            val dataColumn = it.getColumnIndex(MediaStore.Video.Media.DATA)
            val dateColumn=it.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val duration = it.getLong(durationColumn)
                val size = it.getLong(sizeColumn)
                val data = it.getString(dataColumn)
                val date=it.getLong(dateColumn)
                val uri = Uri.parse(data)
                val thumbnails = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id.toString())

                videoList.add(VideoData(uri, title, duration, size,thumbnails,date))
            }

            videoRecyclerView.adapter?.notifyDataSetChanged()
        }
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

    companion object {
        const val REQUEST_PERMISSION_CODE = 1001
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

        //imageList.removeAll(selectedItems.toList())
        val count =findViewById<TextView>(R.id.select_item_count)
        count.text="Select items"
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "${selectedItems.size} items deleted", Toast.LENGTH_SHORT).show()
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
