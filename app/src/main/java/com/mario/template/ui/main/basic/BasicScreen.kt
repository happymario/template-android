package com.mario.template.ui.main.basic

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberImagePainter
import com.mario.template.TemplateAppState
import com.mario.template.ui.component.AppScaffold
import kotlinx.coroutines.launch

@Composable
fun BasicRoute(appState: TemplateAppState, viewModel: BasicViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    BasicScreen(state, onDetail = {
        when (it.lowercase()) {
            "camera" -> {
                appState.goCameraActivity(context)
            }

            "webview" -> {
                appState.goCameraActivity(context)
            }
        }
    })
}

@Composable
fun BasicScreen(state: BasicViewState, onDetail: (menu: String) -> Unit = {}) {
    AppScaffold(state = state, onDismissRequest = { /*TODO*/ }) { _, _ ->
        BasicContent(state, onDetail = onDetail)
    }
}


@Composable
fun BasicContent(
    state: BasicViewState,
    modifier: Modifier = Modifier,
    onDetail: (menu: String) -> Unit = {},
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val list = state.list

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
                        scrollState.animateScrollToItem(list.size - 1)
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
                items = list,
                itemContent = {
                    ImageListItem(item = it, onClick = onDetail)
                }
            )
        }
    }
}


@Composable
fun ImageListItem(item: String, onClick: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            onClick(item)
        }) {
        Image(
            painter = rememberImagePainter(data = "https://developer.android.com/images/brand/Android_Robot.png"),
            contentDescription = "Android Logo",
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text("Item #$item", color = MaterialTheme.colors.onSecondary)
    }
}


@Preview(showBackground = true)
@Composable
fun EssentialPreview() {
    BasicScreen(BasicViewState())
}