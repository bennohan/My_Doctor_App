package com.bennohan.mydoctorapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.databinding.ActivityNavigationBinding
import com.bennohan.mydoctorapp.ui.history.HistoryFragment
import com.bennohan.mydoctorapp.ui.save.SaveFragment
import com.crocodic.core.base.activity.NoViewModelActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationActivity :
    NoViewModelActivity<ActivityNavigationBinding>(R.layout.activity_navigation) {

    private val historyFragment = HistoryFragment()
    private val homeFragment = HomeFragment()
    private val savedFragment = SaveFragment()

    val fragmentManager = supportFragmentManager
    val fragment = fragmentManager.findFragmentById(R.id.home_fragment) as? HomeFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        replaceFragment(homeFragment)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.action_home
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    replaceFragment(homeFragment)
                    true
                }
                R.id.action_history -> {
                    replaceFragment(historyFragment)
                    true
                }
                R.id.action_saved -> {
                    replaceFragment(savedFragment)
                    true
                }
                // Add more cases for each menu item
                else -> false
            }
        }

    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.coordinator, fragment)
            .commit()
    }


}