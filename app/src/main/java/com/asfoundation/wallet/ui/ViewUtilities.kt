package com.asfoundation.wallet.ui

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
  val attrs = intArrayOf(android.R.attr.listDivider)

  val typedArray = context.obtainStyledAttributes(attrs)
  val divider = typedArray.getDrawable(0)

  val isLTR =
      TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_LTR
  val (leftInset, rightInset) = getLeftAndRightInset(isLTR, startMargin, endMargin)

  val insetDivider = InsetDrawable(divider, leftInset, 0, rightInset, 0)

  typedArray.recycle()

  return insetDivider
}

fun Context.dp(px: Int): Int {
  return (px * resources.displayMetrics.density).roundToInt()
}

private fun getLeftAndRightInset(isLTR: Boolean, startMargin: Int, endMargin: Int): Pair<Int, Int> =
    if (isLTR) {
      startMargin to endMargin
    } else {
      endMargin to startMargin
    }
