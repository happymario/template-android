package com.mario.template

import androidx.compose.runtime.Composable
import com.mario.template.base.BaseComposeActivity
import com.mario.template.ui.theme.MyTemplateTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StartActivity : BaseComposeActivity() {

    @Composable
    override fun ComposeContent() {
        return MyTemplateTheme {
            TemplateApp()
        }
    }
}