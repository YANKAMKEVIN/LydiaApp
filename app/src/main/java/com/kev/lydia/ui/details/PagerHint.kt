package com.kev.lydia.ui.details

/**
 * Direction of the swipe hint played on the first contact opened: towards the next contact
 * (1), towards the previous one when the last contact is open (-1), or none when there is only
 * one contact (0).
 */
fun pagerHintDirection(index: Int, count: Int): Int = when {
    count < 2 -> 0
    index < count - 1 -> 1
    else -> -1
}
