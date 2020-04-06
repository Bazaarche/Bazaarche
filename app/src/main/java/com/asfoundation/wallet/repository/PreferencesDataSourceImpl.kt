package com.asfoundation.wallet.repository

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferencesDataSourceImpl @Inject constructor(context: Context) : PreferencesDataSource {

  private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

  override var adId by StringPrefProperty(PREF_AD_ID)
  override var lowestSupportedVersion: Int by IntPrefProperty(LOWEST_SUPPORTED_VERSION)

  companion object {
    private const val PREFERENCES_FILE_NAME = "bazaarche_preferences"
    private const val PREF_AD_ID = "ad_id"
    private const val LOWEST_SUPPORTED_VERSION = "lowest_supported_version"
  }


  private class StringPrefProperty(private val key: String) : ReadWriteProperty<PreferencesDataSourceImpl, String> {

    override fun getValue(thisRef: PreferencesDataSourceImpl, property: KProperty<*>): String {
      return thisRef.preferences.getString(key, "")!!
    }

    override fun setValue(thisRef: PreferencesDataSourceImpl, property: KProperty<*>, value: String) {
      thisRef.preferences.edit().putString(key, value).apply()
    }
  }


  private class IntPrefProperty(private val key: String) :
      ReadWriteProperty<PreferencesDataSourceImpl, Int> {

    override fun getValue(thisRef: PreferencesDataSourceImpl, property: KProperty<*>): Int {
      return thisRef.preferences.getInt(key, 0)
    }

    override fun setValue(thisRef: PreferencesDataSourceImpl, property: KProperty<*>, value: Int) {
      thisRef.preferences.edit().putInt(key, value).apply()
    }
  }
}

