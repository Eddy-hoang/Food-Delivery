package com.example.fooddeliveryapp.feature.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.IconPrimary
import com.example.fooddeliveryapp.ui.theme.Resources
import com.example.fooddeliveryapp.ui.theme.SurfaceLighter

@Composable
fun ProfilePhotoEditor(
    photoUrl: String?,
    isUpLoading: Boolean,
    progress: Float,
    onPickClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(132.dp)
                .clip(CircleShape)
                .background(SurfaceLighter)
                .clickable(enabled = !isUpLoading) { onPickClick() },
            contentAlignment = Alignment.Center
        ) {
            // Sử dụng SubcomposeAsyncImage để xử lý ảnh từ URL Cloudinary
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "User profile picture",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = IconPrimary,
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = { state ->
                    android.util.Log.e("KiemTraAnh", "Coil khong the load anh. Loi: ", state.result.throwable)

                    Icon(
                        painter = painterResource(Resources.Icon.Person),
                        contentDescription = "Error Profile icon",
                        tint = IconPrimary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            )

            if (isUpLoading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            color = Color.White,
                            strokeWidth = 4.dp,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            color = Color.White,
                            fontSize = FontSize.SMALL
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onPickClick,
            enabled = !isUpLoading,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(Resources.Icon.Camera),
                    contentDescription = "Camera icon",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Change Photo",
                    color = Color.Black,
                    fontSize = FontSize.SMALL
                )
            }
        }
    }
}
