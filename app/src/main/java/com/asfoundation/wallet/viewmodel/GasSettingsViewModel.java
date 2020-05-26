package com.asfoundation.wallet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.asfoundation.wallet.entity.NetworkInfo;
import com.asfoundation.wallet.interact.FindDefaultNetworkInteract;
import java.math.BigDecimal;
import java.math.BigInteger;

public class GasSettingsViewModel extends BaseViewModel {

  public static final int SET_GAS_SETTINGS = 1;

  private FindDefaultNetworkInteract findDefaultNetworkInteract;

  private MutableLiveData<BigInteger> gasPrice = new MutableLiveData<>();
  private MutableLiveData<BigInteger> gasLimit = new MutableLiveData<>();
  private MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();

  GasSettingsViewModel(FindDefaultNetworkInteract findDefaultNetworkInteract) {
    this.findDefaultNetworkInteract = findDefaultNetworkInteract;
    gasPrice.setValue(BigInteger.ZERO);
    gasLimit.setValue(BigInteger.ZERO);
  }

  public void prepare() {
    disposable = findDefaultNetworkInteract.find()
        .subscribe(this::onDefaultNetwork, this::onError);
  }

  public MutableLiveData<BigInteger> gasPrice() {
    return gasPrice;
  }

  public MutableLiveData<BigInteger> gasLimit() {
    return gasLimit;
  }

  public LiveData<NetworkInfo> defaultNetwork() {
    return defaultNetwork;
  }

  private void onDefaultNetwork(NetworkInfo networkInfo) {
    defaultNetwork.setValue(networkInfo);
  }

  public BigDecimal networkFee() {
    return new BigDecimal(gasPrice.getValue()
        .multiply(gasLimit.getValue()));
  }
}
