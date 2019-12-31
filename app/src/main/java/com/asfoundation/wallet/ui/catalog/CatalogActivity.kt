package com.asfoundation.wallet.ui.catalog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.wallet.R
import com.asfoundation.wallet.util.observeNotNull
import com.asfoundation.wallet.viewmodel.CatalogViewModel
import com.asfoundation.wallet.viewmodel.CatalogViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_catalog.*
import javax.inject.Inject

class CatalogActivity : AppCompatActivity() {

  @Inject
  lateinit var viewModelFactory: CatalogViewModelFactory
  private val viewModel: CatalogViewModel by lazy {
    ViewModelProviders.of(this, viewModelFactory).get(CatalogViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidInjection.inject(this)

    initView()
    observeData()
  }

  private fun initView() {

    setContentView(R.layout.activity_catalog)
    recyclerCatalog.apply {

      layoutManager = LinearLayoutManager(this@CatalogActivity)
    }
  }

  private fun observeData() {

    viewModel.getCatalogRows().observeNotNull(this) {

      //TODO
    }
  }

}
