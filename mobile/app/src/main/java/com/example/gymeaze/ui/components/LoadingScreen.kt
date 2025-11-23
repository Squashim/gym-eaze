package com.example.gymeaze.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.res.stringResource
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.LocalDarkTheme
import com.example.gymeaze.ui.theme.getPrimaryColor

@Composable
fun LoadingScreen() {
    val isDarkTheme = LocalDarkTheme.current

    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        )
    )

    val logoRes = if (isDarkTheme) R.drawable.gym_eaze_logo_light else R.drawable.gym_eaze_logo_dark
    val arcColor = getPrimaryColor()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = logoRes),
            contentDescription = stringResource(R.string.app_logo),
            modifier = Modifier.size(110.dp)
        )
        Canvas(
            modifier = Modifier
                .size(160.dp)
                .rotate(angle)
        ) {
            drawArc(
                color = arcColor,
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    }
}