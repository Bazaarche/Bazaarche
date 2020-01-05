package com.asfoundation.wallet.router;

import android.content.Context;
import android.content.Intent;

import com.asfoundation.wallet.ui.bazarchesettings.BazaarcheSettingsActivity;

public class SettingsRouter {
  public void open(Context context) {
    Intent intent = new Intent(context, BazaarcheSettingsActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    context.startActivity(intent);
  }
}
