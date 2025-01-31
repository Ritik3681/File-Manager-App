package com.example.filemanager

import android.net.Uri

data class AudioData(
    val uri: Uri,

    val title: String,
    val path: String,
    val dateAdded: Long,
    val size: Long
)
