package com.asfoundation.wallet.ui.catalog

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import com.asfoundation.wallet.BAZAARCHE_ABOUT_URL
import com.asfoundation.wallet.navigator.AppsNavigator
import com.asfoundation.wallet.ui.bazarchesettings.BazaarcheSettingsActivity
import com.asfoundation.wallet.ui.getDividerDrawable
import com.asfoundation.wallet.ui.iab.WebViewActivity
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

  @Inject
  lateinit var appsNavigator: AppsNavigator

  private lateinit var viewModel: CatalogViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidInjection.inject(this)

    initView()

    viewModel = ViewModelProviders.of(this, viewModelFactory).get(CatalogViewModel::class.java)
    observeData()
  }

  private fun initView() {

    setContentView(R.layout.activity_catalog)
    setupRecyclerView()
    setSettingsImageClickListener()
    setHelpImageClickListener()
  }

  private fun observeData() {

    viewModel.getCatalogRows().observeNotNull(this) {

      val adapter = CatalogAdapter(appsNavigator)
      recyclerCatalog.adapter = adapter

      adapter.addItems(it)
    }
  }

  private fun setupRecyclerView() {

    fun createItemDecoration(): RecyclerView.ItemDecoration {

      val startMargin = resources.getDimensionPixelSize(R.dimen.source_image_size)
      val dividerDrawable = getDividerDrawable(this, startMargin, 0)

      val itemDecoration = CatalogDividerItemDecoration(this, VERTICAL, R.layout.item_app)
      itemDecoration.setDrawable(dividerDrawable)

      return itemDecoration
    }

    recyclerCatalog.apply {

      layoutManager = LinearLayoutManager(this@CatalogActivity)
      addItemDecoration(createItemDecoration())
    }

  }

  private fun setSettingsImageClickListener() {
    imageSettings.setOnClickListener {
      val intent = Intent(this, BazaarcheSettingsActivity::class.java)
      startActivity(intent)
    }

  }

  private fun setHelpImageClickListener() {

    imageHelp.setOnClickListener {
      val intent = WebViewActivity.newIntent(this, BAZAARCHE_ABOUT_URL)
      startActivity(intent)
    }
  }

}
