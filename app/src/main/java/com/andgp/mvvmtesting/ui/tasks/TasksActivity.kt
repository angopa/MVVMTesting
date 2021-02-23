package com.andgp.mvvmtesting.ui.tasks

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.andgp.mvvmtesting.R
import com.google.android.material.navigation.NavigationView

/**
 *  Created by Andres Gonzalez on 02/02/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *
 * Main activity for the todoapp. Holds the Navigation Host Fragment and the Drawer, Toolbar, etc.
 */
class TaskActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        setupNavigationDrawer()
        setSupportActionBar(findViewById(R.id.toolbar))

        val navigationController: NavController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration =
            AppBarConfiguration.Builder(R.id.tasks_fragment_dest, R.id.statistics_fragment_dest)
                .setDrawerLayout(drawerLayout)
                .build()

        setupActionBarWithNavController(navigationController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navigationController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout)).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
    }
}

//Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3