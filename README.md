# blockChain
test etherium \

============Deploy SmartContract============= \
POST http://localhost:8080/createSmartContract #For create SmartContract \
GET http://localhost:8080/checkSmartContract #For check complete and get address \

=========Interact with SmartContract========= \
GET http://localhost:8080/getSmartContractValue?contractAddress="your_contract_address" #For get value from smart contract \
POST http://localhost:8080/setSmartContractValue?contractAddress="your_contract_address"&newValue="your_new_value" #For set value to contract