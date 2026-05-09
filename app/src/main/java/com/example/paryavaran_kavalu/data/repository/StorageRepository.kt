package com.example.paryavaran_kavalu.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    suspend fun uploadImage(uri: Uri, folder: String): String {
        val fileName = "${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child("$folder/$fileName")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
