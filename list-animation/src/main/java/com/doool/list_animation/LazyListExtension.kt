package com.doool.list_animation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex

@OptIn(ExperimentalAnimationApi::class)
inline fun <T> LazyListScope.animateItemsIndexed(
  state: AnimationItemList<T>,
  enterTransition: EnterTransition = fadeIn() + expandIn(),
  exitTransition: ExitTransition = fadeOut() + shrinkOut(),
  noinline key: ((item: T) -> Any)? = null,
  crossinline itemContent: @Composable() (LazyItemScope.(index: Int, item: T) -> Unit)
) {
  itemsIndexed(
    state.list,
    if (key != null) { index: Int, item: AnimationItem<T> -> key(item.item) } else null
  ) { index, item ->
    // Move 1. onGloballyPositioned 를 통해서 아이템의 현재 위치 얻기
    Box(modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
      val rect = layoutCoordinates.boundsInParent()
      if (!rect.isEmpty) item.updatePosition(rect.top.toInt())
    }) {
      LaunchedEffect(key1 = index) {
        // Move 2. index 변경시 저장해둔 위치값을 기반으로 이동 애니메이션 진행
        if (item.isMoved()) item.startMoveAnimation()
      }

      AnimatedVisibility(
        modifier = Modifier
          // Move 3. offset 을 통해서 값만큼 이동
          .offset { IntOffset(0, item.moveOffset.value) }
          .zIndex(item.zIndex()),
        visibleState = item.visibility,
        // Add 1. 아이템 생성시에 추가되 애니메이션 진행
        enter = if (item.isAdded()) enterTransition else EnterTransition.None,
        // Remove 1. 아이템 제거시에 사라지는 애니메이션 진행
        exit = if (item.isRemoved()) exitTransition else ExitTransition.None
      ) {
        itemContent(index, item.item)
      }
    }
  }
}



