package com.example.paryavaran_kavalu.data.repository

import com.example.paryavaran_kavalu.data.model.Report
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReportRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val reportsCollection = firestore.collection("reports")

    fun getAllReports(): Flow<List<Report>> = callbackFlow {
        val subscription = reportsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reports = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Report::class.java)?.copy(id = doc.id)
                    }
                    trySend(reports)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun insertReport(report: Report) {
        reportsCollection.add(report).await()
    }

    suspend fun updateReport(report: Report) {
        if (report.id.isNotEmpty()) {
            reportsCollection.document(report.id).set(report).await()
        }
    }

    suspend fun markAsCleaned(reportId: String, cleanedImageUrl: String, cleanedBy: String) {
        reportsCollection.document(reportId).update(
            "status", "Cleaned",
            "cleanedImageUrl", cleanedImageUrl,
            "cleanedBy", cleanedBy
        ).await()
    }
}
