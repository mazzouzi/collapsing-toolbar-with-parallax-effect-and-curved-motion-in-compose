package com.example.collapsingtoolbar

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collapsingtoolbar.ui.theme.CollapsingToolbarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollapsingToolbarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    CollapsingToolbarParallaxEffect()
                }
            }
        }
    }
}

private val headerHeight = 250.dp
private val toolbarHeight = 56.dp

private val paddingMedium = 16.dp

private val titlePaddingStart = 16.dp
private val titlePaddingEnd = 64.dp

private const val titleFontScaleStart = 1f
private const val titleFontScaleEnd = 0.66f

@Composable
fun CollapsingToolbarParallaxEffect() {
    val scroll: ScrollState = rememberScrollState(0)

    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Header(scroll, headerHeightPx)
        Body(scroll)
        Toolbar(scroll, headerHeightPx, toolbarHeightPx)
        Title(scroll, headerHeightPx, toolbarHeightPx)
    }
}

@Composable
private fun Header(scroll: ScrollState, headerHeightPx: Float) {
    val headerY = -scroll.value.toFloat() / 2f // Parallax effect
    val headerAlpha = (-1f / headerHeightPx) * scroll.value + 1
    Log.d(
        "Morad",
        "translationY = $headerY | alpha = $headerAlpha | scroll = ${scroll.value}"
    )

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(headerHeight)
        .graphicsLayer {
            translationY = headerY
            alpha = headerAlpha
        }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_pexel),
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xAA000000)),
                        startY = 3 * headerHeightPx / 4 // Gradient applied to wrap the title only
                    )
                )
        )
    }
}

@Composable
private fun Body(scroll: ScrollState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(scroll)
    ) {
        Spacer(Modifier.height(headerHeight))
        repeat(5) {
            Text(
                text = stringResource(R.string.detail_placeholder),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .background(Color(0XFF161616))
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun Toolbar(scroll: ScrollState, headerHeightPx: Float, toolbarHeightPx: Float) {
    var showToolbar by remember { mutableStateOf(false) }
    val toolbarBottomTranslation = headerHeightPx - toolbarHeightPx
    showToolbar = scroll.value >= toolbarBottomTranslation

    AnimatedVisibility(
        visible = showToolbar,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xff026586), Color(0xff032C45))
                    )
                )
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun Title(
    scroll: ScrollState,
    headerHeightPx: Float,
    toolbarHeightPx: Float
) {
    val titlePaddingStartPx = with(LocalDensity.current) { titlePaddingStart.toPx() }
    val titlePaddingEndPx = with(LocalDensity.current) { titlePaddingEnd.toPx() }
    val paddingMediumPx = with(LocalDensity.current) { paddingMedium.toPx() }

    var titleHeightPx by remember { mutableStateOf(0f) }

    val titleScale = (titleFontScaleEnd - titleFontScaleStart)
        .div(headerHeightPx - toolbarHeightPx)
        .times(scroll.value)
        .plus(titleFontScaleStart)
        .coerceAtLeast(titleFontScaleEnd)

    val titleY =
        (-headerHeightPx - (toolbarHeightPx / 2) + (3 * titleHeightPx / 2) + 2 * paddingMediumPx)
            .div(headerHeightPx - toolbarHeightPx)
            .times(scroll.value)
            .plus(headerHeightPx - titleHeightPx - paddingMediumPx)

    val titleX = (titlePaddingEndPx - titlePaddingStartPx)
        .div(headerHeightPx - toolbarHeightPx)
        .times(scroll.value)
        .plus(titlePaddingStartPx)

    Text(
        text = "New York",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .graphicsLayer {
                translationY = titleY.coerceAtLeast(toolbarHeightPx / 2 - titleHeightPx / 2)
                translationX = titleX.coerceAtMost(titlePaddingEndPx)
                scaleX = titleScale
                scaleY = titleScale
            }
            .onGloballyPositioned {
                titleHeightPx = it.size.height.toFloat()
            }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CollapsingToolbarTheme {
        CollapsingToolbarParallaxEffect()
    }
}
