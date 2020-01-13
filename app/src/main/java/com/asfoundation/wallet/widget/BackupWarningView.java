package com.asfoundation.wallet.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.asf.wallet.R;
import com.asfoundation.wallet.entity.Wallet;
import com.asfoundation.wallet.ui.widget.OnBackupClickListener;

public class BackupWarningView extends FrameLayout implements View.OnClickListener {

  private OnBackupClickListener onPositiveClickListener;
  private Wallet wallet;
  private OnClickListener onSkipClickListener;

  public BackupWarningView(@NonNull Context context) {
    this(context, null);
  }

  public BackupWarningView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BackupWarningView(@NonNull Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    init();
  }

  private void init() {
    setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
    LayoutInflater.from(getContext())
        .inflate(R.layout.layout_dialog_warning_backup, this, true);
    findViewById(R.id.backup_action).setOnClickListener(this);
    findViewById(R.id.skip_button).setOnClickListener(v -> onSkipClickListener.onClick(v));
    /* Disabled due to https://github.com/TrustWallet/trust-wallet-android/issues/107
     * findViewById(R.id.later_action).setOnClickListener(this);
     */
  }

  @Override public void onClick(View v) {
    if (v.getId() == R.id.backup_action && onPositiveClickListener != null) {
      onPositiveClickListener.onBackupClick(v, wallet);
    }
  }

  public void setOnPositiveClickListener(OnBackupClickListener onPositiveClickListener) {
    this.onPositiveClickListener = onPositiveClickListener;
  }

  public void setOnSkipClickListener(OnClickListener listener) {
    onSkipClickListener = listener;
  }

  public void show(Wallet wallet) {
    setVisibility(VISIBLE);
    this.wallet = wallet;
  }

  public void hide() {
    setVisibility(GONE);
  }
}
