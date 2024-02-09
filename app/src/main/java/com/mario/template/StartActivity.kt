package com.mario.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mario.template.ui.theme.MyWeatherTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyWeatherTheme {
                TemplateApp()
            }
        }
    }
}