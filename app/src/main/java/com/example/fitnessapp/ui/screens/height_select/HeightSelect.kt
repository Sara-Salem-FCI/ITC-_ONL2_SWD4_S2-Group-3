package com.example.fitnessapp.ui.screens.height_select

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.R
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun rememberPickerState() = remember { PickerState() }

class PickerState {
    var selectedItem by mutableStateOf("")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Picker(
    items: List<String>,
    state: PickerState = rememberPickerState(),
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    val visibleItemsMiddle = visibleItemsCount / 2
    val listScrollCount = Integer.MAX_VALUE
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex =
        listScrollMiddle - listScrollMiddle % items.size - visibleItemsMiddle + startIndex
    fun getItem(index: Int) = items[index % items.size]
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels.value)
    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.5f to Color.Black,
            1f to Color.Transparent
        )
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item -> state.selectedItem = item }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(100.dp)
                    .height(itemHeightDp * visibleItemsCount)
                    .fadingEdge(fadingEdgeGradient)
            ) {
                items(listScrollCount) { index ->
                    val item = getItem(index)
                    val isSelected = item == state.selectedItem
                    Text(
                        text = item,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = if (isSelected) {
                            textStyle.copy(
                                fontWeight = FontWeight.Bold // Bolder font weight for selected item
                            )
                        } else {
                            textStyle
                        },
                        modifier = Modifier
                            .onSizeChanged { size -> itemHeightPixels.value = size.height }
                            .then(textModifier)
                    )
                }
            }

        }

        Column(
            modifier = Modifier
                .padding(start = 6.dp)
                .width(100.dp)
                .size(400.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ruler), // Use the vector drawable
                contentDescription = "Ruler",
                modifier = Modifier.size(350.dp)
            )
        }
    }
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }

@Composable
fun NumberPickerDemo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        val values = remember { (140..210).map { it.toString() } }
        val valuesPickerState = rememberPickerState()

        // Title Text
        Text(
            text = "What is your Height?",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight(700),
            fontSize = 35.sp,
            color = Color(0xFFFFFFFF),
        )

        // Selected Height Text
        Text(
            text = "${valuesPickerState.selectedItem} Cm",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight(700),
            fontSize = 35.sp,
            color = Color(0xFFFFFFFF),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(0.5f)
                .padding(vertical = 10.dp, horizontal = 16.dp)
        )

        // Number Picker
        Picker(
            state = valuesPickerState,
            items = values,
            visibleItemsCount = 5,
            modifier = Modifier.fillMaxWidth(0.5f),
            textModifier = Modifier.padding(10.dp),
            textStyle = TextStyle(fontSize = 32.sp, color = Color(0xFFFFFFFF)),
        )

        // Add Spacing Between Picker and Button
        Spacer(modifier = Modifier.height(32.dp))

        // Continue Button

        Button(
            onClick = {
                println("Selected Height: ${valuesPickerState.selectedItem} Cm")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2D30)),
            modifier = Modifier
                .fillMaxWidth(0.5f),
        ) {
            Text(
                text = "Continue",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }


    }
}