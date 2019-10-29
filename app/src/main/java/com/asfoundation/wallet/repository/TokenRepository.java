package com.asfoundation.wallet.repository;

import com.asfoundation.wallet.entity.Token;
import com.asfoundation.wallet.entity.TokenInfo;
import com.asfoundation.wallet.entity.Wallet;
import com.asfoundation.wallet.interact.DefaultTokenProvider;
import io.reactivex.Single;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes2;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Numeric;

import static org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction;

public class TokenRepository implements TokenRepositoryType {

  private final DefaultTokenProvider defaultTokenProvider;
  private final Web3j web3j;

  public TokenRepository(Web3jProvider web3jProvider, DefaultTokenProvider defaultTokenProvider) {
    this.defaultTokenProvider = defaultTokenProvider;
    this.web3j = web3jProvider.get();
  }

  private static Function balanceOf(String owner) {
    return new Function("balanceOf", Collections.singletonList(new Address(owner)),
        Collections.singletonList(new TypeReference<Uint256>() {
        }));
  }

  public static byte[] createTokenTransferData(String to, BigDecimal tokenAmount) {
    List<Type> params = Arrays.asList(new Address(to), new Uint256(tokenAmount.toBigInteger()));
    List<TypeReference<?>> returnTypes = Collections.singletonList(new TypeReference<Bool>() {
    });
    Function function = new Function("transfer", params, returnTypes);
    String encodedFunction = FunctionEncoder.encode(function);
    return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction));
  }

  public static byte[] createTokenApproveData(String spender, BigDecimal amount) {
    List<Type> params = Arrays.asList(new Address(spender), new Uint256(amount.toBigInteger()));
    List<TypeReference<?>> returnTypes = Collections.singletonList(new TypeReference<Bool>() {
    });
    Function function = new Function("approve", params, returnTypes);
    String encodedFunction = FunctionEncoder.encode(function);
    return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction));
  }

  static byte[] buyData(String developerAddress, String storeAddress, String oemAddress,
      String data, BigDecimal amount, String tokenAddress, String packageName, byte[] countryCode) {
    Uint256 amountParam = new Uint256(amount.toBigInteger());
    Utf8String packageNameType = new Utf8String(packageName);
    Utf8String dataParam = data == null ? new Utf8String("") : new Utf8String(data);
    Address contractAddress = new Address(tokenAddress);
    Address developerAddressParam = new Address(developerAddress);
    Address storeAddressParam = new Address(storeAddress);
    Address oemAddressParam = new Address(oemAddress);
    Bytes2 countryCodeBytes = new Bytes2(countryCode);
    List<Type> params = Arrays.asList(packageNameType, dataParam, amountParam, contractAddress,
        developerAddressParam, storeAddressParam, oemAddressParam, countryCodeBytes);
    List<TypeReference<?>> returnTypes = Collections.singletonList(new TypeReference<Bool>() {
    });
    Function function = new Function("buy", params, returnTypes);
    String encodedFunction = FunctionEncoder.encode(function);
    return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction));
  }

  @Override public Single<Token> getAppcBalance(@NotNull Wallet wallet) {
    return defaultTokenProvider.getDefaultToken()
        .map(tokenInfo -> new Token(tokenInfo, getBalance(wallet, tokenInfo)));
  }

  private BigDecimal getBalance(Wallet wallet, TokenInfo tokenInfo) throws Exception {
    Function function = balanceOf(wallet.address);
    String responseValue = callSmartContractFunction(function, tokenInfo.address, wallet.address);

    List<Type> response =
        FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
    if (response.size() == 1) {
      return new BigDecimal(((Uint256) response.get(0)).getValue());
    } else {
      return null;
    }
  }

  private String callSmartContractFunction(Function function, String contractAddress,
      String walletAddress) throws Exception {
    String encodedFunction = FunctionEncoder.encode(function);
    org.web3j.protocol.core.methods.request.Transaction transaction =
        createEthCallTransaction(walletAddress, contractAddress, encodedFunction);
    return web3j.ethCall(transaction, DefaultBlockParameterName.LATEST)
        .send()
        .getValue();
  }
}
