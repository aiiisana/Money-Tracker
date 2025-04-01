package com.fpis.money.utils


import android.content.Context
import android.content.SharedPreferences


class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveLoginDetails(login: String?, password: String?) {
        editor.putString(KEY_LOGIN, login)
        editor.putString(KEY_PASSWORD, password)
        editor.apply()
    }

    val login: String?
        get() = sharedPreferences.getString(KEY_LOGIN, null)
    val password: String?
        get() = sharedPreferences.getString(KEY_PASSWORD, null)

    fun clearLoginDetails() {
        editor.remove(KEY_LOGIN)
        editor.remove(KEY_PASSWORD)
        editor.apply()
    }

    companion object {
        private const val PREF_NAME = "loginPrefs"
        private const val KEY_LOGIN = "login"
        private const val KEY_PASSWORD = "password"
    }
}