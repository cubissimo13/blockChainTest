package com.example.blockChainTest.service;

import com.example.blockChainTest.generated.SimpleStorage;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class BlockChainAdapterImpl implements BlockChainAdapter {
    private Web3j web3j;
    private static final String INFURA_TOKEN = "https://kovan.infura.io/a5c7f2a097f74bfdb3e001b1fc27cc50";
    private static final String PASSWORD = "testblockchain";
    private static final String WALLET_FILE_PATH = "wallet";
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(30_000_000_000L);
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000L);
    private static final ContractGasProvider contractGasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
    private Map<Integer, CompletableFuture<SimpleStorage>> deployedContractBase = new HashMap<>();
    private Integer contractNumberCounter = 0;

    public BlockChainAdapterImpl() {
        this.web3j = Web3j.build(new HttpService(INFURA_TOKEN));
    }

    @Override
    public String deployContract() {
        CompletableFuture<SimpleStorage> deployedContract = SimpleStorage.deploy(web3j, getCredentials(), contractGasProvider, BigInteger.ONE).sendAsync();
        deployedContractBase.put(++contractNumberCounter, deployedContract);
        return "Smart contract number " + contractNumberCounter + " is deployed, wait for transaction complete";
    }

    @Override
    public String checkContract(Integer contractNumber) {
        CompletableFuture<SimpleStorage> awaitContract = deployedContractBase.get(contractNumber);
        if (awaitContract == null) {
            return "There is no contract with provided number " + contractNumber.toString();
        }
        if (awaitContract.isDone()) {
            try {
                return awaitContract.get().getContractAddress();
            } catch (Exception e) {
                throw new IllegalStateException("Error while deploying contract");
            }
        }
        return "Transaction is not complete";
    }

    @Override
    public void setContractStoredValue(String contractAddress, Integer newStoredValue) {
        if (contractAddress == null || contractAddress.isEmpty()) {
            throw new IllegalArgumentException("IllegalContractAddress");
        }
        try {
            loadContract(contractAddress).set(BigInteger.valueOf(newStoredValue)).send();
        } catch (Exception e) {
            throw new IllegalStateException("Error while set stored value");
        }
    }

    @Override
    public BigInteger getContractStoredValue(String contractAddress) {
        if (contractAddress == null || contractAddress.isEmpty()) {
            throw new IllegalArgumentException("IllegalContractAddress");
        }
        try {
            return loadContract(contractAddress).get().send();
        } catch (Exception e) {
            throw new IllegalStateException("Error while get stored value");
        }
    }

    private SimpleStorage loadContract(String contractAddress) {
        return SimpleStorage.load(contractAddress, web3j, getCredentials(), contractGasProvider);
    }

    private Credentials getCredentials()  {
        try {
            return WalletUtils.loadCredentials(PASSWORD, WALLET_FILE_PATH);
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad credential", e);
        }
    }
}
