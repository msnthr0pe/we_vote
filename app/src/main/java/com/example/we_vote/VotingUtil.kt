package com.example.we_vote

import com.google.android.material.bottomnavigation.BottomNavigationView

object VotingUtil {
    fun setBottomBar(access: String?, bottomNav: BottomNavigationView) {
        if (access == "admin") {
            bottomNav.menu.clear()
            bottomNav.inflateMenu(R.menu.menu_bottom_nav_admin)
        } else {
            bottomNav.menu.clear()
            bottomNav.inflateMenu(R.menu.menu_bottom_nav_user)
        }
    }
}