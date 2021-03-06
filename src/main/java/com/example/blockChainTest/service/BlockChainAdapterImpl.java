package com.example.blockChainTest.service;

import com.example.blockChainTest.generated.SimpleStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class BlockChainAdapterImpl implements BlockChainAdapter {
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(30_000_000_000L);
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000L);
    private Integer contractNumberCounter = 0;
    private Integer transactionNumberCounter = 0;
    @Value("${smart.contract.wallet.file.password}")
    private String password;
    @Value("${smart.contract.wallet.file.path}")
    private String walletFilePath;
    private ContractGasProvider contractGasProvider;
    private Web3j web3j;
    private Map<Integer, CompletableFuture<SimpleStorage>> deployedContractBase;
    private Map<Integer, CompletableFuture<TransactionReceipt>> deployedTransactionsBase;

    public BlockChainAdapterImpl(@Value("${smart.contract.infura.token}") String infuraToken) {
        this.web3j = Web3j.build(new HttpService(infuraToken));
        this.deployedContractBase = new HashMap<>();
        this.deployedTransactionsBase = new HashMap<>();
        this.contractGasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
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
            return "There is no contract with provided number " + contractNumber;
        }
        if (awaitContract.isDone()) {
            try {
                return awaitContract.get().getContractAddress();
            } catch (Exception e) {
                throw new IllegalStateException("Error while deploying contract", e);
            }
        }
        return "Transaction is not complete";
    }

    @Override
    public String sendTransactionForChangeContractStoredValue(String contractAddress, Integer newStoredValue) {
        if (StringUtils.isEmpty(contractAddress)) {
            throw new IllegalArgumentException("IllegalContractAddress");
        }
        CompletableFuture<TransactionReceipt> transaction = loadContract(contractAddress).set(BigInteger.valueOf(newStoredValue)).sendAsync();
        deployedTransactionsBase.put(++transactionNumberCounter, transaction);
        return "Transaction number " + transactionNumberCounter + " is sended, wait for transaction complete";
    }

    @Override
    public String checkTransactionComplete(Integer transactionNumber) {
        CompletableFuture<TransactionReceipt> awaitTransaction = deployedTransactionsBase.get(transactionNumber);
        if (awaitTransaction == null) {
            return "There is no transaction with provided number " + transactionNumber;
        }
        if (awaitTransaction.isCompletedExceptionally()) {
            deployedTransactionsBase.remove(transactionNumber);
            return "Transaction has error. Please try again";
        } else if (awaitTransaction.isDone()) {
            return "Transaction completed";
        } else {
            return "Transaction is not complete";
        }
    }

    @Override
    public BigInteger getContractStoredValue(String contractAddress) {
        if (StringUtils.isEmpty(contractAddress)) {
            throw new IllegalArgumentException("IllegalContractAddress");
        }
        try {
            return loadContract(contractAddress).get().send();
        } catch (Exception e) {
            throw new IllegalStateException("Error while get stored value", e);
        }
    }

    private SimpleStorage loadContract(String contractAddress) {
        return SimpleStorage.load(contractAddress, web3j, getCredentials(), contractGasProvider);
    }

    private Credentials getCredentials() {
        try {
            return WalletUtils.loadCredentials(password, walletFilePath);
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad credential", e);
        }
    }
}
