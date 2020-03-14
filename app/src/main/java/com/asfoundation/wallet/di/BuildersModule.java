package com.asfoundation.wallet.di;

import com.asfoundation.wallet.advertise.AdvertisingService;
import com.asfoundation.wallet.advertise.WalletPoAService;
import com.asfoundation.wallet.fcm.FcmService;
import com.asfoundation.wallet.billing.adyen.AdyenPaymentFragment;
import com.asfoundation.wallet.permissions.manage.view.PermissionsListFragment;
import com.asfoundation.wallet.permissions.request.view.CreateWalletFragment;
import com.asfoundation.wallet.permissions.request.view.PermissionFragment;
import com.asfoundation.wallet.permissions.request.view.PermissionsActivity;
import com.asfoundation.wallet.promotions.PromotionsFragment;
import com.asfoundation.wallet.referrals.InviteFriendsActivity;
import com.asfoundation.wallet.referrals.InviteFriendsFragment;
import com.asfoundation.wallet.referrals.InviteFriendsVerificationFragment;
import com.asfoundation.wallet.referrals.ReferralsFragment;
import com.asfoundation.wallet.topup.TopUpActivity;
import com.asfoundation.wallet.topup.TopUpFragment;
import com.asfoundation.wallet.topup.TopUpSuccessFragment;
import com.asfoundation.wallet.topup.payment.AdyenTopUpFragment;
import com.asfoundation.wallet.ui.BaseActivity;
import com.asfoundation.wallet.ui.ConfirmationActivity;
import com.asfoundation.wallet.ui.Erc681Receiver;
import com.asfoundation.wallet.ui.GasSettingsActivity;
import com.asfoundation.wallet.ui.ImportWalletActivity;
import com.asfoundation.wallet.ui.MyAddressActivity;
import com.asfoundation.wallet.ui.OneStepPaymentReceiver;
import com.asfoundation.wallet.ui.SendActivity;
import com.asfoundation.wallet.ui.SettingsActivity;
import com.asfoundation.wallet.ui.SplashActivity;
import com.asfoundation.wallet.ui.TransactionsActivity;
import com.asfoundation.wallet.ui.UpdateRequiredActivity;
import com.asfoundation.wallet.ui.WalletsActivity;
import com.asfoundation.wallet.ui.airdrop.AirdropFragment;
import com.asfoundation.wallet.ui.balance.BalanceFragment;
import com.asfoundation.wallet.ui.balance.TokenDetailsActivity;
import com.asfoundation.wallet.ui.balance.TransactionDetailActivity;
import com.asfoundation.wallet.ui.bazarchesettings.BazaarcheSettingsFragment;
import com.asfoundation.wallet.ui.catalog.CatalogActivity;
import com.asfoundation.wallet.ui.gamification.HowItWorksFragment;
import com.asfoundation.wallet.ui.gamification.MyLevelFragment;
import com.asfoundation.wallet.ui.iab.AppcoinsRewardsBuyFragment;
import com.asfoundation.wallet.ui.iab.BillingWebViewFragment;
import com.asfoundation.wallet.ui.iab.EarnAppcoinsFragment;
import com.asfoundation.wallet.ui.iab.IabActivity;
import com.asfoundation.wallet.ui.iab.IabUpdateRequiredFragment;
import com.asfoundation.wallet.ui.iab.LocalPaymentFragment;
import com.asfoundation.wallet.ui.iab.MergedAppcoinsFragment;
import com.asfoundation.wallet.ui.iab.OnChainBuyFragment;
import com.asfoundation.wallet.ui.iab.PaymentMethodsFragment;
import com.asfoundation.wallet.ui.iab.WebViewActivity;
import com.asfoundation.wallet.ui.iab.bazaariab.BazaarIabFragment;
import com.asfoundation.wallet.ui.iab.share.SharePaymentLinkFragment;
import com.asfoundation.wallet.ui.onboarding.OnboardingActivity;
import com.asfoundation.wallet.ui.transact.AppcoinsCreditsTransferSuccessFragment;
import com.asfoundation.wallet.ui.transact.TransferFragment;
import com.asfoundation.wallet.wallet_blocked.WalletBlockedActivity;
import com.asfoundation.wallet.wallet_validation.generic.CodeValidationFragment;
import com.asfoundation.wallet.wallet_validation.generic.PhoneValidationFragment;
import com.asfoundation.wallet.wallet_validation.generic.WalletValidationActivity;
import com.asfoundation.wallet.wallet_validation.poa.PoaCodeValidationFragment;
import com.asfoundation.wallet.wallet_validation.poa.PoaPhoneValidationFragment;
import com.asfoundation.wallet.wallet_validation.poa.PoaValidationLoadingFragment;
import com.asfoundation.wallet.wallet_validation.poa.PoaValidationSuccessFragment;
import com.asfoundation.wallet.wallet_validation.poa.PoaWalletValidationActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module abstract class BuildersModule {
  @ActivityScope @ContributesAndroidInjector(modules = SplashModule.class)
  abstract SplashActivity bindSplashModule();

  @ActivityScope @ContributesAndroidInjector() abstract BaseActivity bindBaseActivityModule();

  @ActivityScope @ContributesAndroidInjector(modules = AccountsManageModule.class)
  abstract WalletsActivity bindManageWalletsModule();

  @ActivityScope @ContributesAndroidInjector abstract ImportWalletActivity bindImportWalletModule();

  @ActivityScope @ContributesAndroidInjector(modules = TransactionsModule.class)
  abstract TransactionsActivity bindTransactionsModule();

  @ActivityScope @ContributesAndroidInjector(modules = TransactionDetailModule.class)
  abstract TransactionDetailActivity bindTransactionDetailModule();

  @ActivityScope @ContributesAndroidInjector(modules = SettingsModule.class)
  abstract SettingsActivity bindSettingsModule();

  @ActivityScope @ContributesAndroidInjector(modules = SendModule.class)
  abstract SendActivity bindSendModule();

  @ActivityScope @ContributesAndroidInjector(modules = MyAddressModule.class)
  abstract MyAddressActivity bindMyAddressModule();

  @ActivityScope @ContributesAndroidInjector abstract PermissionsActivity bindPermissionsActivity();

  @ActivityScope @ContributesAndroidInjector(modules = ConfirmationModule.class)
  abstract ConfirmationActivity bindConfirmationModule();

  @ActivityScope @ContributesAndroidInjector(modules = ConfirmationModule.class)
  abstract IabActivity bindIabModule();

  @ActivityScope @ContributesAndroidInjector(modules = GasSettingsModule.class)
  abstract GasSettingsActivity bindGasSettingsModule();

  @ActivityScope @ContributesAndroidInjector(modules = ConfirmationModule.class)
  abstract Erc681Receiver bindErc681Receiver();

  @ActivityScope @ContributesAndroidInjector(modules = ConfirmationModule.class)
  abstract OneStepPaymentReceiver bindOneStepPaymentReceiver();

  @ActivityScope @ContributesAndroidInjector abstract TopUpActivity bindTopUpActivity();

  @ActivityScope
  @ContributesAndroidInjector(modules = CatalogModule.class)
  abstract CatalogActivity bindCatalogModule();

  @ActivityScope @ContributesAndroidInjector abstract OnboardingActivity bindOnboardingModule();

  @ActivityScope @ContributesAndroidInjector
  abstract InviteFriendsActivity bindInviteFriendsActivity();

  @ContributesAndroidInjector() abstract WalletPoAService bindWalletPoAService();

  @ContributesAndroidInjector() abstract AirdropFragment bindAirdropFragment();

  @ContributesAndroidInjector() abstract OnChainBuyFragment bindRegularBuyFragment();

  @ContributesAndroidInjector() abstract HowItWorksFragment bindHowItWorksFragment();

  @ContributesAndroidInjector() abstract MyLevelFragment bindMyLevelFragment();

  @ContributesAndroidInjector() abstract BillingWebViewFragment bindWebViewFragment();

  @ContributesAndroidInjector() abstract WebViewActivity bindWebViewActivity();

  @ContributesAndroidInjector()
  abstract AppcoinsRewardsBuyFragment bindAppcoinsRewardsBuyFragment();

  @ContributesAndroidInjector() abstract PaymentMethodsFragment bindPaymentMethodsFragment();

  @ContributesAndroidInjector() abstract PermissionFragment bindPermissionFragment();

  @ContributesAndroidInjector() abstract CreateWalletFragment bindCreateWalletFragment();

  @ContributesAndroidInjector() abstract PermissionsListFragment bindPermissionsListFragment();

  @ContributesAndroidInjector(modules = ConfirmationModule.class)
  abstract TransferFragment bindTransactFragment();

  @ContributesAndroidInjector()
  abstract AppcoinsCreditsTransferSuccessFragment bindAppcoinsCreditsTransactSuccessFragment();

  @ContributesAndroidInjector() abstract TopUpFragment bindTopUpFragment();

  @ContributesAndroidInjector() abstract TopUpSuccessFragment bindTopUpSuccessFragment();

  @ContributesAndroidInjector() abstract SharePaymentLinkFragment bindSharePaymentLinkFragment();

  @ContributesAndroidInjector() abstract LocalPaymentFragment bindLocalPaymentFragment();

  @ContributesAndroidInjector() abstract MergedAppcoinsFragment bindMergedAppcoinsFragment();

  @ActivityScope @ContributesAndroidInjector abstract AdvertisingService bindAdvertisingService();

  @ContributesAndroidInjector abstract FcmService bindFcmService();

  @ActivityScope @ContributesAndroidInjector
  abstract PoaWalletValidationActivity bindPoaWalletValidationActivity();

  @ActivityScope @ContributesAndroidInjector
  abstract UpdateRequiredActivity bindUpdateRequiredActivity();

  @ContributesAndroidInjector()
  abstract PoaPhoneValidationFragment bindPoaPhoneValidationFragment();

  @ContributesAndroidInjector() abstract PoaCodeValidationFragment bindPoaCodeValidationFragment();

  @ContributesAndroidInjector()
  abstract PoaValidationLoadingFragment bindPoaValidationLoadingFragment();

  @ContributesAndroidInjector()
  abstract PoaValidationSuccessFragment bindPoaValidationSuccessFragment();

  @ContributesAndroidInjector() abstract BalanceFragment bindBalanceFragment();

  @ContributesAndroidInjector() abstract TokenDetailsActivity bindTokenDetailsFragment();

  @ActivityScope @ContributesAndroidInjector
  abstract WalletValidationActivity bindWalletValidationActivity();

  @ContributesAndroidInjector() abstract PhoneValidationFragment bindPhoneValidationFragment();

  @ContributesAndroidInjector() abstract CodeValidationFragment bindCodeValidationFragment();

  @ContributesAndroidInjector() abstract PromotionsFragment bindPromotionsFragment();

  @ContributesAndroidInjector()
  abstract InviteFriendsVerificationFragment bindInviteFriendsVerificationFragment();

  @ContributesAndroidInjector() abstract InviteFriendsFragment bindInviteFriendsFragment();

  @ContributesAndroidInjector() abstract ReferralsFragment bindReferralsFragment();

  @ContributesAndroidInjector() abstract EarnAppcoinsFragment bindEarnAppcoinsFragment();

  @ContributesAndroidInjector(modules = IabModule.class)
  abstract BazaarIabFragment bazaarIabFragment();

  @ContributesAndroidInjector(modules = SettingsFragmentModule.class)
  abstract BazaarcheSettingsFragment bazaarcheSettingsFragment();

  @ContributesAndroidInjector() abstract IabUpdateRequiredFragment bindIabUpdateRequiredFragment();

  @FragmentScope @ContributesAndroidInjector()
  abstract AdyenPaymentFragment bindAdyenPaymentFragment();

  @FragmentScope @ContributesAndroidInjector() abstract AdyenTopUpFragment bindAdyenTopUpFragment();

  @ActivityScope @ContributesAndroidInjector()
  abstract WalletBlockedActivity walletBlockedActivity();
}
