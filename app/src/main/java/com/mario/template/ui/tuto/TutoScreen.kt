package com.mario.template.ui.tuto

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mario.template.R
import com.mario.template.TemplateAppState
import com.mario.template.helper.extension.noRippleClickable
import com.mario.template.ui.component.AppScaffold
import com.mario.template.ui.theme.CustomTheme
import com.mario.template.ui.theme.MyTemplateTheme


@Composable
fun TutoRoute(appState: TemplateAppState, viewModel: TutoViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TutoScreen(state, viewModel::skip, appState::navigateToLogin)
}

@Composable
fun TutoScreen(state: TutoViewState, skip: () -> Unit = {}, onNavHome: () -> Unit = {}) {
    AppScaffold(state = state, onDismissRequest = { /*TODO*/ }) { _, _ ->
        TutoContent(state, skip, onNavHome)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TutoContent(state: TutoViewState, skip: () -> Unit = {}, onNavHome: () -> Unit = {}) {
    val pageCount = 4
    val pagerState = rememberPagerState(pageCount = { pageCount })

    if (state.isFinished) {
        LaunchedEffect(true) {
            onNavHome.invoke()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(CustomTheme.colors.tuto_gradient),
                shape = RectangleShape,
                alpha = 1.0f
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .padding(horizontal = 30.dp),
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(id = R.string.skip),
                    style = CustomTheme.typography.btnTitle1.copy(color = CustomTheme.colors.btn_title1),
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(20.dp)
                        .noRippleClickable {
                            skip()
                        }
                )
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    pageSpacing = 4.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) { page ->
                    TutoPagerItem(
                        page = page,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    Modifier
                        .height(24.dp)
                        .padding(start = 4.dp)
                        .width(57.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Start
                ) {
                    repeat(pageCount) { iteration ->

                        val color =
                            if (pagerState.currentPage == iteration) CustomTheme.colors.indicator_selected else CustomTheme.colors.indicator_unselected
                        Box(
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .clip(CustomTheme.shapes.indicator)
                                .background(color)
                                .width(if (pagerState.currentPage == iteration) 14.dp else 6.dp)
                                .height(6.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxSize()
                    .paint(
                        painterResource(id = R.drawable.bg_oval),
                        contentScale = ContentScale.Fit
                    ),
            ) {
                Spacer(modifier = Modifier.height(81.dp))
                Text(
                    text = stringArrayResource(id = R.array.arr_tuto_title)[pagerState.currentPage],
                    style = CustomTheme.typography.heading03.copy(
                        color = CustomTheme.colors.title1,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = stringArrayResource(id = R.array.arr_tuto_content)[pagerState.currentPage],
                    style = CustomTheme.typography.heading04.copy(
                        color = CustomTheme.colors.subtitle1,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                val arrImageResource =
                    arrayOf(
                        R.drawable.btn_tuto_1,
                        R.drawable.btn_tuto_2,
                        R.drawable.btn_tuto_3,
                        R.drawable.btn_tuto_4
                    )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = arrImageResource[pagerState.currentPage]),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 40.dp)
                            .width(80.dp)
                            .height(80.dp)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
internal fun TutoPagerItem(
    page: Int,
    modifier: Modifier = Modifier
) {
    val arrImageResource =
        arrayOf(R.drawable.ic_tuto1, R.drawable.ic_tuto2, R.drawable.ic_tuto3, R.drawable.ic_tuto4)
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = arrImageResource[page]),
            contentScale = ContentScale.Fit,
            contentDescription = null,
            modifier = Modifier.matchParentSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TutoPreview() {
    MyTemplateTheme {
        TutoContent(TutoViewState())
    }
}