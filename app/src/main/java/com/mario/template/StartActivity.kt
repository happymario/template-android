package com.mario.template

import androidx.compose.runtime.Composable
import com.mario.template.base.BaseComposeActivity
import com.mario.template.ui.theme.MyWeatherTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StartActivity : BaseComposeActivity() {

    @Composable
    override fun ComposeContent() {
        return MyWeatherTheme {
            TemplateApp()
        }
    }
}