package com.goldenowl.ecommerceapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.goldenowl.ecommerceapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController

        setupBottomNavMenu(navController)


    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    private fun setupBottomNavMenu(navController: NavController) {
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.shopFragment, R.id.bagFragment, R.id.favoritesFragment, R.id.profileFragment, R.id.catalogFragment, R.id.warningFragment -> binding.bottomNavigation.visibility =
                    View.VISIBLE
                else -> binding.bottomNavigation.visibility = View.GONE
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item->
            NavigationUI.onNavDestinationSelected(item,navController)
            navController.popBackStack(item.itemId,inclusive = false)
            true
        }
    }

}