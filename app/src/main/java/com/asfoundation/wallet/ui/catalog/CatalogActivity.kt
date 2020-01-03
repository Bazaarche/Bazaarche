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

  val adapter = CatalogAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidInjection.inject(this)

    initView()
    observeData()
  }

  private fun initView() {

    setContentView(R.layout.activity_catalog)
    setupRecyclerView()
  }

  private fun observeData() {

    viewModel.getCatalogRows().observeNotNull(this) {

      adapter.addItems(it)
    }
  }

  private fun setupRecyclerView() {
    recyclerCatalog.also {

      it.layoutManager = LinearLayoutManager(this)
      it.adapter = adapter
    }
  }

}
