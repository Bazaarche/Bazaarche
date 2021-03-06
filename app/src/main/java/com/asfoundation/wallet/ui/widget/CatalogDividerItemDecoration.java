/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asfoundation.wallet.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Similar to {@link androidx.recyclerview.widget.DividerItemDecoration} but just draw Divider for
 * {@link  #toDrawItemViewType} view type and skip others
 */
public class CatalogDividerItemDecoration extends RecyclerView.ItemDecoration {
  public static final int HORIZONTAL = DividerItemDecoration.HORIZONTAL;
  public static final int VERTICAL = DividerItemDecoration.VERTICAL;

  private static final String TAG = "DividerItem";
  private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

  private final int toDrawItemViewType;

  private Drawable mDivider;

  /**
   * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
   */
  private int mOrientation;

  private final Rect mBounds = new Rect();

  /**
   * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
   * {@link androidx.recyclerview.widget.LinearLayoutManager}.
   *
   * @param context            Current context, it will be used to access resources.
   * @param orientation        Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
   * @param toDrawItemViewType item view type to draw divider in it.
   */
  public CatalogDividerItemDecoration(Context context, int orientation, int toDrawItemViewType) {
    final TypedArray a = context.obtainStyledAttributes(ATTRS);
    mDivider = a.getDrawable(0);
    if (mDivider == null) {
      Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
          + "DividerItemDecoration. Please set that attribute all call setDrawable()");
    }
    a.recycle();
    setOrientation(orientation);
    this.toDrawItemViewType = toDrawItemViewType;
  }

  /**
   * Sets the orientation for this divider. This should be called if
   * {@link RecyclerView.LayoutManager} changes orientation.
   *
   * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
   */
  public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL && orientation != VERTICAL) {
      throw new IllegalArgumentException(
          "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
    }
    mOrientation = orientation;
  }

  /**
   * Sets the {@link Drawable} for this divider.
   *
   * @param drawable Drawable that should be used as a divider.
   */
  public void setDrawable(@NonNull Drawable drawable) {
    if (drawable == null) {
      throw new IllegalArgumentException("Drawable cannot be null.");
    }
    mDivider = drawable;
  }

  /**
   * @return the {@link Drawable} for this divider.
   */
  @Nullable
  public Drawable getDrawable() {
    return mDivider;
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    if (parent.getLayoutManager() == null || mDivider == null) {
      return;
    }
    if (mOrientation == VERTICAL) {
      drawVertical(c, parent);
    } else {
      drawHorizontal(c, parent);
    }
  }

  private void drawVertical(Canvas canvas, RecyclerView parent) {
    canvas.save();
    final int left;
    final int right;
    //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
    left = parent.getPaddingLeft();
    right = parent.getWidth() - parent.getPaddingRight();
    canvas.clipRect(left, parent.getPaddingTop(), right,
        parent.getHeight() - parent.getPaddingBottom());

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      if (canSkipDrawingDivider(parent, child)) continue;
      parent.getDecoratedBoundsWithMargins(child, mBounds);
      final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
      final int top = bottom - mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(canvas);
    }
    canvas.restore();
  }

  private void drawHorizontal(Canvas canvas, RecyclerView parent) {
    canvas.save();
    final int top;
    final int bottom;
    //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
    top = parent.getPaddingTop();
    bottom = parent.getHeight() - parent.getPaddingBottom();
    canvas.clipRect(parent.getPaddingLeft(), top,
        parent.getWidth() - parent.getPaddingRight(), bottom);

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      if (canSkipDrawingDivider(parent, child)) continue;
      parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
      final int right = mBounds.right + Math.round(child.getTranslationX());
      final int left = right - mDivider.getIntrinsicWidth();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(canvas);
    }
    canvas.restore();
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                             RecyclerView.State state) {
    if (mDivider == null) {
      outRect.set(0, 0, 0, 0);
      return;
    }

    if (canSkipDrawingDivider(parent, view)) {
      outRect.set(0, 0, 0, 0);
      return;
    }

    if (mOrientation == VERTICAL) {
      outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    } else {
      outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    }
  }

  private boolean canSkipDrawingDivider(RecyclerView parent, View child) {
    int position = parent.getChildAdapterPosition(child);
    RecyclerView.Adapter adapter = parent.getAdapter();

    return adapter == null || adapter.getItemViewType(position) != toDrawItemViewType;
  }

}