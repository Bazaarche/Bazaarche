package com.asf.wallet;

import android.content.Context;
import android.util.Log;
import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.asfoundation.wallet.entity.Wallet;
import com.asfoundation.wallet.service.AccountKeystoreService;
import com.asfoundation.wallet.service.KeyStoreFileManager;
import com.asfoundation.wallet.service.Web3jKeystoreAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class) public class GetKeystoreWalletRepoTest {
  private static final String STORE_1 = "{\"address\":\"eb1a948c6cc57fedf9271626404fc04a74ddd1e6\","
      + "\"crypto\":{\"cipher\":\"aes-128-ctr\","
      + "\"ciphertext\":\"6f6ba0b047f191f01df175255d0ef1eaf687905b3c22f9975d4cdec76f266d1e\","
      + "\"cipherparams\":{\"iv\":\"289195567cced5b5e6c8b18158c5f2ec\"},\"kdf\":\"scrypt\","
      + "\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,"
      + "\"salt\":\"243df82bdd2569ecf5da25fd9db21cf5857be99ed64c7e664432f5ebef626ebe\"},"
      + "\"mac\":\"87313234721b61a2c58b0d89f44847ea01df52c96fd5e3c8855efa0ecfd7ee06\"},"
      + "\"id\":\"3cb467fc-7f98-435f-98e3-7f660e0368cc\",\"version\":3}";
  private static final String PASS_1 = "1234";
  private static final String ADDRESS_1 = "0xeb1a948c6cc57fedf9271626404fc04a74ddd1e6";

  private AccountKeystoreService accountKeystoreService;
  private TestScheduler scheduler;

  @Before public void setUp() {
    Context context = InstrumentationRegistry.getTargetContext();
    scheduler = new TestScheduler();
    accountKeystoreService = new Web3jKeystoreAccountService(
        new KeyStoreFileManager(new File(context.getFilesDir(), "store").getAbsolutePath(),
            new ObjectMapper()), scheduler, new ObjectMapper());
  }

  @Test public void testCreateAccount() {
    TestObserver<Wallet> subscriber = new TestObserver<>();
    accountKeystoreService.createAccount("1234")
        .toObservable()
        .subscribe(subscriber);
    scheduler.triggerActions();
    subscriber.awaitTerminalEvent();
    subscriber.assertComplete();
    subscriber.assertNoErrors();

    assertEquals(subscriber.valueCount(), 1);
    deleteAccountStore(subscriber.values()
        .get(0).address, "1234");
  }

  @Test public void testImportStore() {
    TestObserver<Wallet> subscriber = accountKeystoreService.importKeystore(STORE_1, PASS_1, PASS_1)
        .toObservable()
        .test();
    scheduler.triggerActions();
    subscriber.awaitTerminalEvent();
    subscriber.assertComplete();
    subscriber.assertNoErrors();

    subscriber.assertOf(accountTestObserver -> {
      assertEquals(accountTestObserver.valueCount(), 1);
      assertEquals(accountTestObserver.values()
          .get(0).address, ADDRESS_1);
      assertTrue(accountTestObserver.values()
          .get(0)
          .sameAddress(ADDRESS_1));
    });
    deleteAccountStore(ADDRESS_1, PASS_1);
  }

  @Test public void testDeleteStore() {
    importAccountStore(STORE_1, PASS_1);
    TestObserver<Object> subscriber = new TestObserver<>();
    accountKeystoreService.deleteAccount(ADDRESS_1, PASS_1)
        .subscribe(subscriber);
    scheduler.triggerActions();
    subscriber.awaitTerminalEvent();
    subscriber.assertComplete();
    TestObserver<Wallet[]> accountListSubscriber = accountList();
    scheduler.triggerActions();
    accountListSubscriber.awaitTerminalEvent();
    accountListSubscriber.assertComplete();
    assertEquals(accountListSubscriber.valueCount(), 1);
    assertEquals(accountListSubscriber.values()
        .get(0).length, 0);
  }

  @Test public void testFetchAccounts() {
    List<Wallet> createdWallets = new ArrayList<>();
    System.out.println("creating accounts");
    for (int i = 0; i < 100; i++) {
      createdWallets.add(createAccountStore());
    }
    System.out.println("Accounts created: " + createdWallets.size());
    TestObserver<Wallet[]> subscriber = new TestObserver<>();
    accountKeystoreService.fetchAccounts()
        .subscribe(subscriber);

    scheduler.triggerActions();
    subscriber.awaitTerminalEvent();
    subscriber.assertComplete();
    assertEquals(subscriber.valueCount(), 1);
    int length = subscriber.values()
        .get(0).length;
    System.out.println("length: " + length);
    assertEquals(100, length);

    Wallet[] wallets = subscriber.values()
        .get(0);

    for (int i = 0; i < 100; i++) {
      assertTrue(createdWallets.get(i)
          .sameAddress(wallets[i].address));
    }
    for (Wallet wallet : createdWallets) {
      deleteAccountStore(wallet.address, PASS_1);
    }
  }

  @Test public void testExportAccountStore() {
    importAccountStore(STORE_1, PASS_1);
    TestObserver<String> subscriber =
        accountKeystoreService.exportAccount(new Wallet(ADDRESS_1), PASS_1, PASS_1)
            .test();
    scheduler.triggerActions();
    subscriber.awaitTerminalEvent();
    subscriber.assertComplete();
    assertEquals(subscriber.valueCount(), 1);
    Log.d("EXPORT_ACC", "Val: " + subscriber.values()
        .get(0));
    String val = subscriber.values()
        .get(0);
    try {
      JSONObject json = new JSONObject(val);
      assertTrue(("0x" + json.getString("address")).equalsIgnoreCase(ADDRESS_1));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    deleteAccountStore(ADDRESS_1, PASS_1);
  }

  private TestObserver<Wallet[]> accountList() {
    return accountKeystoreService.fetchAccounts()
        .test();
  }

  private void importAccountStore(String store, String password) {
    TestObserver<Wallet> subscriber =
        accountKeystoreService.importKeystore(store, password, password)
            .toObservable()
            .test();
    scheduler.triggerActions();
    subscriber.awaitTerminalEvent();
    subscriber.assertComplete();
  }

  private void deleteAccountStore(String address, String password) {
    TestObserver<Object> testObserver = new TestObserver<>();
    accountKeystoreService.deleteAccount(address, password)
        .subscribe(testObserver);
    scheduler.triggerActions();
  }

  private Wallet createAccountStore() {
    TestObserver<Wallet> subscriber = new TestObserver<>();
    accountKeystoreService.createAccount("1234")
        .subscribe(subscriber);
    scheduler.triggerActions();
    subscriber.assertComplete();
    assertEquals(subscriber.valueCount(), 1);
    return subscriber.values()
        .get(0);
  }
}
