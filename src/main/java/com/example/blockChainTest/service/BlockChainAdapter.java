package com.example.blockChainTest.service;

import java.math.BigInteger;

public interface BlockChainAdapter {
    String deployContract ();
    String checkContract(Integer contractNumber);
    void setContractStoredValue(String contractAddress, Integer newStoredValue);
    BigInteger getContractStoredValue(String contractAddress);
}
