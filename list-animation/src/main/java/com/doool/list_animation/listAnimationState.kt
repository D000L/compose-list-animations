package com.doool.list_animation

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback

@Composable
fun <T : Any> listAnimationState(
    newList: List<T>
): AnimationItemList<T> {

    val state = remember { AnimationItemList(newList) }
    val newList by rememberUpdatedState(newValue = newList)

    var updated by remember {
        mutableStateOf(false)
    }

    val diffCb = remember {
        object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = state.count
            override fun getNewListSize(): Int = newList.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                state.list[oldItemPosition].item == newList[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                state.list[oldItemPosition].item == newList[newItemPosition]
        }
    }

    val listDiffer = remember {
        object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                state.addAll(position, newList.subList(position, position + count))
                updated = !updated
            }

            override fun onRemoved(position: Int, count: Int) {
                state.removeRange(position, position + count)
                updated = !updated
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                val from = kotlin.math.min(fromPosition, toPosition)
                val to = kotlin.math.max(fromPosition, toPosition)

                for (i in from..to) {
                    state.list[i].updateState(AnimationState.Move)
                }

                val item = state.list.removeAt(fromPosition)
                state.list.add(toPosition, item)

                updated = !updated
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {}
        }
    }

    LaunchedEffect(newList) {
        state.clearRemovedItems()

        val result = DiffUtil.calculateDiff(diffCb, true)
        result.dispatchUpdatesTo(listDiffer)
    }

    LaunchedEffect(key1 = updated) {
        val initialAnimation = Animatable(1.0f)
        initialAnimation.animateTo(0f)

        state.clearRemovedItems()
    }

    return state
}