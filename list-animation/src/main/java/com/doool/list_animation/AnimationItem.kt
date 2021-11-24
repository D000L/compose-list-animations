package com.doool.list_animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.VectorConverter

data class AnimationItem<T>(
    val visibility: MutableTransitionState<Boolean>,
    val item: T,
) {
    private val state = MutableTransitionState(AnimationState.None)
    val moveOffset = Animatable(0, Int.VectorConverter)

    private var oldPosition: Int = 0
    private var newPosition: Int = 0

    fun isAdded() = state.targetState == AnimationState.Add
    fun isRemoved() = state.targetState == AnimationState.Remove
    fun isMoved() = state.targetState == AnimationState.Move
    fun zIndex() = newPosition.toFloat()

    fun updateState(animationState: AnimationState) {
        state.targetState = animationState
    }

    fun updatePosition(position: Int) {
        if (position != newPosition) {
            oldPosition = newPosition
            newPosition = position
        }
    }

    suspend fun startMoveAnimation() {
        val amount = oldPosition - newPosition + moveOffset.value
        moveOffset.snapTo(amount)
        moveOffset.animateTo(0) {
            if (this.value == 0) updateState(AnimationState.None)
        }
    }
}