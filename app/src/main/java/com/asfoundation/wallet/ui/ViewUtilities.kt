package com.asfoundation.wallet.ui

import android.content.Context
import android.graphics.drawable.InsetDrawable
import androidx.recyclerview.widget.DividerItemDecoration


fun createItemDecoration(context: Context, leftMargin: Int, rightMargin: Int): DividerItemDecoration {

  val attrs = intArrayOf(android.R.attr.listDivider)

  val typedArray = context.obtainStyledAttributes(attrs)
  val divider = typedArray.getDrawable(0)
  val insetDivider = InsetDrawable(divider, leftMargin, 0, rightMargin, 0)
  typedArray.recycle()

  val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
  itemDecoration.setDrawable(insetDivider)
  return itemDecoration
}