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

    private val dataStore = context.dataStore

    companion object{
        private val ACCESS_TOKEN =  stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        private val IS_FIRST_LOGIN = booleanPreferencesKey("is_first_login")
        private val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USERNAME = stringPreferencesKey("username")
        private val USER_ID = stringPreferencesKey("user_id")
    }

    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }
    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN]
    }

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val firstLogin: Flow<Boolean> = context.dataStore.data.map {
            preferences -> preferences[IS_FIRST_LOGIN] ?: true
    }
    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map {
            preferences -> preferences[IS_DARK_THEME] ?: true
    }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map {
            preferences -> preferences[IS_USER_LOGGED_IN] ?: false
    }

    val email: Flow<String> = context.dataStore.data.map {
            preferences -> preferences[USER_EMAIL] ?: ""
    }

    val username: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USERNAME] ?: ""
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveUserData(userId: String, username: String, email: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USERNAME] = username
            preferences[USER_EMAIL] = email
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USERNAME)
            preferences.remove(USER_EMAIL)
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    suspend fun setDarkTheme(bool: Boolean){
        context.dataStore.edit {
            preferences -> preferences[IS_DARK_THEME] = bool
        }
    }

    suspend fun setFirstLogin(bool: Boolean){
        context.dataStore.edit {
            preferences -> preferences[IS_FIRST_LOGIN] = bool
        }
    }

    suspend fun setLoggedIn(bool: Boolean){
        context.dataStore.edit {
            preferences -> preferences[IS_USER_LOGGED_IN] = bool
        }
    }


    suspend fun setEmail(email: String){
        context.dataStore.edit {
            preferences -> preferences[USER_EMAIL] = email
        }
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

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}