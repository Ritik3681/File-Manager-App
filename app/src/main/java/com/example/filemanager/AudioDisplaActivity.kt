package com.example.filemanager

import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.VideoDisplayActivity.Companion.REQUEST_PERMISSION_CODE

class AudioDisplaActivity : AppCompatActivity() {
    private val audioFiles = mutableListOf<AudioData>()
    private lateinit var audioRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_displa)


        audioRecyclerView = findViewById(R.id.recyclerview)
        audioRecyclerView.layoutManager = LinearLayoutManager(this)

        if (arePermissionsGranted()) {
            fetchAudioFiles()
        } else {
          //  requestPermissions()
        }

        val backPress=findViewById<ImageView>(R.id.backPress)
        backPress.setOnClickListener{
            onBackPressed()
        }

        val selectItem=findViewById<ImageView>(R.id.openCheckBox)
        val header=findViewById<ConstraintLayout>(R.id.header_text)
        val header1=findViewById<LinearLayout>(R.id.header_text1)
        val bottomSheet=findViewById<ConstraintLayout>(R.id.bottomSheet)
        val cancleButton=findViewById<TextView>(R.id.cancle)
        val SelectAllItem=findViewById<TextView>(R.id.select_All)

        selectItem.setOnClickListener{
            header.visibility= View.GONE
            header1.visibility=View.VISIBLE
            bottomSheet.visibility=View.VISIBLE
            (audioRecyclerView.adapter as? AudioAdapter)?.apply {
                selectionMode = true
                notifyDataSetChanged()
            }
        }
        cancleButton.setOnClickListener{
            header.visibility=View.VISIBLE
            header1.visibility=View.GONE
            bottomSheet.visibility=View.GONE
            (audioRecyclerView.adapter as? AudioAdapter)?.apply {
                selectionMode = false
                notifyDataSetChanged()
            }
        }


    }
    private fun arePermissionsGranted(): Boolean {
        val readMediaVideoPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return readMediaVideoPermission == PackageManager.PERMISSION_GRANTED
    }
//    private fun requestPermissions() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
//                REQUEST_PERMISSION_CODE
//            )
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                REQUEST_PERMISSION_CODE
//            )
//        }
//    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAudioFiles()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchAudioFiles(){
        val projection= arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE
        )
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val sortOrder="${MediaStore.Audio.Media.DATE_ADDED} DESC"
        val cursor: Cursor?=contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        cursor?.use {
            val id=it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titlecolumn=it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val datacolumn=it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val datecolumn=it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val sizecolumn=it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (it.moveToNext()){
                val id = cursor.getLong(id)
                val title=it.getString(titlecolumn)
                val path=it.getString(datacolumn)
                val date=it.getLong(datecolumn)
                val size=it.getLong(sizecolumn)
                val uri = Uri.withAppendedPath(collection, id.toString())

                audioFiles.add(AudioData(uri,title,path,date,size))
            }
        }

        if (audioFiles.isNotEmpty()) {
            val adapter = AudioAdapter(this, audioFiles)
            audioRecyclerView.adapter = adapter
        } else {
            Toast.makeText(this, "No audio files found!", Toast.LENGTH_SHORT).show()
        }


    }

}