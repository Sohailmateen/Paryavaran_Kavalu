package com.example.paryavaran_kavalu

import android.app.Application
import com.example.paryavaran_kavalu.data.local.db.AppDatabase
import com.example.paryavaran_kavalu.data.repository.ReportRepository

class ParyavaranApp : Application() {
    
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ReportRepository(database.reportDao()) }
}
