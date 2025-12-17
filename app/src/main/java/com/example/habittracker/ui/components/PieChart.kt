package com.example.habittracker.ui.components


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PieChart(
    values: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = values.sum()
    var animationTarget by remember { mutableStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(
            500
        )
    )

    LaunchedEffect(Unit) {
        animationTarget = 1f
    }

    Canvas(modifier = modifier) {
        var startAngle = -90f // start at top

        values.forEachIndexed { index, value ->
            val sweepAngle = (value / total) * 360f * animatedProgress

            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )

            startAngle += sweepAngle
        }
    }
}
