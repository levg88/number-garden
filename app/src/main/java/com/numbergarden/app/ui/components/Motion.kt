package com.numbergarden.app.ui.components

import android.animation.ValueAnimator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Motion is opt-out at the app level and also respects the system animation scale.
 * Nothing in the app depends on animation to communicate meaning.
 */
@Composable
fun rememberMotionAllowed(reduceMotion: Boolean): Boolean {
    val view = LocalView.current
    val systemAnimations = remember(view) { ValueAnimator.areAnimatorsEnabled() }
    return !reduceMotion && systemAnimations
}
