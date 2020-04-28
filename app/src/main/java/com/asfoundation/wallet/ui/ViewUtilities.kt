package com.asfoundation.wallet.ui

import android.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import kotlin.math.roundToInt


fun createItemDecoration(context: Context, leftMargin: Int, rightMargin: Int): DividerItemDecoration {

  val dividerDrawable = getDividerDrawable(context, leftMargin, rightMargin)

  val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
  itemDecoration.setDrawable(dividerDrawable)
  return itemDecoration
}

fun getDividerDrawable(context: Context, leftMargin: Int, rightMargin: Int): Drawable {
  val attrs = intArrayOf(R.attr.listDivider)

  val typedArray = context.obtainStyledAttributes(attrs)
  val divider = typedArray.getDrawable(0)
  val insetDivider = InsetDrawable(divider, leftMargin, 0, rightMargin, 0)

  typedArray.recycle()

  return insetDivider
}

fun Context.dp(px: Int): Int {
  return (px * resources.displayMetrics.density).roundToInt()
}

fun View.toggleVisibility(visible: Boolean) {
  visibility = if (visible) {
    View.VISIBLE
  } else {
    View.GONE
  }
}