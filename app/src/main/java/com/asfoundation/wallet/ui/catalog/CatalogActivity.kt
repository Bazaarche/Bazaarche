package com.asfoundation.wallet.ui.catalog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import com.asfoundation.wallet.navigator.CatalogViewNavigator
import com.asfoundation.wallet.ui.getDividerDrawable
import com.asfoundation.wallet.ui.widget.CatalogDividerItemDecoration
import com.asfoundation.wallet.ui.widget.CatalogDividerItemDecoration.VERTICAL
import com.asfoundation.wallet.util.observeNotNull
import com.asfoundation.wallet.viewmodel.CatalogViewModel
import com.asfoundation.wallet.viewmodel.CatalogViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_catalog.*
import kotlinx.android.synthetic.main.layout_catalog_appbar.*
import javax.inject.Inject

class CatalogActivity : AppCompatActivity() {

  @Inject
  lateinit var viewModelFactory: CatalogViewModelFactory
  private val viewModel: CatalogViewModel by lazy {
    ViewModelProviders.of(this, viewModelFactory).get(CatalogViewModel::class.java)
  }

  @Inject
  lateinit var catalogViewNavigator: CatalogViewNavigator

  private val adapter by lazy { CatalogAdapter(catalogViewNavigator) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidInjection.inject(this)

    initView()
    observeData()
  }

  override fun onDestroy() {
    catalogViewNavigator.destroy()
    super.onDestroy()
  }

  private fun initView() {

    setContentView(R.layout.activity_catalog)
    setupRecyclerView()
    setSettingsImageClickListener()
  }

  private fun observeData() {

    viewModel.getCatalogRows().observeNotNull(this) {

      adapter.addItems(it)
    }
  }

  private fun setupRecyclerView() {

    fun createItemDecoration(): RecyclerView.ItemDecoration {

      val rightMargin = resources.getDimensionPixelSize(R.dimen.app_image_size)
      val dividerDrawable = getDividerDrawable(this, 0, rightMargin)

      val itemDecoration = CatalogDividerItemDecoration(this, VERTICAL, R.layout.item_app)
      itemDecoration.setDrawable(dividerDrawable)

      return itemDecoration
    }

    recyclerCatalog.also {

      it.layoutManager = LinearLayoutManager(this)
      it.adapter = adapter

      it.addItemDecoration(createItemDecoration())
    }

  }

  private fun setSettingsImageClickListener() {
    imageSettings.setOnClickListener {
      catalogViewNavigator.openSettings()
    }

  }

}
