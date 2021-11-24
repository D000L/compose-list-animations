package com.doool.list_animation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.mutableStateListOf

class AnimationItemList<T>(initList: List<T>) {
    var list = mutableStateListOf<AnimationItem<T>>().apply {
        addAll(initList.mapIndexed { index, item -> createItem(item, true) })
    }

    val count get() = list.size

    private fun createItem(item: T, immediately: Boolean = false): AnimationItem<T> {
        return AnimationItem(
            visibility = MutableTransitionState(immediately).apply {
                this.targetState = true
            },
            item = item
        ).apply {
            if (!immediately) updateState(AnimationState.Add)
        }
    }

    private fun addItem(index: Int, item: T) {
        val animationItem = createItem(item)

        list.add(index, animationItem)
    }

    fun addAll(index: Int, items: List<T>) {
        items.forEachIndexed { i, item ->
            addItem(index + i, item)
        }
    }

    private fun removeItem(index: Int) {
        list[index].updateState(AnimationState.Remove)
        list[index].visibility.targetState = false
    }

    fun removeRange(start: Int, end: Int) {
        for (i in start until end) {
            removeItem(i)
        }
    }

    fun clearRemovedItems() {
        list.removeAll {
            !it.visibility.targetState
        }
    }

    fun clear(){
        list.clear()
    }
}

