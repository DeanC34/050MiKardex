package com.example.a050a41mikardex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = CardexDatabase.getDatabase(this)
        setContent {
            AppMiCardex(db.materiaDao())
        }
    }
}