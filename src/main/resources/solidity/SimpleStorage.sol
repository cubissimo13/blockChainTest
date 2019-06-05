pragma solidity ^0.5.1;
contract SimpleStorage {
    uint storedData;
    constructor(uint x) public {
        storedData = x;
    }
    function set(uint x) public {
        storedData = x;
    }
    function get() public view returns (uint retVal)  {
        return storedData;
    }
}