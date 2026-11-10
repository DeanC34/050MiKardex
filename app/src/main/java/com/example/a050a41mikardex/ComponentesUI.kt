package com.example.a050a41mikardex

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object EstilosCardex {
    val gradienteFondo = Brush.verticalGradient(
        listOf(Color(0xFFE0F7FA), Color(0xFF80DEEA))
    )

    val modifierTarjeta = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White.copy(alpha = 0.6f))
        .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
        .padding(16.dp)
}