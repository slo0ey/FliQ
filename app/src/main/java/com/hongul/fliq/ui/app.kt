package com.hongul.fliq.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.navigation.compose.rememberNavController
import com.hongul.fliq.api.Api
import com.hongul.fliq.model.user.User
import com.hongul.fliq.ui.ProfileStore.dataStore
import com.hongul.fliq.ui.juwon.FliQLoginScreen
import com.hongul.fliq.ui.navigation.BottomNavigation
import com.hongul.fliq.ui.navigation.NavigationGraph
import kotlinx.coroutines.launch

@Composable
fun App() {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var me by rememberSaveable { mutableStateOf<User?>(null) }
    var showNavigation by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        context.dataStore.data.collect {
            if (me != null) return@collect

            it[ProfileStore.profile_id]?.let {
                me = Api.user.getUser(it).body()!!.toUser()
            }
        }
    }

    CompositionLocalProvider(Me provides me) {
        if (me == null) {
            FliQLoginScreen(onLogin = {
                me = it
                coroutineScope.launch {
                    context.dataStore.edit { preferences ->
                        preferences[ProfileStore.profile_id] = it.id
                    }
                }
            })
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                NavigationGraph(navController = navController) {
                    showNavigation = it
                }

                if (showNavigation) {
                    BottomNavigation(navController = navController)
                }
            }
        }
    }
}