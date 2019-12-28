package com.asfoundation.wallet.ui.bazarchesettings

import android.content.Context
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {


  lateinit var interactionListener: InteractionListener

  @CallSuper
  override fun onAttach(context: Context) {
    super.onAttach(context)
    interactionListener = context as InteractionListener
  }

}
