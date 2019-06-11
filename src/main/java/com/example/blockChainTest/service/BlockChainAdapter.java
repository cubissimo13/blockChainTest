package com.example.blockChainTest.service;

import java.math.BigInteger;

public interface BlockChainAdapter {
    String deployContract ();
    String checkContract(Integer contractNumber);
    String sendTransactionForChangeContractStoredValue(String contractAddress, Integer newStoredValue);
    String checkTransactionComplete(Integer transactionNumber);
    BigInteger getContractStoredValue(String contractAddress);
}