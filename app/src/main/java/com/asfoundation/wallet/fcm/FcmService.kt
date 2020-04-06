package com.asfoundation.wallet.fcm

import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.asf.wallet.R
import com.asfoundation.wallet.di.Bazaarche
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import javax.inject.Inject

class FcmService : FirebaseMessagingService() {

  @Inject
  @Bazaarche
  internal lateinit var notificationBuilder: NotificationCompat.Builder

  @Inject
  internal lateinit var fcmInterActor: FcmInterActor

  override fun onCreate() {
    AndroidInjection.inject(this)
    super.onCreate()
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {

    if (remoteMessage.data.isNotEmpty()) {
      fcmInterActor.handleDataNotification(remoteMessage.data)
    } else {
      sendNotification(remoteMessage.notification ?: return)
    }

  }

  override fun onNewToken(token: String) {
    sendRegistrationToServer(token)
  }

  private fun sendRegistrationToServer(token: String) {
    //TODO: send token to server
  }

  private fun sendNotification(notification: RemoteMessage.Notification) {

    notificationBuilder.apply {
      setContentTitle(notification.title ?: getString(R.string.app_name))
      setContentText(notification.body)
    }

    val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java)

    notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
  }

  companion object {

    private const val NOTIFICATION_ID = 1
    const val NOTIFICATION_CHANNEL_ID = "fcm_notification_channel_id"
    const val NOTIFICATION_CHANNEL_NAME = "Notifications Channel"
  }
}