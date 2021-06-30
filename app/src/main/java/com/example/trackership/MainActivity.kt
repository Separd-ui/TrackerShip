package com.example.trackership

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trackership.databinding.ActivityMainBinding
import com.example.trackership.utils.Constans
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment=supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController=navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /* NOTHING TO DI*/ }

        navigateToTrackingFragment(intent)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title=""

        navController.addOnDestinationChangedListener{
            _,destination,_->
            when(destination.id){
                R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment->{
                    binding.bottomNavigationView.visibility=View.VISIBLE
                }
                else->{
                    binding.bottomNavigationView.visibility=View.GONE
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragment(intent)
    }

    private fun navigateToTrackingFragment(intent:Intent?){
        intent?.let{
            if(it.action==Constans.ACTION_SHOW_TRACKING_FRAGMENT){
                navHostFragment.findNavController().navigate(R.id.action_global_ToTrackingFragment)
            }
        }
    }
}