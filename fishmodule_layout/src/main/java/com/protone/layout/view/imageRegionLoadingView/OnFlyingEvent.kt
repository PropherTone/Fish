package com.protone.layout.view.imageRegionLoadingView

interface OnFlyingEvent {
    fun calculateScrollHorizontally(scrollValue: Float): Float
    fun calculateScrollVertically(scrollValue: Float): Float
}