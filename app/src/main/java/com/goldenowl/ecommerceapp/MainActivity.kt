package com.goldenowl.ecommerceapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val splashScreen = installSplashScreen()
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.title = "Home"
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController
        setupBottomNavMenu(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    toolbar?.title = "Home"
                }
                R.id.shopFragment -> {
                    toolbar?.title = "Shop"
                }
                R.id.bagFragment -> {
                    toolbar?.title = "Bag"
                }
                R.id.favoritesFragment -> {
                    toolbar?.title = "Favorites"
                }
                R.id.profileFragment -> {
                    toolbar?.title = "Profile"
                }
            }
        }
//        val db = FirebaseFirestore.getInstance()
//        val user: MutableMap<String, Any> = HashMap()
//        user["first"] = "Ada"
//        user["last"] = "Lovelace"
//        user["born"] = 1815
//        db.collection("users")
//            .add(user)
//            .addOnSuccessListener(OnSuccessListener<DocumentReference> { documentReference ->
//                Log.d(
//                    TAG,
//                    "DocumentSnapshot added with ID: " + documentReference.id
//                )
//            })
//            .addOnFailureListener(OnFailureListener { e -> Log.w(TAG, "Error adding document", e) })
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setupWithNavController(navController)
    }

}