package com.hongul.fliq.ui

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hongul.fliq.model.user.User

object ProfileStore {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore("profile")
    val profile_id = longPreferencesKey("profile_id")
}

val Me = staticCompositionLocalOf<User?> { null }
