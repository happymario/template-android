package com.victoria.bleled.app.recent.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.victoria.bleled.app.recent.compose.ui.theme.MyApplicationTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        MyApp()
    }
}

class ComposeUiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MyApp()
            }
        }
    }
}

@Composable
private fun MyApp() {
    //var shouldShowOnboarding by remember { mutableStateOf(true)}
    var shouldShowBoarding by rememberSaveable { mutableStateOf(true)}
    Surface(color = MaterialTheme.colors.background) {
        if(shouldShowBoarding) {
            OnboardingScreen { shouldShowBoarding = false }
        }
        else {
            Greetings()
        }
    }
}

@Composable
private fun Greetings(names: List<String> = List(1000) {"$it"}) {
    LazyColumn(modifier = Modifier.padding(4.dp)) {
        items(items = names) { name ->
            Greetings(name = name)
        }
    }
}

@Composable
private fun Greetings(name: String) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val extraPadding by animateDpAsState(
        if(expanded) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Surface(color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)) {
            Column( modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPadding.coerceAtLeast(0.dp))){
                Text(text = "Hello,", style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.ExtraBold))
                Text(text = "$name", style=MaterialTheme.typography.h4)
            }
            OutlinedButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Show less" else "Show more")
            }
        }
    }
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
    Surface {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcom to the Basics Codlab!", color = MaterialTheme.colors.primary)
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked ) {
                Text("Continue")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    MaterialTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}