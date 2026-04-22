package com.nnita.kickin.ui.matchdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nnita.kickin.R
import com.nnita.kickin.ui.theme.KickinTheme

@Composable
fun MatchDetailScreen(fixtureId: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.screen_match_detail, fixtureId),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMatchDetailScreen() {
    KickinTheme { MatchDetailScreen(fixtureId = 1035765) }
}
