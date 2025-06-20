import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val scrollBarWidth = 10.dp

const val VisibleTime = 800L

@Composable
fun BoxScope.ScrollBar(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    isAlwaysShowScrollBar: Boolean = false,
) {
    var isVisible by remember { mutableStateOf(isAlwaysShowScrollBar) }

    var isPressed by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(isAlwaysShowScrollBar, listState.isScrollInProgress) {
        isVisible = if (isAlwaysShowScrollBar || listState.isScrollInProgress) {
            true
        } else {
            // スクロールが止まってからVisibleTime ms後に非表示にする
            delay(VisibleTime)
            // ただし、バーを触っていたら消さない
            isPressed
        }
    }

    val density = LocalDensity.current

    var height by remember {
        mutableStateOf(0)
    }

    var move by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(move) {
        listState.scrollBy(move)
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Row(
            modifier = modifier
                .onGloballyPositioned {
                    height = it.size.height
                },
            horizontalArrangement = Arrangement.End,
        ) {
            val viewHeight = height
            val totalCount = listState.layoutInfo.totalItemsCount
            if (totalCount == 0) return@Row

            val firstVisibleItemIndex = listState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
            val visibleItemCount = listState.layoutInfo.visibleItemsInfo.size

            val scrollRatio = firstVisibleItemIndex.toFloat() / totalCount

            // スクロールバーの位置とサイズを計算
            val scrollbarHeight = viewHeight * (visibleItemCount.toFloat() / totalCount)
            val scrollbarTopY1 = scrollRatio * viewHeight

            // 次のアイテムの位置とサイズを計算
            val scrollRatio2 = (firstVisibleItemIndex + 1).toFloat() / totalCount
            val scrollbarTopY2 = scrollRatio2 * viewHeight

            // 表示中の先頭アイテムの高さ
            val firstVisibleItemHeight = listState.layoutInfo.visibleItemsInfo.getOrNull(0)?.size

            // スクロールバー位置の微調整(スクロール量をスクロールバーのoffsetに変換する。offsetの範囲はこのアイテムと次のアイテムのスクロールバーの位置)
            val scrollbarTopOffset = if (firstVisibleItemHeight == null || firstVisibleItemHeight == 0) {
                // 先頭アイテムの高さが不明なので微調整なし
                0f
            } else {
                firstVisibleItemScrollOffset.toFloat() / firstVisibleItemHeight * (scrollbarTopY2 - scrollbarTopY1)
            }

            Spacer(
                modifier = Modifier
                    .padding(
                        top = with(density) {
                            (scrollbarTopY1 + scrollbarTopOffset)
                                .toDp()
                        },
                    )
                    .height(with(density) { scrollbarHeight.toDp() })
                    .width(scrollBarWidth)
                    .background(
                        Color.Gray,
                    ).pointerInput(Unit) {
                        awaitEachGesture {
                            // 放すときも反応するのでタップ時だけ処理できるように入れる
                            val first = awaitFirstDown()
                            move = 0f

                            isPressed = true

                            while (true) {
                                val eventList = awaitPointerEvent().changes
                                if (eventList.all { it.pressed.not() }) {
                                    break
                                }

                                // tap位置と最初のタップ位置の差だけ移動する
                                move = (eventList.last().position.y - first.position.y)
                            }

                            isPressed = false
                            move = 0f

                            scope.launch {
                                // 一定時間経過後
                                delay(VisibleTime)
                                // 、触ってなければ非表示
                                isVisible = isPressed
                            }
                        }
                    },
            )
        }
    }
}
