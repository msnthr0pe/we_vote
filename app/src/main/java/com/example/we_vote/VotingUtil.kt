package com.example.we_vote

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

object VotingUtil {
    fun setBottomBar(access: String?, bottomNav: BottomNavigationView) {
        bottomNav.menu.clear()
        // В данном проекте меню для всех ролей содержит одинаковые базовые элементы,
        // включая профиль (ic_settings_off).
        when (access) {
            "admin", "developer" -> bottomNav.inflateMenu(R.menu.menu_bottom_nav_admin)
            else -> bottomNav.inflateMenu(R.menu.menu_bottom_nav_user)
        }
    }

    fun setupNavigation(fragment: Fragment, itemId: Int, vararg actionIdInOrder: Int): Boolean {
        // Убеждаемся, что передано ровно 4 ID действий для 4 кнопок
        if (actionIdInOrder.size != 4) return false
        
        return when (itemId) {
            R.id.nav_home ->  navigate(fragment, actionIdInOrder[0])
            R.id.nav_new_poll -> navigate(fragment, actionIdInOrder[1])
            R.id.nav_profile -> navigate(fragment, actionIdInOrder[2])
            R.id.nav_archive -> navigate(fragment, actionIdInOrder[3])
            else -> false
        }
    }

    private fun navigate(fragment: Fragment, id: Int): Boolean {
        try {
            fragment.findNavController().navigate(id)
        } catch (e: Exception) {
            return false
        }
        return true
    }
}
