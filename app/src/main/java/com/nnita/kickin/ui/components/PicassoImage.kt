package com.nnita.kickin.ui.components

import android.widget.ImageView
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.squareup.picasso.Picasso
import com.nnita.kickin.ui.theme.KickinTheme

@Composable
fun PicassoImage(
    url: String,
    teamName: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    var loadFailed by remember(url) { mutableStateOf(false) }

    if (url.isBlank() || loadFailed) {
        TeamIcon(teamName = teamName, size = size, modifier = modifier)
    } else {
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
            },
            update = { imageView ->
                Picasso.get()
                    .load(url)
                    .into(
                        imageView,
                        object : com.squareup.picasso.Callback {
                            override fun onSuccess() {}
                            override fun onError(e: Exception?) {
                                loadFailed = true
                            }
                        }
                    )
            },
            modifier = modifier.size(size)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPicassoImage() {
    KickinTheme {
        PicassoImage(url = "", teamName = "Arsenal", size = 40.dp)
    }
}
