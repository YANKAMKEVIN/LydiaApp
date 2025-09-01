package com.kev.lydia.ui.navigation

object LydiaRoutes {
    const val HOME = "home"
    const val DETAIL_ROUTE = "detail/{startIndex}"
    const val DETAIL_ARG = "startIndex"

    fun detail(startIndex: Int): String = "detail/$startIndex"
}