package com.asfoundation.wallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import com.asf.wallet.R;
import com.asfoundation.wallet.C;
import com.asfoundation.wallet.entity.GasSettings;
import com.asfoundation.wallet.entity.NetworkInfo;
import com.asfoundation.wallet.util.BalanceUtils;
import com.asfoundation.wallet.util.CurrencyFormatUtils;
import com.asfoundation.wallet.util.WalletCurrency;
import com.asfoundation.wallet.viewmodel.GasSettingsViewModel;
import com.asfoundation.wallet.viewmodel.GasSettingsViewModelFactory;
import dagger.android.AndroidInjection;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.inject.Inject;

public class GasSettingsActivity extends BaseActivity {

  @Inject GasSettingsViewModelFactory viewModelFactory;
  GasSettingsViewModel viewModel;
  private CurrencyFormatUtils currencyFormatUtils;
  private TextView gasPriceText;
  private TextView gasLimitText;
  private TextView networkFeeText;
  private TextView gasPriceInfoText;
  private TextView gasLimitInfoText;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    AndroidInjection.inject(this);

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_gas_settings);
    toolbar();

    currencyFormatUtils = CurrencyFormatUtils.Companion.create();
    SeekBar gasPriceSlider = findViewById(R.id.gas_price_slider);
    SeekBar gasLimitSlider = findViewById(R.id.gas_limit_slider);
    gasPriceText = findViewById(R.id.gas_price_text);
    gasLimitText = findViewById(R.id.gas_limit_text);
    networkFeeText = findViewById(R.id.text_network_fee);
    gasPriceInfoText = findViewById(R.id.gas_price_info_text);
    gasLimitInfoText = findViewById(R.id.gas_limit_info_text);

    gasPriceSlider.setPadding(0, 0, 0, 0);
    gasLimitSlider.setPadding(0, 0, 0, 0);

    viewModel = ViewModelProviders.of(this, viewModelFactory)
        .get(GasSettingsViewModel.class);

    BigInteger gasPrice = new BigInteger(getIntent().getStringExtra(C.EXTRA_GAS_PRICE));
    BigInteger gasLimit = new BigInteger(getIntent().getStringExtra(C.EXTRA_GAS_LIMIT));
    BigInteger gasLimitMin = BigInteger.valueOf(C.GAS_LIMIT_MIN);
    BigInteger gasLimitMax = BigInteger.valueOf(C.GAS_LIMIT_MAX);
    BigInteger gasPriceMin = BigInteger.valueOf(C.GAS_PRICE_MIN);
    BigInteger networkFeeMax = BigInteger.valueOf(C.NETWORK_FEE_MAX);

    final int gasPriceMinGwei = BalanceUtils.weiToGweiBI(gasPriceMin)
        .intValue();
    gasPriceSlider.setMax(BalanceUtils.weiToGweiBI(networkFeeMax.divide(gasLimitMax))
        .subtract(BigDecimal.valueOf(gasPriceMinGwei))
        .intValue());
    int gasPriceProgress = BalanceUtils.weiToGweiBI(gasPrice)
        .subtract(BigDecimal.valueOf(gasPriceMinGwei))
        .intValue();
    gasPriceSlider.setProgress(gasPriceProgress);
    gasPriceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        viewModel.gasPrice()
            .setValue(BalanceUtils.gweiToWei(BigDecimal.valueOf(progress + gasPriceMinGwei)));
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });

    gasLimitSlider.setMax(gasLimitMax.subtract(gasLimitMin)
        .intValue());
    gasLimitSlider.setProgress(gasLimit.subtract(gasLimitMin)
        .intValue());
    gasLimitSlider.refreshDrawableState();
    gasLimitSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress = progress / 100;
        progress = progress * 100;
        viewModel.gasLimit()
            .setValue(BigInteger.valueOf(progress)
                .add(gasLimitMin));
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });

    viewModel.gasPrice()
        .observe(this, this::onGasPrice);
    viewModel.gasLimit()
        .observe(this, this::onGasLimit);
    viewModel.defaultNetwork()
        .observe(this, this::onDefaultNetwork);

    viewModel.gasPrice()
        .setValue(gasPrice);
    viewModel.gasLimit()
        .setValue(gasLimit);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_save) {
      Intent intent = new Intent();
      intent.putExtra(C.EXTRA_GAS_SETTINGS, new GasSettings(new BigDecimal(viewModel.gasPrice()
          .getValue()), new BigDecimal(viewModel.gasLimit()
          .getValue())));
      setResult(RESULT_OK, intent);
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onResume() {

    super.onResume();

    viewModel.prepare();
  }

  private void onDefaultNetwork(NetworkInfo network) {
    gasPriceInfoText.setText(
        getString(R.string.info_gas_price).replace(C.ETHEREUM_NETWORK_NAME, network.name));
    gasLimitInfoText.setText(
        getString(R.string.info_gas_limit).replace(C.ETHEREUM_NETWORK_NAME, network.symbol));
  }

  private void onGasPrice(BigInteger price) {
    BigDecimal priceStr = BalanceUtils.weiToGwei(new BigDecimal(price));
    String formattedPrice =
        currencyFormatUtils.formatTransferCurrency(priceStr, WalletCurrency.ETHEREUM)
            + " "
            + C.GWEI_UNIT;
    gasPriceText.setText(formattedPrice);

    updateNetworkFee();
  }

  private void onGasLimit(BigInteger limit) {
    gasLimitText.setText(limit.toString());

    updateNetworkFee();
  }

  private void updateNetworkFee() {
    BigDecimal fee = BalanceUtils.weiToEth(viewModel.networkFee());
    String formattedFee = currencyFormatUtils.formatTransferCurrency(fee, WalletCurrency.ETHEREUM)
        + " "
        + C.ETH_SYMBOL;
    networkFeeText.setText(formattedFee);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.send_settings_menu, menu);

    return super.onCreateOptionsMenu(menu);
  }
}
