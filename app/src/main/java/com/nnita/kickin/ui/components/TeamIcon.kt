package com.nnita.kickin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnita.kickin.ui.theme.KickinTheme
import kotlin.math.abs

private val palette = listOf(
    Color(0xFF1565C0),
    Color(0xFFC62828),
    Color(0xFF6A1B9A),
    Color(0xFF00695C),
    Color(0xFFE65100),
    Color(0xFF4527A0),
    Color(0xFF2E7D32),
    Color(0xFF4E342E),
    Color(0xFF0277BD),
    Color(0xFFAD1457),
    Color(0xFF558B2F),
    Color(0xFF37474F),
)

@Composable
fun TeamIcon(
    teamName: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val color = palette[abs(teamName.hashCode()) % palette.size]
    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = teamName.firstOrNull()?.uppercase() ?: "?",
            color = Color.White,
            fontSize = (size.value * 0.38f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTeamIcon() {
    KickinTheme {
        TeamIcon(teamName = "Liverpool", size = 40.dp)
    }
}
