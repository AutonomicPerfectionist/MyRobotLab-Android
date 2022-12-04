package org.myrobotlab.android.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.myrobotlab.android.MrlAndroidTheme

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .wrapContentSize(Alignment.TopCenter)
    ) {
        Text(
            text = "MyRobotLab Android",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text("Version: 0.0.1 Alpha 1")
        Text("Author: Branden Butler (AutonomicPerfectionist @ Github.com)")

        val annotatedLinkString: AnnotatedString = buildAnnotatedString {

            val str = "Repo: Github"
            val startIndex = str.indexOf("Github")
            val endIndex = startIndex + 6
            append(str)
            addStyle(
                style = SpanStyle(
                    color = Color(0xff64B5F6),
                    textDecoration = TextDecoration.Underline
                ), start = startIndex, end = endIndex
            )

            // attach a string annotation that stores a URL to the text "link"
            addStringAnnotation(
                tag = "URL",
                annotation = "https://github.com/AutonomicPerfectionist/MyRobotLab-Android/",
                start = startIndex,
                end = endIndex
            )

        }

// UriHandler parse and opens URI inside AnnotatedString Item in Browse
        val uriHandler = LocalUriHandler.current

// 🔥 Clickable text returns position of text that is clicked in onClick callback
        ClickableText(
            text = annotatedLinkString,
            onClick = {
                annotatedLinkString
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
            },
            style = TextStyle(color = MaterialTheme.colors.onBackground)
        )

        Text("License: Apache 2.0")
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    MrlAndroidTheme {
        AboutScreen()
    }
}