package com.asfoundation.wallet.ui.catalog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.wallet.R
import kotlinx.android.synthetic.main.activity_catalog.*

class CatalogActivity : AppCompatActivity() {


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    initView()
  }

  private fun initView() {

    setContentView(R.layout.activity_catalog)
    recyclerCatalog.apply {

      layoutManager = LinearLayoutManager(this@CatalogActivity)
    }
  }

}
