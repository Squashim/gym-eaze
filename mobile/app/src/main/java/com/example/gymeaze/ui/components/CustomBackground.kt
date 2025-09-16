package com.example.gymeaze.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getGradientMiddleColor
import com.example.gymeaze.ui.theme.getPrimaryColor

@Composable
fun BackgroundWithDotsAndCircles(
    modifier: Modifier = Modifier
) {

    val dotColor = getGradientMiddleColor()
    val backgroundColor = getBackgroundColor()
    val greenCircleColor = getPrimaryColor().copy(alpha = 0.3f)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false }
    ) {
        drawRect(color = backgroundColor, size = size)

        val step = 40f
        for (x in 0..(size.width / step).toInt()) {
            for (y in 0..(size.height / step).toInt()) {
                drawCircle(
                    color = dotColor,
                    radius = 4f,
                    center = Offset(x * step, y * step)
                )
            }
        }

        drawIntoCanvas { canvas ->
            val paint = Paint().asFrameworkPaint().apply {
                isAntiAlias = true
                color = greenCircleColor.toArgb()
                maskFilter = BlurMaskFilter(200f, BlurMaskFilter.Blur.NORMAL)
            }

            canvas.nativeCanvas.drawCircle(
                size.width * 0.8f,
                size.height * 0.2f,
                size.minDimension / 3,
                paint
            )

            canvas.nativeCanvas.drawCircle(
                size.width * 0.2f,
                size.height * 0.7f,
                size.minDimension / 2,
                paint
            )
        }
    }
}


@Preview(showBackground = true, name = "Light Theme")
@Composable
fun BackgroundWithDotsAndCirclesLightPreview() {
    BackgroundWithDotsAndCircles(modifier = Modifier.fillMaxSize())
}
