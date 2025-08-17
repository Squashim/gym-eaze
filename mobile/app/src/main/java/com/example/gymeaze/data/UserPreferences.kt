package com.example.gymeaze.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences (private val context: Context) {
    companion object{
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val IS_FIRST_LOGIN = booleanPreferencesKey("is_first_login")
        val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USERNAME = stringPreferencesKey("username")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map {
        preferences -> preferences[IS_DARK_THEME] ?: true
    }

    suspend fun setDarkTheme(bool: Boolean){
        context.dataStore.edit {
            preferences -> preferences[IS_DARK_THEME] = bool
        }
    }

    val firstLogin: Flow<Boolean> = context.dataStore.data.map {
        preferences -> preferences[IS_FIRST_LOGIN] ?: true
    }

    suspend fun setFirstLogin(bool: Boolean){
        context.dataStore.edit {
            preferences -> preferences[IS_FIRST_LOGIN] = bool
        }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map {
        preferences -> preferences[IS_USER_LOGGED_IN] ?: false
    }

    suspend fun setLoggedIn(bool: Boolean){
        context.dataStore.edit {
            preferences -> preferences[IS_USER_LOGGED_IN] = bool
        }
    }

    val email: Flow<String> = context.dataStore.data.map {
        preferences -> preferences[USER_EMAIL] ?: ""
    }

    suspend fun setEmail(email: String){
        context.dataStore.edit {
            preferences -> preferences[USER_EMAIL] = email
        }
    }

    val username: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USERNAME] ?: ""
    }

    suspend fun setUsername(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = name
        }
    }

    suspend fun clearData() {
        context.dataStore.edit{
            preferences -> preferences[IS_USER_LOGGED_IN] = false
            preferences[USER_EMAIL] = ""
            preferences[USERNAME] = ""
        }
    }
}