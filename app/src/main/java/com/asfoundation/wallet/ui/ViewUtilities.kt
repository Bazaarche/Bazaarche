package com.asfoundation.wallet.ui

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import java.util.*
import kotlin.math.roundToInt


fun createItemDecoration(context: Context,
                         startMargin: Int,
                         endMargin: Int): DividerItemDecoration {

  val dividerDrawable = getDividerDrawable(context, startMargin, endMargin)

  val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
  itemDecoration.setDrawable(dividerDrawable)
  return itemDecoration
}

fun getDividerDrawable(context: Context, startMargin: Int, endMargin: Int): Drawable {

  val divider = getDividerDrawable(context)

  val (leftInset, rightInset) = getLeftAndRightInset(startMargin, endMargin)

  return InsetDrawable(divider, leftInset, 0, rightInset, 0)
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

private fun getDividerDrawable(context: Context): Drawable {
  val attrs = intArrayOf(android.R.attr.listDivider)

  val typedArray = context.obtainStyledAttributes(attrs)
  val dividerDrawable = typedArray.getDrawable(0)!!
  typedArray.recycle()
  return dividerDrawable
}

private fun getLeftAndRightInset(startInset: Int, endInset: Int): LeftAndRightInset {
  val isLTR =
      TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_LTR

  return if (isLTR) {
    startInset to endInset
  } else {
    endInset to startInset
  }
}

typealias LeftAndRightInset = Pair<Int, Int>