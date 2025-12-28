package com.example.movieapp.common

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale
import androidx.core.content.edit

object LocaleHelper {
    private const val PREF_NAME = "settings"
    private const val KEY_LANGUAGE = "app_language"
    fun saveLanguage(context: Context, languageCode: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit(commit = true) { putString(KEY_LANGUAGE, languageCode) }
    }

    // 2. Kayıtlı dili okumak için
    fun getLanguage(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "tr") ?: "tr"
    }

    fun onAttach(context: Context): Context {
        val lang = getLanguage(context)
        return setLocale(context, lang)
    }

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)

        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    // Anlık resource güncellemesi (titremeyi önlemek için)
    fun updateResources(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
}