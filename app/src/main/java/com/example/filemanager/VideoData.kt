package com.example.filemanager

import android.net.Uri
import android.provider.MediaStore.Video.Thumbnails

data class VideoData(
    val uri: Uri,
    val name: String,
    val duration: Long,
    val size: Long,
    val thumbnails:Uri,
    val date:Long
)
