import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max


private val scrollBarWidth = 10.dp

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

    val updateVisibility: suspend () -> Unit = {
        isVisible = if (isAlwaysShowScrollBar || listState.isScrollInProgress || isPressed) {
            true
        } else {
            // 操作をやめてから800ms後に非表示にする
            delay(800)
            // 常に表示 or スクロール中　or tap中　は表示
            isAlwaysShowScrollBar || listState.isScrollInProgress || isPressed
        }
    }

    LaunchedEffect(isAlwaysShowScrollBar, listState.isScrollInProgress) {
        updateVisibility.invoke()
    }

    var viewHeight by remember {
        mutableStateOf(0)
    }

    val totalCount = listState.layoutInfo.totalItemsCount
    if (totalCount == 0) return

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

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Row(
            modifier = modifier
                .onGloballyPositioned {
                    viewHeight = it.size.height
                },
            horizontalArrangement = Arrangement.End
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(scrollBarWidth)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            while (true) {
                                val eventList = awaitPointerEvent().changes

                                isPressed = true

                                if (eventList.any { it.pressed }.not()) {
                                    break
                                }

                                // tap位置を取得
                                val tap = eventList.last().position.y

                                // タップ位置と描画領域の比率から表示アイテムを決定
                                val target = (tap / viewHeight * listState.layoutInfo.totalItemsCount).toInt()

                                scope.launch {
                                    listState.scrollToItem(
                                        max(
                                            target,
                                            0
                                        )
                                    )
                                }
                            }

                            scope.launch {
                                // tap終了
                                isPressed = false

                                //表示切り替え
                                updateVisibility.invoke()
                            }
                        }
                    }
            ) {
                drawRect(
                    color = Color.Gray,
                    topLeft = Offset(
                        size.width - scrollBarWidth.toPx(),
                        scrollbarTopY1 + scrollbarTopOffset
                    ),
                    size = Size(scrollBarWidth.toPx(), scrollbarHeight)
                )
            }
        }
    }
}
