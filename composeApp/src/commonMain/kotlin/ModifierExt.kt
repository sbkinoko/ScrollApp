import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

fun Modifier.frame(): Modifier {
    return this.border(
        width = 1.dp,
        color = Color(0, 0, 0)
    )
}

fun Modifier.isPressed(
    buttonState: ButtonState,
    update: (ButtonState) -> Unit,
): Modifier {
    return this.pointerInput(Unit) {
        awaitEachGesture {
            awaitPointerEvent()
            update(buttonState)

            // 押されている間はtrue
            while (
                awaitPointerEvent().changes.any {
                    it.pressed
                }
            ) {
               // なにもしない
            }

            //　放されたらfalse
            update(ButtonState.None)
        }
    }
}
