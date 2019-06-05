package com.example.blockChainTest.controller;

import com.example.blockChainTest.service.BlockChainAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmartContractController {
    private final BlockChainAdapter blockChainAdapter;

    @Autowired
    public SmartContractController(BlockChainAdapter blockChainAdapter) {
        this.blockChainAdapter = blockChainAdapter;
    }

    @GetMapping("/")
    public String hello() {
        return "Hi, I'm here! Check readme for use this api";
    }

    @PostMapping("/createSmartContract")
    public String createSmartContract() {
        return blockChainAdapter.deployContract();
    }

    @GetMapping("/checkSmartContract")
    public String getStatusOfContract(Integer contractNumber) {
        if (contractNumber == null) {
            return "Please provide valid contractNumber";
        }
        return blockChainAdapter.checkContract(contractNumber);
    }

    @GetMapping("/getSmartContractValue")
    public String getStoredValue(String contractAddress) {
        return blockChainAdapter.getContractStoredValue(contractAddress).toString();
    }

    @PostMapping("/setSmartContractValue")
    public String setStoredValue(String contractAddress, Integer newValue) {
        blockChainAdapter.setContractStoredValue(contractAddress, newValue);
        return "New value is saved";
    }
}
