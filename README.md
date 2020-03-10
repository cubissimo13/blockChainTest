# blockChain
test etherium \

============Deploy SmartContract============= \
POST http://localhost:8080/createSmartContract #For create SmartContract <br>
GET http://localhost:8080/checkSmartContract?contractNumber=*your_contract_number* #For check complete and get address <br>

=========Interact with SmartContract========= \
GET http://localhost:8080/getSmartContractValue?contractAddress=*your_contract_address* #For get value from smart contract <br>
POST http://localhost:8080/setSmartContractValue?contractAddress=*your_contract_address*&newValue=*your_new_value* #For set value to contract <br>
GET http://localhost:8080/checkChangeContractTransaction?transactionNumber=*your_transact_number* #Check transaction status