package com.victoria.bleled.app.recent.compose.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.victoria.bleled.app.ViewModelFactory2
import com.victoria.bleled.app.main.MainViewModel
import kotlinx.coroutines.launch


@Preview(showBackground = true)
@Composable
fun EssentialPreview() {
    Essential()
}

@Composable
fun Essential(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val viewModel: MainViewModel = viewModel(factory = ViewModelFactory2(LocalContext.current))
    val essentialList by viewModel.items.observeAsState(emptyList())

    viewModel.start()
    viewModel.setPage(0)

    Column(modifier = modifier) {
        Text(text = "Essential")
        Row {
            Button(
                onClick = {
                    coroutineScope.launch {
                        // 0 is the first item index
                        scrollState.animateScrollToItem(0)
                    }
                }) {
                Text(
                    "Scroll to the top",
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(essentialList.size - 1)
                    }
                }) {
                Text(
                    "Scroll to the bottom",
                    color = Color.White
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = scrollState
        ) {
            items(
                items = essentialList,
                itemContent = {
                    ImageListItem(item = it)
                }
            )
        }
    }
}


@Composable
fun ImageListItem(item: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberImagePainter(data = "https://developer.android.com/images/brand/Android_Robot.png"),
            contentDescription = "Android Logo",
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text("Item #$item", color = MaterialTheme.colors.onSecondary)
    }
}