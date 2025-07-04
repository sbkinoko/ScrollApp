import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.max

@Composable
@Preview
fun App() {
    val scrollState = rememberLazyListState()

    var isButtonPressed by remember {
        mutableStateOf(ButtonState.None)
    }

    LaunchedEffect(isButtonPressed) {
        val delayTime = 100L
        var dif = 1
        while (true) {
            when (isButtonPressed) {
                ButtonState.Up -> scrollState.animateScrollToItem(
                    max(
                        scrollState.firstVisibleItemIndex - dif,
                        0,
                    ),
                )

                ButtonState.Down -> scrollState.animateScrollToItem(
                    scrollState.firstVisibleItemIndex + dif,
                )

                ButtonState.None -> return@LaunchedEffect
            }
            delay(delayTime)
            dif++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .frame(),
                state = scrollState,
            ) {
                items(
                    List(50) { it + 1 }
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .frame(),
                        textAlign = TextAlign.Center,
                        text = "$it",
                        fontSize = 50.sp
                    )
                }
            }

            ScrollBar(
                modifier = Modifier.fillMaxSize(),
                listState = scrollState,
            )
        }

        Text(
            text = "↑",
            modifier = Modifier
                .fillMaxWidth()
                .frame()
                .clickable {}
                .isPressed(
                    buttonState = ButtonState.Up
                ) {
                    isButtonPressed = it
                },
            fontSize = 50.sp,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "↓",
            modifier = Modifier
                .fillMaxWidth()
                .frame()
                .clickable { }
                .isPressed(buttonState = ButtonState.Down) {
                    isButtonPressed = it
                },
            fontSize = 50.sp,
            textAlign = TextAlign.Center,
        )
    }
}
