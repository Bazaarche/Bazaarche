package com.asfoundation.wallet.util.languagecontroller

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager.GET_META_DATA
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.LocaleList
import android.preference.PreferenceManager
import java.util.*

class LanguageController private constructor(application: Application, private val store: LocaleStore) {

  init {
    registerApplicationCallbacks(application)
    setLocale(application, store.getLocale())
  }

  fun setLocale(context: Context, language: Language) {
    setLocale(context, Locale(language.languageString))
  }

  fun setLocale(context: Context, locale: Locale) {
    store.persistLocale(locale)
    update(context, locale)
  }

  private fun getLocale(): Locale {
    return store.getLocale()
  }

  fun getLanguage(): Language {
    val languageString = verifyLanguage(getLocale().language)
    return Language.of(languageString)
  }

  private fun verifyLanguage(language: String): String {
    // get rid of deprecated language tags
    return when (language) {
      "iw" -> "he"
      "ji" -> "yi"
      "in" -> "id"
      else -> language
    }
  }

  private fun registerApplicationCallbacks(application: Application) {
    application.registerActivityLifecycleCallbacks(LanguageControllerActivityLifecycleCallbacks(this))
    application.registerComponentCallbacks(LanguageControllerApplicationCallbacks(application, this))
  }

  internal fun setLocaleInternal(context: Context) {
    update(context, store.getLocale())
  }

  private fun update(context: Context, locale: Locale) {
    updateResources(context, locale)
    val appContext = context.applicationContext
    if (appContext !== context) {
      updateResources(appContext, locale)
    }
  }

  @Suppress("DEPRECATION")
  private fun updateResources(context: Context, locale: Locale) {
    Locale.setDefault(locale)

    val res = context.resources
    val current = res.configuration.getLocaleCompat()

    if (current == locale) return

    val config = Configuration(res.configuration)
    when {
      isAtLeastSdkVersion(VERSION_CODES.N) -> setLocaleForApi24(config, locale)
      isAtLeastSdkVersion(VERSION_CODES.JELLY_BEAN_MR1) -> config.setLocale(locale)
      else -> config.locale = locale
    }
    res.updateConfiguration(config, res.displayMetrics)
  }

  @SuppressLint("NewApi")
  private fun setLocaleForApi24(config: Configuration, locale: Locale) {
    // bring the target locale to the front of the list
    val set = linkedSetOf(locale)

    val defaultLocales = LocaleList.getDefault()
    val all = List<Locale>(defaultLocales.size()) { defaultLocales[it] }
    // append other locales supported by the user
    set.addAll(all)

    config.locales = LocaleList(*all.toTypedArray())
  }

  internal fun resetActivityTitle(activity: Activity) {
    try {
      val pm = activity.packageManager
      val info = pm.getActivityInfo(activity.componentName, GET_META_DATA)
      if (info.labelRes != 0) {
        activity.setTitle(info.labelRes)
      }
    } catch (e: NameNotFoundException) {
      e.printStackTrace()
    }
  }

  @Suppress("DEPRECATION")
  private fun Configuration.getLocaleCompat(): Locale {
    return if (isAtLeastSdkVersion(VERSION_CODES.N)) locales.get(0) else locale
  }

  private fun isAtLeastSdkVersion(versionCode: Int): Boolean {
    return Build.VERSION.SDK_INT >= versionCode
  }

  companion object {

    private lateinit var instance: LanguageController

    @JvmStatic
    fun getInstance(): LanguageController {
      check(::instance.isInitialized) { "LanguageController should be initialized first" }
      return instance
    }

    @JvmStatic
    fun init(application: Application, defaultLanguage: Language): LanguageController {
      check(!::instance.isInitialized) { "Already initialized" }
      val locale = Locale(defaultLanguage.languageString)
      val preferences = PreferenceManager.getDefaultSharedPreferences(application)
      val localeStore = PreferenceLocaleStore(preferences, locale)
      instance = LanguageController(application, localeStore)
      return instance
    }

  }

}