package com.example.paryavaran_kavalu

import android.app.Application
import com.example.paryavaran_kavalu.data.repository.ReportRepository

class ParyavaranApp : Application() {
    val repository by lazy { ReportRepository() }
}
