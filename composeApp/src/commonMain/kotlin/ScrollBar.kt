import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


private val scrollBarWidth = 10.dp

@Composable
fun BoxScope.ScrollBar(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    isAlwaysShowScrollBar: Boolean = false,
) {
    var isVisible by remember { mutableStateOf(isAlwaysShowScrollBar) }

    LaunchedEffect(isAlwaysShowScrollBar, listState.isScrollInProgress) {
        isVisible = if (isAlwaysShowScrollBar || listState.isScrollInProgress) {
            true
        } else {
            delay(800) // スクロールが止まってから800ms後に非表示にする
            false
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Canvas(
            modifier = modifier
                .align(Alignment.CenterEnd)
                .fillMaxSize()
        ) {
            val viewHeight = size.height
            val totalCount = listState.layoutInfo.totalItemsCount
            if (totalCount == 0) return@Canvas

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
