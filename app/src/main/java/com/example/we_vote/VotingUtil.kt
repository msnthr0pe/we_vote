package com.example.we_vote

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

object VotingUtil {
    fun setBottomBar(access: String?, bottomNav: BottomNavigationView) {
        // Очищаем и инфлейтим общее меню. Теперь Профиль доступен всем.
        bottomNav.menu.clear()
        bottomNav.inflateMenu(R.menu.menu_bottom_nav_admin)
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
        // Предотвращаем повторный переход на тот же фрагмент, если это не предусмотрено логикой "self"
        // Но в данном проекте используются action_..._self, поэтому вызываем напрямую
        try {
            fragment.findNavController().navigate(id)
        } catch (e: Exception) {
            return false
        }
        return true
    }
}
