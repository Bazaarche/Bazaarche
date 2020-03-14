package com.asfoundation.wallet.view.rx;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import com.jakewharton.rxrelay2.PublishRelay;
import io.reactivex.Observable;

/**
 * Created by marcelobenites on 08/03/17.
 */

public class RxAlertDialog implements DialogInterface {

  private static final Object OBJECT = new Object();

  private final AlertDialog dialog;
  private final DialogClick negativeClick;
  private final View view;
  private final DialogClick positiveClick;
  private final CancelEvent cancelEvent;
  private final DismissEvent dismissEvent;

  protected RxAlertDialog(AlertDialog dialog, View view, DialogClick positiveClick,
      DialogClick negativeClick, CancelEvent cancelEvent, DismissEvent dismissEvent) {
    this.dialog = dialog;
    this.view = view;
    this.positiveClick = positiveClick;
    this.negativeClick = negativeClick;
    this.cancelEvent = cancelEvent;
    this.dismissEvent = dismissEvent;
  }

  public View getDialogView() {
    return view;
  }

  public void show() {
    dialog.show();
  }

  public boolean isShowing() {
    return dialog.isShowing();
  }

  public void changeMessage(String message) {
    dialog.setMessage(message);
  }

  @Override public void cancel() {
    dialog.cancel();
  }

  @Override public void dismiss() {
    dialog.dismiss();
  }

  public Observable<DialogInterface> positiveClicks() {
    if (positiveClick != null) {
      return positiveClick.clicks()
          .map(click -> this);
    }
    return Observable.empty();
  }

  public Observable<DialogInterface> negativeClicks() {
    if (negativeClick != null) {
      return negativeClick.clicks()
          .map(click -> this);
    }
    return Observable.empty();
  }

  public Observable<DialogInterface> cancels() {
    return cancelEvent.cancels()
        .map(click -> this);
  }

  public Observable<DialogInterface> dismisses() {
    return dismissEvent.dismisses()
        .map(click -> this);
  }

  public static class Builder {

    private final AlertDialog.Builder builder;

    private DialogClick positiveClick;
    private DialogClick negativeClick;
    private View view;

    public Builder(Context context) {
      this.builder = new AlertDialog.Builder(context);
    }

    public Builder setView(View view) {
      this.view = view;
      builder.setView(view);
      return this;
    }

    public Builder setTitle(@StringRes int titleId) {
      builder.setTitle(titleId);
      return this;
    }

    public Builder setMessage(@StringRes int messageId) {
      builder.setMessage(messageId);
      return this;
    }

    public Builder setMessage(@Nullable CharSequence message) {
      builder.setMessage(message);
      return this;
    }

    public Builder setPositiveButton(@StringRes int textId) {
      positiveClick = new DialogClick(DialogInterface.BUTTON_POSITIVE, PublishRelay.create());
      builder.setPositiveButton(textId, positiveClick);
      return this;
    }

    public Builder setPositiveButton(CharSequence text) {
      positiveClick = new DialogClick(DialogInterface.BUTTON_POSITIVE, PublishRelay.create());
      builder.setPositiveButton(text, positiveClick);
      return this;
    }

    public Builder setNegativeButton(@StringRes int textId) {
      negativeClick = new DialogClick(DialogInterface.BUTTON_NEGATIVE, PublishRelay.create());
      builder.setNegativeButton(textId, negativeClick);
      return this;
    }

    public Builder setNegativeButton(CharSequence text) {
      negativeClick = new DialogClick(DialogInterface.BUTTON_NEGATIVE, PublishRelay.create());
      builder.setNegativeButton(text, negativeClick);
      return this;
    }

    public RxAlertDialog build() {
      final AlertDialog dialog = builder.create();
      final CancelEvent cancelEvent = new CancelEvent(PublishRelay.create());
      final DismissEvent dismissEvent = new DismissEvent(PublishRelay.create());
      dialog.setOnCancelListener(cancelEvent);
      dialog.setOnDismissListener(dismissEvent);
      return new RxAlertDialog(dialog, view, positiveClick, negativeClick, cancelEvent,
          dismissEvent);
    }
  }

  protected static class DismissEvent implements DialogInterface.OnDismissListener {

    private final PublishRelay<Object> subject;

    public DismissEvent(PublishRelay<Object> subject) {
      this.subject = subject;
    }

    @Override public void onDismiss(DialogInterface dialog) {
      subject.accept(OBJECT);
    }

    public Observable<Object> dismisses() {
      return subject;
    }
  }

  protected static class CancelEvent implements DialogInterface.OnCancelListener {

    private final PublishRelay<Object> subject;

    public CancelEvent(PublishRelay<Object> subject) {
      this.subject = subject;
    }

    @Override public void onCancel(DialogInterface dialog) {
      subject.accept(OBJECT);
    }

    public Observable<Object> cancels() {
      return subject;
    }
  }

  protected static class DialogClick implements DialogInterface.OnClickListener {

    private final int which;
    private final PublishRelay<Object> subject;

    public DialogClick(int which, PublishRelay<Object> subject) {
      this.which = which;
      this.subject = subject;
    }

    @Override public void onClick(DialogInterface dialog, int which) {
      if (this.which == which) {
        subject.accept(OBJECT);
      }
    }

    public Observable<Object> clicks() {
      return subject;
    }
  }
}
