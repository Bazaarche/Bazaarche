package com.asfoundation.wallet.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.asf.wallet.R
import com.asfoundation.wallet.fcm.FcmService
import com.asfoundation.wallet.repository.PreferencesDataSource
import com.asfoundation.wallet.repository.PreferencesDataSourceImpl
import com.asfoundation.wallet.ui.catalog.CatalogActivity
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class BazaarcheToolsModule {


  @Binds
  abstract fun bindsPreferencesDataSource(preferencesDataSource: PreferencesDataSourceImpl): PreferencesDataSource

  @Module
  companion object {

    @JvmStatic
    @Bazaarche
    @Provides
    fun provideNotificationBuilder(context: Context): NotificationCompat.Builder {

      val intent = Intent(context, CatalogActivity::class.java)
      intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
      val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

      val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

      return NotificationCompat.Builder(context, FcmService.NOTIFICATION_CHANNEL_ID)
          .setSmallIcon(R.mipmap.ic_launcher)
          .setContentTitle(context.getString(R.string.app_name))
          .setAutoCancel(true)
          .setSound(defaultSoundUri)
          .setContentIntent(pendingIntent)
    }

  }

}